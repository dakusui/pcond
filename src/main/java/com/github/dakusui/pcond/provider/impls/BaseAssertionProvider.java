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
import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static com.github.dakusui.pcond.provider.AssertionProviderBase.ReportComposer.composeExplanation;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

public abstract class BaseAssertionProvider implements AssertionProviderBase {
  private final MessageComposer messageComposer = new MessageComposer() {
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
  };

  private final ReportComposer  reportComposer  = new ReportComposer() {
  };

  private final boolean         useEvaluator;
  private final Configuration   configuration;

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
  public MessageComposer messageComposer() {
    return this.messageComposer;
  }

  @Override
  public ReportComposer reportComposer() {
    return this.reportComposer;
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
  public <T, E extends Throwable> T checkValueAndThrowIfFails(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, ExceptionFactory<E> exceptionFactory) throws E {
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
      throw exceptionFactory.apply(composeExplanation(messageComposer.apply(value, cond), result.entries, null));
    } else {
      if (!cond.test(value))
        throw exceptionFactory.apply(composeExplanation(messageComposer.apply(value, cond), emptyList(), null));
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
