package com.github.dakusui.pcond;

import com.github.dakusui.pcond.core.AssertionProvider;

import java.util.function.Predicate;

public enum Postconditions {
  ;

  public static <T> T ensureNonNull(T value) {
    return AssertionProvider.INSTANCE.ensureNonNull(value);
  }

  public static <T> T ensureState(T value, Predicate<? super T> cond) {
    return AssertionProvider.INSTANCE.ensureState(value, cond);
  }

  public static <T> T ensure(T value, Predicate<? super T> cond) {
    return AssertionProvider.INSTANCE.ensure(value, cond);
  }
}
