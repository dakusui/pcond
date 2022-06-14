package com.github.dakusui.pcond;

import com.github.dakusui.pcond.provider.AssertionProvider;

import java.util.function.Function;
import java.util.function.Predicate;

public enum Validations {
  ;

  public static <T, E extends RuntimeException> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) {
    return AssertionProvider.INSTANCE.validate(value, cond, exceptionComposer::apply);
  }
}
