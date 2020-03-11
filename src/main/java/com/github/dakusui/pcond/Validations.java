package com.github.dakusui.pcond;

import com.github.dakusui.pcond.annotations.Published;
import com.github.dakusui.pcond.functions.MessageComposer;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;

public enum Validations {
  ;

  @Published
  public static <T, E extends Throwable> T validate(
      T value,
      Predicate<? super T> cond,
      MessageComposer<T> messageComposer,
      Function<String, E> exceptionFactory) throws E {
    if (cond.test(value))
      return value;
    throw exceptionFactory.apply(messageComposer.apply(value, cond));
  }

  @Published
  public static <T, E extends Throwable> T validate(
      T value,
      Predicate<? super T> cond,
      Function<String, E> exceptionFactory) throws E {
    return validate(value, cond, Validations::composeMessage, exceptionFactory);
  }

  private static <T> String composeMessage(T value, Predicate<? super T> predicate) {
    return String.format("value:%s violated runtime check:value %s", formatObject(value), predicate);
  }
}
