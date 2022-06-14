package com.github.dakusui.pcond;

import com.github.dakusui.pcond.provider.AssertionProvider;

import java.util.function.Function;
import java.util.function.Predicate;

public enum Validations {
  ;

  public static <T> T validate(T value, Predicate<? super T> cond) {
    return AssertionProvider.INSTANCE.validate(value, cond);
  }

  public static <T, E extends RuntimeException> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionFactory) {
    return AssertionProvider.INSTANCE.validate(value, cond, exceptionFactory::apply);
  }

  public static <T> T validateNonNull(T value) {
    return AssertionProvider.INSTANCE.validateNonNull(value);
  }

  public static <T> T validateArgument(T value, Predicate<? super T> cond) {
    return AssertionProvider.INSTANCE.validateArgument(value, cond);
  }

  public static <T> T validateState(T value, Predicate<? super T> cond) {
    return AssertionProvider.INSTANCE.validateState(value, cond);
  }
}
