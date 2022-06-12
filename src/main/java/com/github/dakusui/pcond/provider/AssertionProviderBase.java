package com.github.dakusui.pcond.provider;

import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.core.Evaluator.Entry.Type.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public interface AssertionProviderBase extends AssertionProvider {
  ExceptionComposer exceptionComposer();

  MessageComposer messageComposer();

  ReportComposer reportComposer();

  @Override
  default <T> T requireNonNull(T value) {
    return checkValueAndThrowIfFails(value, Predicates.isNotNull(), this.messageComposer()::composeMessageForPrecondition, ExceptionFactory.from(NullPointerException::new));
  }

  @Override
  default <T> T requireArgument(T value, Predicate<? super T> cond) {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPrecondition, ExceptionFactory.from(IllegalArgumentException::new));
  }

  @Override
  default <T> T requireState(T value, Predicate<? super T> cond) {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPrecondition, ExceptionFactory.from(IllegalStateException::new));
  }

  @Override
  default <T, E extends Exception> T require(T value, Predicate<? super T> cond) throws E {
    return require(value, cond, this.<E>exceptionComposerForPrecondition());
  }

  @Override
  default <T, E extends Exception> T require(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPrecondition, ExceptionFactory.from(exceptionComposer));
  }

  @Override
  default <T, E extends Exception> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForValidation, ExceptionFactory.from(exceptionComposer));
  }


  @Override
  default <T> T ensureNonNull(T value) {
    return checkValueAndThrowIfFails(value, Predicates.isNotNull(), this.messageComposer()::composeMessageForPostcondition, ExceptionFactory.from(NullPointerException::new));
  }

  @Override
  default <T> T ensureState(T value, Predicate<? super T> cond) {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPostcondition, ExceptionFactory.from(IllegalStateException::new));
  }

  @Override
  default <T, E extends Exception> T ensure(T value, Predicate<? super T> cond) throws E {
    return ensure(value, cond, this.<E>exceptionComposerForPostcondition());
  }

  @Override
  default <T, E extends Exception> T ensure(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPostcondition, ExceptionFactory.from(exceptionComposer));
  }

  @Override
  default <T> void checkPrecondition(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPrecondition, AssertionError::new);
  }

  @Override
  default <T> void checkPostcondition(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPostcondition, AssertionError::new);
  }

  @Override
  default <T> void checkInvariant(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForAssertion, AssertionError::new);
  }

  @Override
  default <T> void assertThat(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForAssertion, this.exceptionComposer()::<Error>testFailedException);
  }

  @SuppressWarnings("RedundantTypeArguments")
  @Override
  default <T> void assumeThat(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForAssertion, this.exceptionComposer()::<RuntimeException>testSkippedException);
  }

  @SuppressWarnings("unchecked")
  default <E extends Exception> Function<String, E> exceptionComposerForPrecondition() {
    return message -> (E) new PreconditionViolationException(message);
  }

  @SuppressWarnings("unchecked")
  default <E extends Exception> Function<String, E> exceptionComposerForPostcondition() {
    return message -> (E) new PostconditionViolationException(message);
  }

  <T, E extends Throwable> T checkValue(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, Function<String, E> exceptionComposer) throws E;

  default <T, E extends Throwable> T checkValueAndThrowIfFails(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, ExceptionFactory<E> exceptionFactory) throws E {
    return checkValue(value, cond, messageComposer, msg -> exceptionFactory.apply(reportComposer().explanationFromMessage(msg)));
  }

  interface ExceptionFactory<E extends Throwable> extends Function<Explanation, E> {
    static <E extends Throwable> ExceptionFactory<E> from(Function<String, E> exceptionComposingFunction) {
      return explanation -> exceptionComposingFunction.apply(explanation.toString());
    }
  }

  interface ExceptionComposer {
    <T extends RuntimeException> T testSkippedException(String message);

    default <T extends RuntimeException> T testSkippedException(Explanation explanation) {
      return testSkippedException(explanation.toString());
    }

    <T extends Error> T testFailedException(String message);

    default <T extends Error> T testFailedException(Explanation explanation) {
      return testFailedException(explanation.toString());
    }
  }

  interface MessageComposer {
    <T> String composeMessageForPrecondition(T value, Predicate<? super T> predicate);

    <T> String composeMessageForPostcondition(T value, Predicate<? super T> predicate);

    <T> String composeMessageForAssertion(T t, Predicate<? super T> predicate);

    <T> String composeMessageForValidation(T t, Predicate<? super T> predicate);
  }

  interface ReportComposer {
    default Explanation explanationFromMessage(String msg) {
      return Explanation.fromMessage(msg);
    }

    default Explanation composeExplanation(String message, List<Evaluator.Entry> result, Throwable t) {
      return Utils.composeExplanation(message, result, t);
    }

    enum Utils {
      ;

      public static String composeReport(String summary, List<Object> details) {
        AtomicInteger index = new AtomicInteger(0);
        if (summary == null && details == null)
          return null;
        String ret = summary;
        ret += format("%n");
        if (details != null && !details.isEmpty()) {
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

      public static Explanation composeExplanation(String message, List<Evaluator.Entry> result, Throwable t) {
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
            .map((Evaluator.Entry each) -> evaluatorEntryToFormattedEntry(
                each,
                () -> each.hasOutput() ?
                    InternalUtils.formatObject(each.output()) :
                    InternalUtils.formatObject(t)))
            .collect(toList()));
      }

      private static String composeExplanationForExpectations(List<Evaluator.Entry> result, Throwable t, List<Object> expectationDetails) {
        return composeExplanation(result.stream()
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
            formattedEntries,
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
            .map((FormattedEntry eachEntry) -> {
              FormattedEntry lastEntry = lastFormattedEntry.get();
              lastFormattedEntry.set(eachEntry);
              if (lastEntry == null)
                return eachEntry;
              if (Objects.equals(lastEntry.input, eachEntry.input))
                return new FormattedEntry(null, eachEntry.formName, eachEntry.indent(), eachEntry.output, eachEntry.mismatchExplanation);
              return eachEntry;
            })
            .map(formatter)
            .map(s -> ("+" + s).trim().substring(1))
            .collect(joining(format("%n")));
      }
    }

    class FormattedEntry {
      private final String input;
      private final String formName;
      private final String indent;
      private final String output;
      private final Object mismatchExplanation;

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

  class Explanation {
    private final String message;
    private final String expected;
    private final String actual;

    public Explanation(String message, String expected, String actual) {
      this.message = message;
      this.expected = expected;
      this.actual = actual;
    }

    public String message() {
      return this.message;
    }

    public String expected() {
      return this.expected;
    }

    public String actual() {
      return this.actual;
    }

    public String toString() {
      // Did not include "expected" because it is too much overlapping "actual" in most cases.
      return actual != null ?
          format("%s%n%s", message, actual) :
          message;
    }

    public static Explanation fromMessage(String msg) {
      return new Explanation(msg, ReportComposer.Utils.composeReport(null, null), ReportComposer.Utils.composeReport(null, null));
    }
  }
}
