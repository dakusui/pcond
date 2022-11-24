package com.github.dakusui.pcond.validator;

import com.github.dakusui.pcond.core.EvaluationEntry;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.*;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public interface ReportComposer {
  default Explanation explanationFromMessage(String msg) {
    return Explanation.fromMessage(msg);
  }

  default Explanation composeExplanation(String message, List<EvaluationEntry> result, Throwable t) {
    return Utils.composeExplanation(message, result);
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

    /**
     * Note that an exception thrown during an evaluation is normally caught by the framework.
     *
     * @param message           A message to be prepended to a summary.
     * @param evaluationHistory An "evaluation history" object represented as a list of evaluation entries.
     * @return An explanation object.
     */
    static Explanation composeExplanation(String message, List<EvaluationEntry> evaluationHistory) {
      List<Object> detailsForExpectation = new LinkedList<>();
      List<FormattedEntry> summaryDataForExpectations = squashTrivialEntries(evaluationHistory)
          .stream()
          .peek((EvaluationEntry each) -> addToDetailsListIfExplanationIsRequired(detailsForExpectation, each, each::detailOutputExpectation))
          .map(Utils::createFormattedEntryForExpectation)
          .collect(toList());
      String textSummaryForExpectations = composeSummaryForExpectations(summaryDataForExpectations);
      List<Object> detailsForActual = new LinkedList<>();
      List<FormattedEntry> summaryForActual = squashTrivialEntries(evaluationHistory)
          .stream()
          .peek((EvaluationEntry each) -> addToDetailsListIfExplanationIsRequired(detailsForActual, each, each::detailOutputActualValue))
          .map(Utils::createFormattedEntryForActualValue)
          .collect(toList());
      String textSummaryForActualResult = composeSummaryForActualResults(summaryForActual);
      return new Explanation(message,
          composeReport(textSummaryForExpectations, detailsForExpectation),
          composeReport(textSummaryForActualResult, detailsForActual));
    }

    private static List<EvaluationEntry> squashTrivialEntries(List<EvaluationEntry> evaluationHistory) {
      List<EvaluationEntry> ret = new LinkedList<>();
      List<EvaluationEntry> squashedItems = new LinkedList<>();
      for (EvaluationEntry each : evaluationHistory) {
        if (squashedItems.isEmpty()) {
          if (each.isTrivial()) {
            squashedItems.add(each);
          } else {
            ret.add(each);
          }
        } else {
          if (each.isTrivial()) {
            squashedItems.add(each);
          } else {
            squashedItems.add(each);
            EvaluationEntry first = squashedItems.get(0);
            EvaluationEntry last = squashedItems.get(squashedItems.size() - 1);
            ret.add(EvaluationEntry.create(
                squashedItems.stream().map(EvaluationEntry::formName).collect(joining(":")), first.type(),
                first.level(),
                first.inputExpectation(), first.detailInputExpectation(),
                first.outputExpectation(), first.detailOutputExpectation(),
                first.inputActualValue(), first.detailInputActualValue(),
                last.outputActualValue(), last.detailOutputActualValue(),
                false,
                squashedItems.stream().anyMatch(EvaluationEntry::wasExceptionThrown),
                squashedItems.stream().anyMatch(EvaluationEntry::requiresExplanation)));
            squashedItems.clear();
          }
        }
      }
      ret.addAll(squashedItems);
      return ret;
    }

    private static FormattedEntry createFormattedEntryForExpectation(EvaluationEntry each) {
      return new FormattedEntry(
          formatObject(each.inputExpectation()),
          each.formName(),
          indent(each.level()),
          formatObject(each.outputExpectation()),
          each.requiresExplanation());
    }

    private static FormattedEntry createFormattedEntryForActualValue(EvaluationEntry each) {
      return new FormattedEntry(
          formatObject(each.inputActualValue()),
          each.formName(),
          indent(each.level()),
          formatObject(each.outputActualValue()),
          each.requiresExplanation());
    }

    private static void addToDetailsListIfExplanationIsRequired(List<Object> detailsForExpectation, EvaluationEntry each, Supplier<Object> detailOutput) {
      if (each.requiresExplanation())
        detailsForExpectation.add(detailOutput.get());
    }

    static Report composeReport(String summary, List<Object> details) {
      List<String> stringFormDetails = details != null ?
          details.stream()
              .filter(Objects::nonNull)
              .map(Objects::toString)
              .collect(toList()) :
          emptyList();
      return ReportComposer.Report.create(summary, stringFormDetails);
    }

    private static String composeSummaryForActualResults(List<FormattedEntry> formattedEntries) {
      return composeSummary(formattedEntries);
    }

    private static String composeSummaryForExpectations(List<FormattedEntry> formattedEntries) {
      return composeSummaryForActualResults(formattedEntries);
    }

    private static String composeSummary(List<FormattedEntry> formattedEntries) {
      AtomicInteger mismatchExplanationCount = new AtomicInteger(0);
      boolean mismatchExplanationFound = formattedEntries
          .stream()
          .anyMatch(FormattedEntry::requiresExplanation);
      return evaluatorEntriesToString(
          squashFormattedEntriesWherePossible(formattedEntries),
          columnLengths -> formattedEntryToString(
              columnLengths[0],
              columnLengths[1],
              columnLengths[2],
              mismatchExplanationCount,
              mismatchExplanationFound));
    }

    private static Function<FormattedEntry, String> formattedEntryToString(
        int inputColumnWidth,
        int formNameColumnLength,
        int outputColumnLength,
        AtomicInteger i,
        boolean mismatchExplanationFound) {
      return (FormattedEntry formattedEntry) ->
          (mismatchExplanationFound ?
              format("%-4s", formattedEntry.requiresExplanation ?
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
              cur.requiresExplanation()));
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
        return new FormattedEntry(null, eachEntry.formName, eachEntry.indent(), eachEntry.output, eachEntry.requiresExplanation);
      return eachEntry;
    }

    public static class FormattedEntry {
      private static final FormattedEntry SENTINEL = new FormattedEntry(null, null, null, null, false);
      private final        String         input;
      private final        String         formName;
      private final        String         indent;
      private final        String         output;
      private final        boolean        requiresExplanation;

      FormattedEntry(String input, String formName, String indent, String output, boolean requiresExplanation) {
        this.input = input;
        this.formName = formName;
        this.indent = indent;
        this.output = output;
        this.requiresExplanation = requiresExplanation;
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

      Optional<String> output() {
        return Optional.ofNullable(this.output);
      }

      public boolean requiresExplanation() {
        return this.requiresExplanation;
      }
    }
  }
}
