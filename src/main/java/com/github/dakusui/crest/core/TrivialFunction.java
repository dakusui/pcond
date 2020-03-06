package com.github.dakusui.crest.core;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public interface TrivialFunction<T, R> extends Function<T, R> {
  static <T, R> TrivialFunction<T, R> create(Function<? super T, ? extends R> function) {
    requireNonNull(function);
    return new TrivialFunction<T, R>() {
      @Override
      public R apply(T t) {
        return function.apply(t);
      }

      @Override
      public String toString() {
        return "";
      }
    };
  }
}
