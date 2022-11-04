package com.github.dakusui.pcond.fluent;

import java.util.NoSuchElementException;
import java.util.function.Predicate;

@FunctionalInterface
public interface Statement<T> extends Predicate<T> {
  default T statementValue() {
    throw new NoSuchElementException();
  }

  default Predicate<T> statementPredicate() {
    return this;
  }
}
