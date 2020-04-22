package com.github.dakusui.pcond.functions;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Evaluable<T> {
  void accept(T value, Evaluator evaluator);

  interface Pred<T> extends Evaluable<T> {
  }

  interface Composite<T> extends Pred<T> {
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

  interface Negation<T> extends Pred<T> {
    @Override
    default void accept(T value, Evaluator evaluator) {
      evaluator.evaluate(value, this);
    }

    Evaluable<? super T> target();
  }

  interface LeafPred<T> extends Pred<T> {
    @Override
    default void accept(T value, Evaluator evaluator) {
      evaluator.evaluate(value, this);
    }

    Predicate<? super T> predicate();
  }

  interface StreamPred<E> extends Pred<Stream<E>> {
    @Override
    default void accept(Stream<E> value, Evaluator evaluator) {
      evaluator.evaluate(value, this);
    }

    boolean defaultValue();

    Evaluable<? super E> cut();

    boolean valueOnCut();
  }

  interface Transformation<T, R> extends Pred<T> {
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
