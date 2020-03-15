package com.github.dakusui.pcond;

import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Predicate;

public enum Assertions {
  ;

  public static <T> String composeMessage(T t, Predicate<? super T> predicate) {
    return "Value: " + InternalUtils.formatObject(t) + " violated: " + predicate.toString();
  }

  public static <T> boolean that(T value, Predicate<? super T> predicate) {
    if (!predicate.test(value))
      throw new AssertionError(composeMessage(value, predicate));
    return true;
  }
}
