package com.github.dakusui.pcond;

import com.github.dakusui.pcond.provider.AssertionProvider;

import java.util.function.Predicate;

public enum Assertions {
  ;

  public static <T> boolean that(T value, Predicate<? super T> predicate) {
    AssertionProvider.INSTANCE.checkInvariant(value, predicate);
    return true;
  }

  public static <T> boolean precondition(T value, Predicate<? super T> predicate) {
    AssertionProvider.INSTANCE.checkPrecondition(value, predicate);
    return true;
  }

  public static <T> boolean postcondition(T value, Predicate<? super T> predicate) {
    AssertionProvider.INSTANCE.checkPostcondition(value, predicate);
    return true;
  }
}
