package com.github.dakusui.pcond.provider.impls;

import com.github.dakusui.pcond.functions.Evaluable;
import com.github.dakusui.pcond.functions.Evaluator;
import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcond.provider.ApplicationException;
import com.github.dakusui.pcond.provider.AssertionProviderBase;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static com.github.dakusui.pcond.internals.InternalUtils.spaces;
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

  @Override
  public <T, E extends Throwable> T checkValue(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, Function<String, E> exceptionComposer) throws E {
    if (useEvaluator() && cond instanceof Evaluable) {
      @SuppressWarnings("unchecked")
      Evaluator.Result result = Evaluator.evaluate(value, (Evaluable<? super T>) cond);
      if (result.result())
        return value;
      String b = messageComposer.apply(value, cond) + composeExplanation(result);
      throw exceptionComposer.apply(b);
    } else {
      if (!cond.test(value))
        throw exceptionComposer.apply(messageComposer.apply(value, cond));
      return value;
    }
  }

  private String composeExplanation(Evaluator.Result result) {
    int maxLevel = result.stream().map(Evaluator.Result.Record::level).max(Integer::compareTo).orElse(0);
    int maxNameLength = result.stream().map(record -> record.name().length() + record.level() * 2).max(Integer::compareTo).orElse(0);
    int maxInputLength = result.stream().map(record -> formatObject(record.input()).length() + record.level() * 2).max(Integer::compareTo).orElse(0);
    return String.format("%n") +
        result.stream()
            .map(r -> this.formatRecord(r, maxLevel, maxNameLength, maxInputLength))
            .collect(joining(String.format("%n")));
  }

  protected String formatRecord(Evaluator.Result.Record r, int maxLevel, int maxNameLength, int maxInputLength) {
    String formattedInput = InternalUtils.formatObject(r.input());
    String input;
    String arrowForInput;
    input = formattedInput;
    arrowForInput = "->";
    String indent = spaces(r.level() * 2);
    return String.format("%-" + maxInputLength + "s %s %-" + maxNameLength + "s -> %s%s",
        indent + input,
        arrowForInput,
        indent + r.name(),
        indent,
        r.output().map(InternalUtils::formatObject).orElse("<<OUTPUT MISSING>>"));
  }

  private boolean useEvaluator() {
    return this.useEvaluator;
  }

  private static boolean useEvaluator(Class<?> myClass, Properties properties) {
    return Boolean.parseBoolean(properties.getProperty(myClass.getName() + ".useEvaluator", "true"));
  }
}
