package com.github.dakusui.pcond;

import com.github.dakusui.pcond.provider.AssertionProvider;

import java.util.function.Predicate;

public enum Validations {
  ;

  @SuppressWarnings({ "RedundantCast", "unchecked" })
  public static <T, E extends Throwable> T validate(T value, Predicate<? super T> cond) throws E {
    return ((AssertionProvider<E>) AssertionProvider.INSTANCE).validate(value, cond);
  }
}
