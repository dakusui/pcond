package com.github.dakusui.pcond;

import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.functions.Printable;
import com.github.dakusui.pcond.functions.TransformingPredicate;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;

public enum Preconditions {
  ;

  public static <T> T require(
      T value,
      Predicate<? super T> cond,
      BiFunction<T, Predicate<? super T>, ? extends RuntimeException> exceptionFactory) {
    if (!cond.test(value))
      throw exceptionFactory.apply(value, cond);
    return value;
  }

  public static <T> T requireNonNull(T value) {
    return require(value, Predicates.isNotNull(), nullPointer());
  }

  public static <T> T requireArgument(T value, Predicate<? super T> cond) {
    return require(value, cond, illegalArgument());
  }

  public static <T> T requireState(T value, Predicate<? super T> cond) {
    return require(value, cond, illegalState());
  }

  @SafeVarargs
  public static <T> Predicate<? super T> and(Predicate<T>... conds) {
    if (conds.length == 0)
      return Predicates.alwaysTrue();
    if (conds.length == 1)
      return conds[0];
    Predicate<T> ret = conds[0];
    for (int i = 1; i < conds.length; i++)
      ret = ret.and(conds[i]);
    return ret;
  }

  @SafeVarargs
  public static <T> Predicate<? super T> or(Predicate<T>... conds) {
    if (conds.length == 0)
      return Predicates.alwaysTrue().negate();
    if (conds.length == 1)
      return conds[0];
    Predicate<T> ret = conds[0];
    for (int i = 1; i < conds.length; i++)
      ret = ret.or(conds[i]);
    return ret;
  }

  public static <T> Predicate<T> not(Predicate<T> cond) {
    return cond.negate();
  }

  public static <O, P> TransformingPredicate.Factory<P, O> when(String funcName, Function<? super O, ? extends P> func) {
    return when(Printable.function(funcName, func));
  }

  public static <O, P> TransformingPredicate.Factory<P, O> when(Function<? super O, ? extends P> function) {
    return cond -> new TransformingPredicate<>(cond, function);
  }

  public static <T> BiFunction<T, Predicate<? super T>, IllegalStateException> nullPointer() {
    return (t, predicate) -> {
      throw new NullPointerException(composeMessage(t, predicate));
    };
  }

  public static <T> BiFunction<T, Predicate<? super T>, IllegalStateException> illegalState() {
    return (t, predicate) -> {
      throw new IllegalStateException(composeMessage(t, predicate));
    };
  }

  public static <T> BiFunction<T, Predicate<? super T>, IllegalArgumentException> illegalArgument() {
    return (t, predicate) -> {
      throw new IllegalArgumentException(composeMessage(t, predicate));
    };
  }

  private static <T> String composeMessage(T value, Predicate<? super T> predicate) {
    return String.format("value:%s violated precondition:value %s", formatObject(value), predicate);
  }
}
