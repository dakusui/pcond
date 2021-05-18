package com.github.dakusui.pcond.provider.impls;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcond.provider.ApplicationException;
import com.github.dakusui.pcond.provider.AssertionProviderBase;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.*;
import static java.util.stream.Collectors.joining;

public class DefaultAssertionProvider implements AssertionProviderBase<ApplicationException> {
  private final boolean useEvaluator;

  public DefaultAssertionProvider(Properties properties) {
    this.useEvaluator = useEvaluator(this.getClass(), properties);
  }

  @Override
  public <T> String composeMessageForPrecondition(T value, Predicate<? super T> predicate) {
    return String.format("value:%s violated precondition:value %s", formatObject(value), predicate);
  }

  @Override
  public <T> String composeMessageForPostcondition(T value, Predicate<? super T> predicate) {
    return String.format("value:%s violated postcondition:value %s", formatObject(value), predicate);
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
    String className;
    if (isJUnit5Abailable())
      className = "org.opentest4j.TestSkippedException";
    else
      className = "org.junit.AssumptionViolatedException";
    return (T) createException(message, className);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Error> T testFailedException(String message) {
    String className;
    if (isJUnit5Abailable())
      className = "org.opentest4j.AssertionFailedError";
    else
      className = "junit.framework.AssertionFailedError";
    return (T) createException(message, className);
  }

  private Object createException(String message, String className) {
    try {
      return Class.forName(className).getConstructor(String.class).newInstance(message);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
      throw new RuntimeException("FAILED TO INSTANTIATE EXCEPTION: '" + className + "'", e);
    }
  }

  boolean isJUnit5Abailable() {
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T, E extends Throwable> T checkValue(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, Function<String, E> exceptionComposer) throws E {
    if (useEvaluator() && cond instanceof Evaluable) {
      Evaluator evaluator = Evaluator.create();
      try {
        ((Evaluable<T>) cond).accept(value, evaluator);
      } catch (Error error) {
        throw error;
      } catch (Throwable t) {
        String message = String.format("An exception(%s) was thrown during evaluation of value: %s: %s", t, value, cond);
        message = message + String.format("%n") + composeExplanation(evaluator.resultEntries(), t);
        throw wrap(message, t);
      }
      Result result = new Result(evaluator.resultValue(), evaluator.resultEntries());
      if (result.result())
        return value;
      String message = messageComposer.apply(value, cond) + String.format("%n") + composeExplanation(evaluator.resultEntries(), null);
      throw exceptionComposer.apply(message);
    } else {
      if (!cond.test(value))
        throw exceptionComposer.apply(messageComposer.apply(value, cond));
      return value;
    }
  }

  private String composeExplanation(List<Evaluator.Entry> result, Throwable t) {
    int maxNameColumnLength = result
        .stream()
        .map(entry -> entry.name().length() + entry.level() * 2 + formatObject(entry.input()).length() + 3)
        .max(Integer::compareTo)
        .orElse(0);
    return result.stream()
        .map(r -> formatEntry(r, maxNameColumnLength, t))
        .collect(joining(String.format("%n")));
  }

  protected static String formatEntry(Evaluator.Entry r, int maxNameLength, Throwable throwable) {
    String indent = spaces(r.level() * 2);
    return String.format("%-" + maxNameLength + "s%s %s",
        indent + r.name() + formatInput(r),
        r.hasOutput() ? "->" : "  ",
        r.hasOutput() ? InternalUtils.formatObject(r.output()) : throwable);
  }

  private boolean useEvaluator() {
    return this.useEvaluator;
  }

  private static boolean useEvaluator(Class<?> myClass, Properties properties) {
    return Boolean.parseBoolean(properties.getProperty(myClass.getName() + ".useEvaluator", "true"));
  }

  private static String formatInput(Evaluator.Entry r) {
    String formattedInput = InternalUtils.formatObject(r.input());
    return r.isLeaf() ? String.format("%s %s", "(" + formattedInput + ")", "") : "";
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
