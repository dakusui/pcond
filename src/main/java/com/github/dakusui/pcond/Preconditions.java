package com.github.dakusui.pcond;

import com.github.dakusui.pcond.annotations.Published;
import com.github.dakusui.pcond.functions.MessageComposer;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.internals.Exceptions;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;

public enum Preconditions {
  ;

  @Published
  public static <T> T requireNonNull(T value) {
    return InternalUtils.check(value, Predicates.isNotNull(), Exceptions.nullPointer(Preconditions::composeMessage));
  }

  @Published
  public static <T> T requireArgument(T value, Predicate<? super T> cond) {
    return InternalUtils.check(value, cond, Exceptions.illegalArgument(Preconditions::composeMessage));
  }

  @Published
  public static <T> T requireState(T value, Predicate<? super T> cond) {
    return InternalUtils.check(value, cond, Exceptions.illegalState(Preconditions::composeMessage));
  }

  @Published
  public static <T, E extends Throwable> T require(
      T value,
      Predicate<? super T> cond,
      MessageComposer<T> messageComposer,
      Function<String, E> exceptionFactory) throws E {
    return InternalUtils.check(value, cond, (v, p) -> exceptionFactory.apply(messageComposer.apply(v, p)));
  }

  private static <T> String composeMessage(T value, Predicate<? super T> predicate) {
    return String.format("value:%s violated precondition:value %s", formatObject(value), predicate);
  }
}
