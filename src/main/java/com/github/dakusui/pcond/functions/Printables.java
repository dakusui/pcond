package com.github.dakusui.pcond.functions;


import com.github.dakusui.pcond.core.identifieable.IdentifiableFunctionFactory;
import com.github.dakusui.pcond.core.identifieable.IdentifiablePredicateFactory;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An entry point class that provides methods to create a new "printable" function from a given conventional function.
 */
public enum Printables {
  ;

  public static <T> Predicate<T> predicate(String s, Predicate<T> predicate) {
    return Printables.predicate(() -> s, predicate);
  }

  public static <T> Predicate<T> predicate(Supplier<String> s, Predicate<T> predicate) {
    return Printables.printablePredicate(s.get(), predicate);
  }

  public static <T> Predicate<T> printablePredicate(String s, Predicate<T> predicate) {
    return IdentifiablePredicateFactory.leaf(Printables.class, () -> s, predicate);
  }

  public static <T, R> Function<T, R> function(String s, Function<T, R> function) {
    return Printables.printableFunction(s, function);
  }


  public static <T, R> Function<T, R> function(Supplier<String> s, Function<T, R> function) {
    return Printables.printableFunction(s.get(), function);
  }

  @SuppressWarnings("unchecked")
  public static <T, R> Function<T, R> printableFunction(String s, Function<? super T, ? extends R> function) {
    return (Function<T, R>) IdentifiableFunctionFactory.function(() -> s, function);
  }
}