package com.github.dakusui.pcond.provider.impls;

import com.github.dakusui.pcond.functions.Evaluable;
import com.github.dakusui.pcond.functions.Evaluator;
import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcond.provider.ApplicationException;
import com.github.dakusui.pcond.provider.AssertionProviderBase;

import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static com.github.dakusui.pcond.internals.InternalUtils.spaces;
import static java.util.stream.Collectors.joining;

public class DefaultAssertionProvider implements AssertionProviderBase<ApplicationException> {
  private final int     evaluableNameWidth;
  private final boolean useEvaluator;

  public DefaultAssertionProvider(Properties properties) {
    this.evaluableNameWidth = evaluableNameWidth(this.getClass(), properties);
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

  @Override
  public <T, E extends Throwable> T checkValue(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, Function<String, E> exceptionComposer) throws E {
    if (useEvaluator() && cond instanceof Evaluable) {
      @SuppressWarnings("unchecked")
      Evaluator.Result result = Evaluator.evaluate(value, (Evaluable<? super T>) cond);
      if (result.result())
        return value;
      String b = messageComposer.apply(value, cond) + String.format("%n") +
          result.stream()
              .map(this::formatRecord)
              .collect(joining(String.format("%n")));
      throw exceptionComposer.apply(b);
    } else {
      if (!cond.test(value))
        throw exceptionComposer.apply(messageComposer.apply(value, cond));
      return value;
    }
  }

  protected String formatRecord(Evaluator.Result.Record r) {
    return String.format("%-" + evaluableNameWidth() + "s -> %s",
        String.format("%s%s",
            spaces(r.level() * 2),
            String.format("%s%s", r.name(), r.hasInput() ? "(" + InternalUtils.formatObject(r.input()) + ")" : "")),
        r.output().map(InternalUtils::formatObject).orElse("<<OUTPUT MISSING>>"));
  }

  private int evaluableNameWidth() {
    return this.evaluableNameWidth;
  }

  private boolean useEvaluator() {
    return this.useEvaluator;
  }

  private static int evaluableNameWidth(Class<?> myClass, Properties properties) {
    return Integer.parseInt(properties.getProperty(myClass.getName() + ".evaluableNameWidth", "58"));
  }

  private static boolean useEvaluator(Class<?> myClass, Properties properties) {
    return Boolean.parseBoolean(properties.getProperty(myClass.getName() + ".useEvaluator", "true"));
  }
}
