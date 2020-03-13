package com.github.dakusui.pcond.functions;


import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum Printables {
  ;

  public static <T> Predicate<T> predicate(Supplier<String> s, Predicate<T> predicate) {
    return Printables.printablePredicate(s.get(), predicate);
  }

  public static <T> Predicate<T> predicate(String s, Predicate<T> predicate) {
    return predicate(() -> s, predicate);
  }

  public static <T, R> Function<T, R> function(Supplier<String> s, Function<T, R> function) {
    return Printables.printableFunction(s.get(), function);
  }

  public static <T, R> Function<T, R> function(String s, Function<T, R> function) {
    return function(() -> s, function);
  }

  public static <T> Predicate<T> printablePredicate(String s, Predicate<T> predicate) {
    return new PrintablePredicate<>(() -> s, predicate);
  }

  public static <T, R> PrintableFunction<T, R> printableFunction(String s, Function<? super T, ? extends R> function) {
    return PrintableFunction.create(s, function);
  }

  public static <T, R, E> PrintableFunction.Factory<T, R, E> functionFactory(
      final Function<E, String> nameComposer,
      final Function<E, Function<T, R>> ff) {
    return PrintableFunction.factory(nameComposer, ff);
  }

  public static <T, E> PrintablePredicate.Factory<T, E> predicateFactory(
      final Function<E, String> nameComposer,
      final Function<E, Predicate<T>> ff) {
    return PrintablePredicate.factory(nameComposer, ff);
  }
}