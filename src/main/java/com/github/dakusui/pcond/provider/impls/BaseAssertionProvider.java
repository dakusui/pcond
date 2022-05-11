package com.github.dakusui.pcond.provider.impls;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcond.provider.ApplicationException;
import com.github.dakusui.pcond.provider.AssertionProviderBase;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.core.Evaluator.Entry.Type.*;
import static com.github.dakusui.pcond.internals.InternalUtils.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public abstract class BaseAssertionProvider implements AssertionProviderBase<ApplicationException> {
  private final boolean useEvaluator;
  private final Configuration configuration ;

  public BaseAssertionProvider(Properties properties) {
    this.useEvaluator = useEvaluator(this.getClass(), properties);
    this.configuration = new Configuration() {
      @Override
      public int summarizedStringLength() {
        return Configuration.super.summarizedStringLength();
      }
    };
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

  @Override
  public <T extends Error> T testFailedException(String message) {
    throw testFailedException(Explanation.fromMessage(message));
  }

  @Override
  public Configuration configuration() {
    return this.configuration;
  }

  @FunctionalInterface
  interface ReflectiveExceptionFactory<T extends Throwable> {
    T create(Class<T> c, Explanation explanation) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    default T apply(Class<T> c, Explanation explanation) {
      try {
        return create(c, explanation);
      } catch (InvocationTargetException | InstantiationException |
          IllegalAccessException | NoSuchMethodException e) {
        throw new RuntimeException("FAILED TO INSTANTIATE EXCEPTION: '" + c.getCanonicalName() + "'", e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  <T extends Throwable> T createException(String className, Explanation explanation, ReflectiveExceptionFactory<T> reflectiveExceptionFactory) {
    try {
      return reflectiveExceptionFactory.apply((Class<T>) Class.forName(className), explanation);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("FAILED TO INSTANTIATE EXCEPTION: '" + className + "' (NOT FOUND)", e);
    }
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T, E extends Throwable> T checkValueAndThrowIfFails(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, ExceptionComposer<E> exceptionComposer) throws E {
    if (useEvaluator && cond instanceof Evaluable) {
      Evaluator evaluator = Evaluator.create();
      try {
        ((Evaluable<T>) cond).accept(value, evaluator);
      } catch (Error error) {
        throw error;
      } catch (Throwable t) {
        String message = format("An exception(%s) was thrown during evaluation of value: %s: %s", t, value, cond);
        throw executionFailure(composeExplanation(message, evaluator.resultEntries(), t), t);
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

  private Explanation composeExplanation(String message, List<Evaluator.Entry> result, Throwable t) {
    String expectation = composeExplanation(result.stream()
        .map(r -> evaluatorEntryToFormattedEntry(
            r,
            () -> (r.hasOutput() ?
                InternalUtils.formatObject(r.output() instanceof Boolean ? r.expectedBooleanValue() : r.output()) :
                InternalUtils.formatObject(t))))
        .collect(toList()));
    String actualResult = composeExplanation(result.stream()
        .map(r -> evaluatorEntryToFormattedEntry(
            r,
            () -> r.hasOutput() ?
                InternalUtils.formatObject(r.output()) :
                InternalUtils.formatObject(t)))
        .collect(toList()));
    return new Explanation(message, expectation, actualResult);
  }

  private static String composeExplanation(List<FormattedEntry> formattedEntries) {
    return evaluatorEntriesToString(
        formattedEntries,
        columnLengths -> formattedEntryToStringForActualResult(columnLengths[0], columnLengths[1], columnLengths[2]));
  }

  private static boolean useEvaluator(Class<?> myClass, Properties properties) {
    return Boolean.parseBoolean(properties.getProperty(myClass.getName() + ".useEvaluator", "true"));
  }


  private static FormattedEntry evaluatorEntryToFormattedEntry(Evaluator.Entry entry, Supplier<String> outputFormatter) {
    return new FormattedEntry(
        InternalUtils.formatObject(entry.input()),
        entry.name(),
        entry.level() == 0 ?
            "" :
            format("%" + (entry.level() * 2) + "s", ""),
        !asList(LEAF, AND, OR, NOT).contains(entry.type()) ?
            null :
            outputFormatter.get());
  }

  private static Function<FormattedEntry, String> formattedEntryToStringForActualResult(int inputColumnWidth, int formNameColumnLength, int outputColumnLength) {
    return (FormattedEntry formattedEntry) -> format("%-" +
            Math.max(2, inputColumnWidth) + "s%-" +
            (formNameColumnLength + 2) + "s%-" +
            Math.max(2, outputColumnLength) + "s",
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
            return new FormattedEntry(null, eachEntry.formName, eachEntry.indent(), eachEntry.output);
          return eachEntry;
        })
        .map(formatter)
        .map(s -> ("+" + s).trim().substring(1))
        .collect(joining(format("%n")));
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
