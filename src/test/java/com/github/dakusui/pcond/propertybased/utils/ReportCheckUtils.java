package com.github.dakusui.pcond.propertybased.utils;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public enum ReportCheckUtils {
  ;

  public static <T> Predicate<T> makePrintablePredicate(String s, Predicate<T> predicate) {
    return new Predicate<T>() {
      @Override
      public boolean test(T t) {
        return predicate.test(t);
      }

      @Override
      public String toString() {
        return s;
      }
    };
  }

  public static <T, R> Function<T, R> makePrintableFunction(String s, Function<T, R> function) {
    return new Function<T, R>() {
      @Override
      public R apply(T t) {
        return function.apply(t);
      }

      @Override
      public String toString() {
        return s;
      }
    };
  }

  public static <T> Predicate<T> equalsPredicate(T w) {
    return makePrintablePredicate("equals(" + w + ")", v -> Objects.equals(v, w));
  }
}
