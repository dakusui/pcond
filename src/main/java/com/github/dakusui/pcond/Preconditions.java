package com.github.dakusui.pcond;

import com.github.dakusui.pcond.provider.AssertionProvider;

import java.util.function.Predicate;

public enum Preconditions {
  ;

  public static <T> T requireNonNull(T value) {
    return AssertionProvider.INSTANCE.requireNonNull(value);
  }

  public static <T> T requireArgument(T value, Predicate<? super T> cond) {
    return AssertionProvider.INSTANCE.requireArgument(value, cond);
  }

  public static <T> T requireState(T value, Predicate<? super T> cond) {
    return AssertionProvider.INSTANCE.requireState(value, cond);
  }

  @SuppressWarnings("RedundantThrows")
  public static <T, E extends Throwable> T require(
      T value,
      Predicate<? super T> cond) throws E {
    return AssertionProvider.INSTANCE.require(value, cond);
  }

}
