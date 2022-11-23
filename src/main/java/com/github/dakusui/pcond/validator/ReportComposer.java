package com.github.dakusui.pcond.validator;

import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.Evaluator.Entry.Type.*;
import static com.github.dakusui.pcond.internals.InternalUtils.*;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
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

  interface Report {
    String summary();

    List<String> details();

    static Report create(String summary, List<String> details) {
      List<String> detailsCopy = unmodifiableList(new ArrayList<>(details));
      return new Report() {
        @Override
        public String summary() {
          return summary;
        }

        @Override
        public List<String> details() {
          return detailsCopy;
        }
      };
    }
  }

  enum Utils {
    ;

    static Explanation composeExplanation(String message, List<Evaluator.Entry> result, Throwable t) {
      List<Object> expectationDetails = new LinkedList<>();
      List<Object> actualResultDetails = new LinkedList<>();
      return new Explanation(message,
          composeReport(composeSummaryForExpectations(result, t, expectationDetails), expectationDetails),
          composeReport(composeSummaryForActualResults(result, t, actualResultDetails), actualResultDetails));
    }

    static Report composeReport(String summary, List<Object> details) {
      List<String> stringFormDetails = details != null ?
          details.stream()
              .map(Object::toString)
              .collect(toList()) :
          emptyList();
      return ReportComposer.Report.create(summary, stringFormDetails);
    }

    private static String composeSummaryForActualResults(List<Evaluator.Entry> result, Throwable t, List<Object> actualInputDetails) {
      return composeSummary(
          result.stream()
              .peek((Evaluator.Entry each) -> {
                if (each.hasActualInputDetail())
                  actualInputDetails.add(each.actualInputDetail());
              })
              .filter((Evaluator.Entry each) -> !each.isTrivial())
              .map((Evaluator.Entry each) -> evaluatorEntryToFormattedEntry(
                  each,
                  () -> each.evaluationFinished() ?
                      formatObject(each.output()) :
                      formatObject(t)))
              .collect(toList()));
    }

    private static String composeSummaryForExpectations(List<Evaluator.Entry> result, Throwable t, List<Object> expectationDetails) {
      return composeSummary(
          result.stream()
              .filter((Evaluator.Entry each) -> !each.isTrivial())
              .map((Evaluator.Entry each) -> evaluatorEntryToFormattedEntry(
                  each,
                  () -> composeExpectationSummaryRecordForEntry(each, t)))
              .peek((FormattedEntry each) -> {
                Optional<Object> formSnapshot = each.mismatchExplanation();
                formSnapshot.ifPresent(expectationDetails::add);
              })
              .collect(toList()));
    }

    private static String composeExpectationSummaryRecordForEntry(Evaluator.Entry entry, Throwable t) {
      return entry.evaluationFinished() ?
          formatObject(
              (entry.output() instanceof Boolean || entry.output() instanceof Throwable) ?
                  //              !asList(FUNCTION, TRANSFORM).contains(entry.type()) ?
                  entry.expectedBooleanValue() :
                  entry.output()) :
          formatObject(t);
    }

    private static String composeSummary(List<FormattedEntry> formattedEntries) {
      AtomicInteger mismatchExplanationCount = new AtomicInteger(0);
      boolean mismatchExplanationFound = formattedEntries
          .stream()
          .anyMatch(e -> e.mismatchExplanation().isPresent());
      return evaluatorEntriesToString(
          squashFormattedEntriesWherePossible(formattedEntries),
          columnLengths -> formattedEntryToString(
              columnLengths[0],
              columnLengths[1],
              columnLengths[2],
              mismatchExplanationCount,
              mismatchExplanationFound));
    }

    private static FormattedEntry evaluatorEntryToFormattedEntry(Evaluator.Entry entry, Supplier<String> outputFormatter) {
      return new FormattedEntry(
          formatObject(entry.input()),
          entry.formName(),
          indent(entry.level()),
          asList(LEAF, AND, OR, NOT, FUNCTION).contains(entry.type()) ?
              outputFormatter.get() :
              null,
          entry.hasExpectationDetail() ?
              entry.expectationDetail() :
              null);
    }

    private static Function<FormattedEntry, String> formattedEntryToString(
        int inputColumnWidth,
        int formNameColumnLength,
        int outputColumnLength,
        AtomicInteger i,
        boolean mismatchExplanationFound) {
      return (FormattedEntry formattedEntry) ->
          (mismatchExplanationFound ?
              format("%-4s", formattedEntry.mismatchExplanation().isPresent() ?
                  "[" + i.getAndIncrement() + "]" : "") :
              "") +
              format("%-" + max(2, inputColumnWidth) + "s" +
                      "%-" + (formNameColumnLength + 2) + "s" +
                      "%-" + max(2, outputColumnLength) + "s",
                  formattedEntry.input().orElse(""),
                  formattedEntry.input()
                      .map(v -> "->")
                      .orElse("  ") + formatObject(InternalUtils.toNonStringObject(formattedEntry.indent() + formattedEntry.formName()), formNameColumnLength - 2),
                  formattedEntry
                      .output()
                      .map(v -> "->" + v).orElse(""));
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
      int formNameColumnLength = (formNameColumnLength = max(12, min(summarizedStringLength(), maxIndentAndFormNameLength))) + formNameColumnLength % 2;
      Function<FormattedEntry, String> formatter = formatterFactory.apply(
          new int[] { maxInputLength, formNameColumnLength, maxOutputLength });
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

    private static FormattedEntry squashFormattedEntries(AtomicReference<FormattedEntry> curHolder, FormattedEntry entry) {
      final FormattedEntry cur = curHolder.get();
      if (cur == null) {
        if (!entry.output().isPresent()) {
          curHolder.set(entry);
          // null will be filtered out by the caller.
          return null;
        } else {
          return entry;
        }
      } else {
        if (!cur.output().isPresent() && !entry.input().isPresent()) {
          curHolder.set(new FormattedEntry(
              cur.input().orElse(null),
              String.format("%s:%s", cur.formName(), entry.formName()),
              cur.indent(),
              entry.output().orElse(null),
              cur.mismatchExplanation().orElse(entry.mismatchExplanation)));
          return null;
        } else {
          curHolder.set(entry);
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
