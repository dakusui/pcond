package com.github.dakusui.pcond;

import com.github.dakusui.pcond.annotations.Published;
import com.github.dakusui.pcond.functions.MessageComposer;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.internals.Exceptions;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;

public enum Postconditions {
  ;

  @Published
  public static <T> T ensureNonNull(T value) {
    return InternalUtils.check(value, Predicates.isNotNull(), Exceptions.nullPointer(Postconditions::composeMessage));
  }

  @Published
  public static <T> T ensureState(T value, Predicate<? super T> cond) {
    return InternalUtils.check(value, cond, Exceptions.illegalState(Postconditions::composeMessage));
  }

  @Published
  public static <T, E extends Throwable> T ensure(T value, Predicate<? super T> cond, MessageComposer<T> messageComposer, Function<String, E> exceptionFactory) throws E {
    return InternalUtils.check(value, cond,
        (v, p) -> exceptionFactory.apply(messageComposer.apply(v, p)));
  }

  private static <T> String composeMessage(T value, Predicate<? super T> predicate) {
    return String.format("value:%s violated postcondition:value %s", formatObject(value), predicate);
  }
}
