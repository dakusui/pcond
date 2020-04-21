package com.github.dakusui.pcond.functions;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Evaluable<T> {
  void accept(T value, Evaluator evaluator);

  interface Composite<T> extends Evaluable<T> {
    Evaluable<? super T> a();

    Evaluable<? super T> b();
  }

  interface Conjunction<T> extends Composite<T> {
    @Override
    default void accept(T value, Evaluator evaluator) {
      evaluator.evaluate(value, this);
    }
  }

  interface Disjunction<T> extends Composite<T> {
    @Override
    default void accept(T value, Evaluator evaluator) {
      evaluator.evaluate(value, this);
    }
  }

  interface Negation<T> extends Evaluable<T> {
    @Override
    default void accept(T value, Evaluator evaluator) {
      evaluator.evaluate(value, this);
    }

    Evaluable<? super T> target();
  }

  interface Leaf<T> extends Evaluable<T> {
    @Override
    default void accept(T value, Evaluator evaluator) {
      evaluator.evaluate(value, this);
    }

    Predicate<? super T> predicate();
  }

  interface Transformation<T, R> extends Evaluable<T> {
    @Override
    default void accept(T value, Evaluator evaluator) {
      evaluator.evaluate(value, this);
    }

    Evaluable<? super T> mapper();

    Evaluable<? super R> checker();
  }

  interface Func<T> extends Evaluable<T> {
    @Override
    default void accept(T value, Evaluator evaluator) {
      evaluator.evaluate(value, this);
    }

    Function<? super T, ?> head();

    Optional<Evaluable<?>> tail();
  }
}
