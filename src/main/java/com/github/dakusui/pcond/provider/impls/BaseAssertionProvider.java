package com.github.dakusui.pcond.provider.impls;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.provider.AssertionProviderBase;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.executionFailure;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

public class BaseAssertionProvider implements AssertionProviderBase {
  private final MessageComposer messageComposer;

  private final ReportComposer reportComposer;

  private final boolean           useEvaluator;
  private final Configuration     configuration;
  private final ExceptionComposer exceptionComposer;

  public BaseAssertionProvider(Properties properties) {
    this.useEvaluator = useEvaluator(this.getClass(), properties);
    this.configuration = Configuration.create(properties);
    this.messageComposer = this.configuration.createMessageComposer();
    this.reportComposer = this.configuration.createReportComposer();
    this.exceptionComposer = this.configuration.createExceptionComposerFromProperties(properties, this);
  }
  @Override
  public Configuration configuration() {
    return this.configuration;
  }

  @Override
  public MessageComposer messageComposer() {
    return this.messageComposer;
  }

  @Override
  public ReportComposer reportComposer() {
    return this.reportComposer;
  }

  @Override
  final public ExceptionComposer exceptionComposer() {
    return this.exceptionComposer;
  }

  @FunctionalInterface
  public interface ReflectiveExceptionFactory<T extends Throwable> {
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
  public static <T extends Throwable> T createException(String className, Explanation explanation, ReflectiveExceptionFactory<T> reflectiveExceptionFactory) {
    try {
      return reflectiveExceptionFactory.apply((Class<T>) Class.forName(className), explanation);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("FAILED TO INSTANTIATE EXCEPTION: '" + className + "' (NOT FOUND)", e);
    }
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T, E extends Throwable> T checkValueAndThrowIfFails(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, ExceptionFactory<E> exceptionFactory) throws E {
    if (useEvaluator && cond instanceof Evaluable) {
      Evaluator evaluator = Evaluator.create();
      try {
        ((Evaluable<T>) cond).accept(value, evaluator);
      } catch (Error error) {
        throw error;
      } catch (Throwable t) {
        String message = format("An exception(%s) was thrown during evaluation of value: %s: %s", t, value, cond);
        throw executionFailure(reportComposer().composeExplanation(message, evaluator.resultEntries(), t), t);
      }
      Result result = new Result(evaluator.resultValue(), evaluator.resultEntries());
      if (result.result())
        return value;
      throw exceptionFactory.apply(reportComposer().composeExplanation(messageComposer.apply(value, cond), result.entries, null));
    } else {
      if (!cond.test(value))
        throw exceptionFactory.apply(reportComposer().composeExplanation(messageComposer.apply(value, cond), emptyList(), null));
      return value;
    }
  }

  @Override
  public <T, E extends Throwable> T checkValue(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, Function<String, E> exceptionComposer) throws E {
    return checkValueAndThrowIfFails(value, cond, messageComposer, explanation -> exceptionComposer.apply(explanation.toString()));
  }


  private static boolean useEvaluator(Class<?> myClass, Properties properties) {
    return Boolean.parseBoolean(properties.getProperty(myClass.getName() + ".useEvaluator", "true"));
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
