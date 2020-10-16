package com.github.dakusui.pcond.functions;


import com.github.dakusui.pcond.core.printable.PrintableFunctionFactory;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An entry point class that provides methods to create a new "printable" function from a given conventional function.
 */
public enum Printables {
  ;

  public static <T> Predicate<T> predicate(String name, Predicate<T> predicate) {
    return PrintablePredicateFactory.leaf(name, predicate);
  }

  public static <T> Predicate<T> predicate(Supplier<String> formatter, Predicate<T> predicate) {
    return PrintablePredicateFactory.leaf(formatter, predicate);
  }

  public static <T, R> Function<T, R> function(String name, Function<T, R> function) {
    return PrintableFunctionFactory.function(name, function);
  }

  public static <T, R> Function<T, R> function(Supplier<String> formatter, Function<T, R> function) {
    return PrintableFunctionFactory.function(formatter, function);
  }
}