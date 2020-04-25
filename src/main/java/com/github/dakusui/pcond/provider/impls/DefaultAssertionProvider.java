package com.github.dakusui.pcond.provider.impls;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcond.provider.ApplicationException;
import com.github.dakusui.pcond.provider.AssertionProviderBase;

import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
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
  public <T, E extends Throwable> T checkValue(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, Function<String, E> exceptionComposer) throws E {
    if (useEvaluator() && cond instanceof Evaluable) {
      Evaluator evaluator = Evaluator.create();
      try {
        ((Evaluable<T>) cond).accept(value, evaluator);
      } catch (Error error) {
        throw error;
      } catch (Throwable t) {
        String message = String.format("An exception(%s) was thrown during evaluation of value: %s: %s", t, value, cond);
        message = message + String.format("%n") + composeExplanation(evaluator.resultRecords(), t);
        throw wrap(message, t);
      }
      Evaluator.Result result = new Evaluator.Result(evaluator.resultValue(), evaluator.resultRecords());
      if (result.result())
        return value;
      String message = messageComposer.apply(value, cond) + String.format("%n") + composeExplanation(evaluator.resultRecords(), null);
      throw exceptionComposer.apply(message);
    } else {
      if (!cond.test(value))
        throw exceptionComposer.apply(messageComposer.apply(value, cond));
      return value;
    }
  }

  private String composeExplanation(List<Evaluator.Result.Record> result, Throwable t) {
    int maxLevel = result.stream().map(Evaluator.Result.Record::level).max(Integer::compareTo).orElse(0);
    int maxNameLength = result.stream().map(record -> record.name().length() + record.level() * 2).max(Integer::compareTo).orElse(0);
    int maxInputLength = result.stream().map(record -> formatObject(record.input()).length() + record.level() * 2).max(Integer::compareTo).orElse(0);
    AtomicReference<?> previousInput = new AtomicReference<>(new Object());
    return result.stream()
        .map(r -> formatRecord(t, maxLevel, maxNameLength, maxInputLength, previousInput, r))
        .collect(joining(String.format("%n")));
  }

  private String formatRecord(Throwable t, int maxLevel, int maxNameLength, int maxInputLength, AtomicReference<?> previousInput, Evaluator.Result.Record r) {
    try {
      return formatRecord(r, previousInput.get(), maxLevel, maxNameLength, maxInputLength, t);
    } finally {
      previousInput.set(r.input());
    }
  }

  protected static String formatRecord(Evaluator.Result.Record r, Object previousInput, int maxLevel, int maxNameLength, int maxInputLength, Throwable throwable) {
    boolean inputValueChanged = !Objects.equals(r.input(), previousInput);
    String indent = spaces(r.level() * 2);
    return String.format("%s %-" + maxNameLength + "s %s %s%s",
        formatInput(r, maxInputLength, inputValueChanged),
        indent + r.name(),
        r.hasOutput() ? "->" : "  ",
        spaces((maxLevel - r.level()) * 2),
        r.hasOutput() ? InternalUtils.formatObject(r.output()) : throwable);
  }

  private boolean useEvaluator() {
    return this.useEvaluator;
  }

  private static boolean useEvaluator(Class<?> myClass, Properties properties) {
    return Boolean.parseBoolean(properties.getProperty(myClass.getName() + ".useEvaluator", "true"));
  }

  private static String formatInput(Evaluator.Result.Record r, int maxInputLength, boolean valueChanged) {
    String formattedInput = InternalUtils.formatObject(r.input());
    String input;
    input = formattedInput;
    String indent = spaces(r.level() * 2);
    return valueChanged ?
        String.format("%-" + maxInputLength + "s %s", indent + input, "->") :
        spaces(maxInputLength + 3);
  }
}
