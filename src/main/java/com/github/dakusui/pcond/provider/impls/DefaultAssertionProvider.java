package com.github.dakusui.pcond.provider.impls;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcond.provider.ApplicationException;
import com.github.dakusui.pcond.provider.AssertionProviderBase;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.*;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class DefaultAssertionProvider implements AssertionProviderBase<ApplicationException> {
  private final boolean useEvaluator;

  public DefaultAssertionProvider(Properties properties) {
    this.useEvaluator = useEvaluator(this.getClass(), properties);
  }

  @Override
  public <T> String composeMessageForPrecondition(T value, Predicate<? super T> predicate) {
    return format("value:<%s> violated precondition:value %s", formatObject(value), predicate);
  }

  @Override
  public <T> String composeMessageForPostcondition(T value, Predicate<? super T> predicate) {
    return format("value:<%s> violated postcondition:value %s", formatObject(value), predicate);
  }

  @Override
  public <T> String composeMessageForAssertion(T t, Predicate<? super T> predicate) {
    return "Value:" + formatObject(t) + " violated: " + predicate.toString();
  }

  @Override
  public <T> String composeMessageForValidation(T t, Predicate<? super T> predicate) {
    return "Value:" + formatObject(t) + " violated: " + predicate.toString();
  }

  @Override
  public ApplicationException applicationException(String message) {
    return new ApplicationException(message);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends RuntimeException> T testSkippedException(String message) {
    throw (T) createException("org.opentest4j.TestSkippedException", Explanation.fromMessage(message));
  }

  @Override
  public <T extends Error> T testFailedException(String message) {
    throw testFailedException(Explanation.fromMessage(message));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Error> T testFailedException(Explanation explanation) {
    throw (T) createException("org.opentest4j.AssertionFailedError", explanation);
  }

  @SuppressWarnings("unchecked")
  private <T extends Throwable> T createException(String className, Explanation explanation) {
    String message = explanation.message();
    String expected = explanation.expected();
    String actual = explanation.actual();
    try {
      return (T) Class.forName(className).getConstructor(String.class, Object.class, Object.class).newInstance(message, expected, actual);
    } catch (InstantiationException | IllegalAccessException |
             InvocationTargetException | NoSuchMethodException |
             ClassNotFoundException e) {
      throw new RuntimeException("FAILED TO INSTANTIATE EXCEPTION: '" + className + "'", e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T, E extends Throwable> T checkValueAndThrowIfFails(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, ExceptionComposer<E> exceptionComposer) throws E {
    if (useEvaluator() && cond instanceof Evaluable) {
      Evaluator evaluator = Evaluator.create();
      try {
        ((Evaluable<T>) cond).accept(value, evaluator);
      } catch (Error error) {
        throw error;
      } catch (Throwable t) {
        String message = format("An exception(%s) was thrown during evaluation of value: %s: %s", t, value, cond);
        message = message + format("%n") + composeExplanation(evaluator.resultEntries(), t);
        throw wrap(message, t);
      }
      Result result = new Result(evaluator.resultValue(), evaluator.resultEntries());
      if (result.result())
        return value;
      throw exceptionComposer.apply(composeExplanation(messageComposer.apply(value, cond), result.entries, null));
    } else {
      if (!cond.test(value))
        throw exceptionComposer.apply(composeExplanation(messageComposer.apply(value, cond), emptyList(), null));
      return value;
    }
  }

  @Override
  public <T, E extends Throwable> T checkValue(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, Function<String, E> exceptionComposer) throws E {
    return checkValueAndThrowIfFails(value, cond, messageComposer, explanation -> exceptionComposer.apply(explanation.toString()));
  }

  private static String composeExplanation(List<Evaluator.Entry> result, Throwable t) {
    //    int maxNameColumnLength = result.stream().map(entry -> entry.name().length() + entry.level() * 2 + formatObject(entry.input()).length() + 3).max(Integer::compareTo).orElse(0);
    return evaluatorEntriesToString(
        result.stream()
            .map(r -> evaluatorEntryToFormattedEntry(r, t))
            .collect(toList()),
        columnLengths -> formattedEntryToStringForActualResult(columnLengths[0], columnLengths[1], columnLengths[2]));
  }

  private Explanation composeExplanation(String message, List<Evaluator.Entry> result, Throwable t) {
    List<FormattedEntry> formattedEntries = result.stream()
        .map(r -> evaluatorEntryToFormattedEntry(r, t))
        .collect(toList());
    String actualResult = composeActualResult(formattedEntries);
    String expectation = composeExpectation(formattedEntries);
    return new Explanation(message, expectation, actualResult);
  }

  private static String composeActualResult(List<FormattedEntry> formattedEntries) {
    //    int maxNameColumnLength = result.stream().map(entry -> entry.name().length() + entry.level() * 2 + formatObject(entry.input()).length() + 3).max(Integer::compareTo).orElse(0);
    return evaluatorEntriesToString(
        formattedEntries,
        columnLengths -> formattedEntryToStringForActualResult(columnLengths[0], columnLengths[1], columnLengths[2]));
        /*
        .map(r -> formatEntry2(r, maxNameColumnLength, t))
        .collect(joining(format("%n")));

         */
  }

  private static String composeExpectation(List<FormattedEntry> formattedEntries) {
    return evaluatorEntriesToString(
        formattedEntries,
        columnLengths -> formattedEntryToStringForExpectation(columnLengths[0], columnLengths[1]));
  }

  private boolean useEvaluator() {
    return this.useEvaluator;
  }

  protected static String formatEntry(Evaluator.Entry r, int maxNameLength, Throwable throwable) {
    String indent = spaces(r.level() * 2);
    return format("%-" + maxNameLength + "s%s", indent + r.name() + formatInput(r), format("%s %s", !r.suppressPrintingOutput() && r.hasOutput() ? "->" : "  ", r.suppressPrintingOutput() ? "" : r.hasOutput() ? InternalUtils.formatObject(r.output()) : throwable));
  }

  private static boolean useEvaluator(Class<?> myClass, Properties properties) {
    return Boolean.parseBoolean(properties.getProperty(myClass.getName() + ".useEvaluator", "true"));
  }

  private static String formatInput(Evaluator.Entry r) {
    String formattedInput = InternalUtils.formatObject(r.input());
    return r.isLeaf() ? format("%s %s", "(" + formattedInput + ")", "") : "";
  }

  private static FormattedEntry evaluatorEntryToFormattedEntry(Evaluator.Entry entry, Throwable throwable) {
    return new FormattedEntry(entry.isLeaf() ? null : InternalUtils.formatObject(entry.input()), entry.name(), entry.level() == 0 ? "" : format("%" + (entry.level() * 2) + "s", ""), entry.suppressPrintingOutput() ? null : entry.hasOutput() ? InternalUtils.formatObject(entry.output()) : InternalUtils.formatObject(throwable));
  }

  private static Function<FormattedEntry, String> formattedEntryToStringForActualResult(int inputColumnWidth, int formNameColumnLength, int outputColumnLength) {
    return (FormattedEntry formattedEntry) -> format("%-" + inputColumnWidth + "s%-" + formNameColumnLength + "s%-" + outputColumnLength + "s", formattedEntry.input().orElse(""), formattedEntry.input().map(v -> "->").orElse("  ") + formattedEntry.indent() + formattedEntry.formName(), formattedEntry.output().map(v -> "->" + v).orElse(""));
  }

  private static Function<FormattedEntry, String> formattedEntryToStringForExpectation(int inputColumnWidth, int formNameColumnLength) {
    return (FormattedEntry formattedEntry) -> format("%-" + inputColumnWidth + "s%-" + formNameColumnLength + "s", formattedEntry.input().orElse(""), formattedEntry.input().map(v -> "->").orElse("  ") + formattedEntry.indent() + formattedEntry.formName());
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
    return formattedEntries
        .stream()
        .map(formatter)
        .map(s -> "+" + s)
        .map(String::trim)
        .map(s -> s.substring(1))
        .collect(joining(format("%n")));
  }


  private static List<FormattedEntry> evaluatorEntriesToFormattedEntries(List<Evaluator.Entry> evaluatorEntries, Throwable throwable) {
    return evaluatorEntries.stream().map(each -> evaluatorEntryToFormattedEntry(each, throwable)).collect(toList());
  }

  static class FormattedEntry {
    private final String input;
    private final String formName;
    private final String indent;
    private final String output;

    FormattedEntry(String input, String formName, String indent, String output) {
      this.input = input;
      this.formName = formName;
      this.indent = indent;
      this.output = output;
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
  }

  public static class Result {
    final boolean               result;
    final List<Evaluator.Entry> entries;

    public Result(boolean result, List<Evaluator.Entry> entries) {
      this.result = result;
      this.entries = entries;
    }

    public boolean result() {
      return this.result;
    }
  }
}
