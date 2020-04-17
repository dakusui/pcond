package com.github.dakusui.pcond.functions;

import java.util.function.Function;
import java.util.function.Predicate;

public interface Evaluable<T> {
  boolean accept(T value, Evaluator evaluator);

  interface Composite<T> extends Evaluable<T> {
    Evaluable<? super T> a();

    Evaluable<? super T> b();
  }

  interface Conjunction<T> extends Composite<T> {
    default boolean accept(T value, Evaluator evaluator) {
      return evaluator.evaluate(value, this);
    }
  }

  interface Disjunction<T> extends Composite<T> {
    default boolean accept(T value, Evaluator evaluator) {
      return evaluator.evaluate(value, this);
    }
  }

  interface Negation<T> extends Evaluable<T> {
    default boolean accept(T value, Evaluator evaluator) {
      return evaluator.evaluate(value, this);
    }

    Evaluable<T> body();
  }

  interface Leaf<T> extends Evaluable<T> {
    default boolean accept(T value, Evaluator evaluator) {
      return evaluator.evaluate(value, this);
    }

    Predicate<? super T> predicate();
  }

  interface Transformation<T, R> extends Evaluable<T> {
    default boolean accept(T value, Evaluator evaluator) {
      return evaluator.evaluate(value, this);
    }

    Function<T, R> mapper();

    Evaluable<R> checker();
  }
}
