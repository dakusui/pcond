package com.github.dakusui.pcond.validator;

import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.Evaluator.Entry.Type.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public interface ReportComposer {
  default Explanation explanationFromMessage(String msg) {
    return Explanation.fromMessage(msg);
  }

  default Explanation composeExplanation(String message, List<Evaluator.Entry> result, Throwable t) {
    return Utils.composeExplanation(message, result, t);
  }

  /**
   * A default implementation of `ReportComposer`.
   */
  class Default implements ReportComposer {
  }

  enum Utils {
    ;

    static String composeReport(String summary, List<Object> details) {
      AtomicInteger index = new AtomicInteger(0);
      String ret = summary;
      ret += format("%n");
      if (details != null) {
        ret += format("%n");
        ret += details.stream()
            .map(Objects::toString)
            .map(each -> format(".Detail of failure [%s]%n", index.getAndIncrement())
                + format("----%n")
                + each + format("%n")
                + format("----%n"))
            .collect(joining(format("%n")));
      }
      return ret;
    }

    static Explanation composeExplanation(String message, List<Evaluator.Entry> result, Throwable t) {
      List<Object> expectationDetails = new LinkedList<>();
      List<Object> actualResultDetails = new LinkedList<>();
      String expectation = composeExplanationForExpectations(result, t, expectationDetails);
      String actualResult = composeExplanationForActualResults(result, t, actualResultDetails);
      //    assert expectationDetails.size() == actualResultDetails.size();
      return new Explanation(message, composeReport(expectation, expectationDetails), composeReport(actualResult, actualResultDetails)
      );
    }

    private static String composeExplanationForActualResults(List<Evaluator.Entry> result, Throwable t, List<Object> actualInputDetails) {
      return composeExplanation(result.stream()
          .peek((Evaluator.Entry each) -> {
            if (each.hasActualInputDetail())
              actualInputDetails.add(each.actualInputDetail());
          })
          .filter((Evaluator.Entry each) -> !each.isTrivial())
          .map((Evaluator.Entry each) -> evaluatorEntryToFormattedEntry(
              each,
              () -> each.hasOutput() ?
                  InternalUtils.formatObject(each.output()) :
                  InternalUtils.formatObject(t)))
          .collect(toList()));
    }

    private static String composeExplanationForExpectations(List<Evaluator.Entry> result, Throwable t, List<Object> expectationDetails) {
      return composeExplanation(result.stream()
          .filter((Evaluator.Entry each) -> !each.isTrivial())
          .map((Evaluator.Entry each) -> evaluatorEntryToFormattedEntry(
              each,
              () -> (each.hasOutput() ?
                  InternalUtils.formatObject(each.output() instanceof Boolean ? each.expectedBooleanValue() : each.output()) :
                  InternalUtils.formatObject(t))))
          .peek((FormattedEntry each) -> {
            Optional<Object> formSnapshot = each.mismatchExplanation();
            formSnapshot.ifPresent(expectationDetails::add);
          })
          .collect(toList()));
    }

    private static String composeExplanation(List<FormattedEntry> formattedEntries) {
      AtomicInteger mismatchExplanationCount = new AtomicInteger(0);
      boolean mismatchExplanationFound = formattedEntries
          .stream()
          .anyMatch(e -> e.mismatchExplanation().isPresent());
      return evaluatorEntriesToString(
          squashFormattedEntriesWherePossible(formattedEntries),
          columnLengths -> formattedEntryToString(
              columnLengths[0], columnLengths[1], columnLengths[2],
              mismatchExplanationCount, mismatchExplanationFound));
    }

    private static FormattedEntry evaluatorEntryToFormattedEntry(Evaluator.Entry entry, Supplier<String> outputFormatter) {
      return new FormattedEntry(
          InternalUtils.formatObject(entry.input()),
          entry.formName(),
          entry.level() == 0 ?
              "" :
              format("%" + (entry.level() * 2) + "s", ""),
          !asList(LEAF, AND, OR, NOT, FUNCTION).contains(entry.type()) ?
              null :
              outputFormatter.get(),
          entry.hasExpectationDetail() ? entry.expectationDetail() : null);
    }

    private static Function<FormattedEntry, String> formattedEntryToString(
        int inputColumnWidth, int formNameColumnLength, int outputColumnLength,
        AtomicInteger i, boolean mismatchExplanationFound) {
      return (FormattedEntry formattedEntry) ->
          (mismatchExplanationFound ?
              format("%-4s", formattedEntry.mismatchExplanation().isPresent() ?
                  "[" + i.getAndIncrement() + "]" : "") :
              "") +
              format("%-" + Math.max(2, inputColumnWidth) + "s" +
                      "%-" + (formNameColumnLength + 2) + "s" +
                      "%-" + Math.max(2, outputColumnLength) + "s",
                  formattedEntry.input().orElse(""),
                  formattedEntry.input().map(v -> "->").orElse("  ") + formattedEntry.indent() + formattedEntry.formName(),
                  formattedEntry.output().map(v -> "->" + v).orElse(""));
    }

    private static String evaluatorEntriesToString(List<FormattedEntry> formattedEntries, Function<int[], Function<FormattedEntry, String>> formatterFactory) {
      int maxInputLength = 0, maxIndentAndFormNameLength = 0, maxOutputLength = 0;
      for (FormattedEntry eachEntry : formattedEntries) {
        int inputLength = eachEntry.input().map(String::length).orElse(0);
        if (inputLength > maxInputLength)
          maxInputLength = inputLength;
        int inputAndFormNameLength = eachEntry.indent().length() + eachEntry.formName().length();
        if (inputAndFormNameLength > maxIndentAndFormNameLength)
          maxIndentAndFormNameLength = inputAndFormNameLength;
        int outputLength = eachEntry.output().map(String::length).orElse(0);
        if (outputLength > maxOutputLength)
          maxOutputLength = outputLength;
      }
      Function<FormattedEntry, String> formatter = formatterFactory.apply(new int[] { maxInputLength, maxIndentAndFormNameLength, maxOutputLength });
      AtomicReference<FormattedEntry> lastFormattedEntry = new AtomicReference<>();
      return formattedEntries
          .stream()
          .map(formatter)
          .map(s -> ("+" + s).trim().substring(1))
          .collect(joining(format("%n")));
    }

    private static List<FormattedEntry> squashFormattedEntriesWherePossible(List<FormattedEntry> formattedEntries) {
      AtomicReference<FormattedEntry> lastFormattedEntry = new AtomicReference<>();
      AtomicReference<FormattedEntry> curHolder = new AtomicReference<>();
      return Stream.concat(
              formattedEntries
                  .stream()
                  .map((FormattedEntry eachEntry) -> hideRedundantInputValues(lastFormattedEntry, eachEntry)),
              Stream.of(FormattedEntry.SENTINEL))
          .map(each -> squashFormattedEntries(curHolder, each))
          .map(each -> flushRemainder(curHolder, each))
          .filter(Objects::nonNull)
          .collect(toList());
    }

    private static FormattedEntry flushRemainder(AtomicReference<FormattedEntry> curHolder, FormattedEntry each) {
      if (each == FormattedEntry.SENTINEL)
        return curHolder.get();
      else
        return each;
    }

    private static FormattedEntry squashFormattedEntries(AtomicReference<FormattedEntry> curHolder, FormattedEntry each) {
      final FormattedEntry cur = curHolder.get();
      if (cur == null) {
        if (!each.output().isPresent()) {
          curHolder.set(each);
          return null;
        } else {
          return each;
        }
      } else {
        if (!cur.output().isPresent() && !each.input().isPresent()) {
          curHolder.set(new FormattedEntry(
              cur.input().orElse(null),
              String.format("%s:%s", cur.formName(), each.formName()),
              cur.indent(),
              each.output().orElse(null),
              cur.mismatchExplanation().orElse(null)));
          return null;
        } else {
          curHolder.set(each);
          return cur;
        }
      }
    }

    private static FormattedEntry hideRedundantInputValues(AtomicReference<FormattedEntry> lastFormattedEntry, FormattedEntry eachEntry) {
      FormattedEntry lastEntry = lastFormattedEntry.get();
      lastFormattedEntry.set(eachEntry);
      if (lastEntry == null)
        return eachEntry;
      if (Objects.equals(lastEntry.input, eachEntry.input))
        return new FormattedEntry(null, eachEntry.formName, eachEntry.indent(), eachEntry.output, eachEntry.mismatchExplanation);
      return eachEntry;
    }

    public static class FormattedEntry {
      private static final FormattedEntry SENTINEL = new FormattedEntry(null, null, null, null, null);
      private final        String         input;
      private final        String         formName;
      private final        String         indent;
      private final        String         output;
      private final        Object         mismatchExplanation;

      FormattedEntry(String input, String formName, String indent, String output, Object mismatchExplanation) {
        this.input = input;
        this.formName = formName;
        this.indent = indent;
        this.output = output;
        this.mismatchExplanation = mismatchExplanation;
      }

      Optional<String> input() {
        return Optional.ofNullable(this.input);
      }

      String indent() {
        return this.indent;
      }

      String formName() {
        return this.formName;
      }

      Optional<Object> mismatchExplanation() {
        return Optional.ofNullable(this.mismatchExplanation);
      }

      Optional<String> output() {
        return Optional.ofNullable(this.output);
      }
    }
  }
}
