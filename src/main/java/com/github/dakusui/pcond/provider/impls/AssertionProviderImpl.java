package com.github.dakusui.pcond.provider.impls;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.provider.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.executionFailure;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

public class AssertionProviderImpl implements AssertionProvider {
  private final MessageComposer messageComposer;

  private final ReportComposer reportComposer;

  private final Configuration     configuration;

  private final ExceptionComposer exceptionComposer;

  public AssertionProviderImpl(Properties properties) {
    this.configuration = new Configuration.Builder(properties)
        .assertionProviderClass(this.getClass())
        .useEvaluator(true)
        .build();
    this.messageComposer = this.configuration.createMessageComposer();
    this.reportComposer = this.configuration.createReportComposer();
    this.exceptionComposer = this.configuration.createExceptionComposer(this.reportComposer());
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
  public <T> T checkValueAndThrowIfFails(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, ExceptionFactory<Throwable> exceptionFactory) {
    if (this.configuration().useEvaluator() && cond instanceof Evaluable) {
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
      throw createException(exceptionFactory, messageComposer.apply(value, cond), result.entries);
    } else {
      if (!cond.test(value))
        throw createException(exceptionFactory, messageComposer.apply(value, cond), emptyList());
      return value;
    }
  }

  private RuntimeException createException(ExceptionFactory<?> exceptionFactory, String message, List<Evaluator.Entry> result) {
    Throwable t = exceptionFactory.apply(reportComposer().composeExplanation(message, result, null));
    if (t instanceof Error)
      throw (Error) t;
    if (t instanceof RuntimeException)
      throw (RuntimeException) t;
    throw new AssertionError(format("Checked exception(%s) cannot be used for validation.", t.getClass()), t);
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
