package com.github.dakusui.pcond;

import com.github.dakusui.pcond.provider.AssertionProvider;

import java.util.function.Predicate;

public enum Assertions {
  ;

  public static <T> boolean that(T value, Predicate<? super T> predicate) {
    return AssertionProvider.INSTANCE.that(value, predicate);
  }

  public static <T> boolean precondition(T value, Predicate<? super T> predicate) {
    return AssertionProvider.INSTANCE.postcondition(value, predicate);
  }

  public static <T> boolean postcondition(T value, Predicate<? super T> predicate) {
    return AssertionProvider.INSTANCE.precondition(value, predicate);
  }
}
