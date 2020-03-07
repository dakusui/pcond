package com.github.dakusui.pcond.functions;


import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum Printable {
  ;

  public static <T> Predicate<T> predicate(Supplier<String> s, Predicate<T> predicate) {
    return Printable.printablePredicate(s.get(), predicate);
  }

  public static <T> Predicate<T> predicate(String s, Predicate<T> predicate) {
    return Printable.printablePredicate(s, predicate);
  }

  public static <T, R> Function<T, R> function(Supplier<String> s, Function<T, R> function) {
    return Printable.printableFunction(s.get(), function);
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

  static <T, R> PrintableFunction.Factory<T, R> printableFunctionFactory(
      final Function<Object, String> nameComposer,
      final Function<Object, Function<T, R>> ff) {
    return new PrintableFunction.Factory<T, R>(nameComposer) {
      @Override
      Function<T, R> createFunction(Object arg) {
        return ff.apply(arg);
      }
    };
  }

  static <T> PrintablePredicate.Factory<T> predicateFactory(
      final Function<Object, String> nameComposer,
      final Function<Object, Predicate<T>> ff) {
    return new PrintablePredicate.Factory<T>(nameComposer) {
      @Override
      Predicate<T> createPredicate(Object arg) {
        return ff.apply(arg);
      }
    };
  }
}