package com.github.dakusui.crest.core;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public interface ChainedFunction<T, R> extends Function<T, R> {
  @Override
  <V> ChainedFunction<T, V> andThen(Function<? super R, ? extends V> after);

  ChainedFunction<?, ?> previous();

  Function<?, R> chained();

  static <T, S, R> ChainedFunction<T, R> chain(ChainedFunction<? super T, ? extends S> function, Function<? super S, ? extends R> next) {
    return new Impl<T, S, R>(function, next);
  }

  /**
   * Creates a {@code ChainedFunction} from a given function {@code func}.
   * Returned function does not have one previous to it. In other words, it will
   * be a 'head' of a chain.
   *
   * @param func A function from which a returned function is created.
   * @param <T> Input
   * @param <R> Output
   *
   * @return created {@code ChainedFunction}
   */
  static <T, R> ChainedFunction<T, R> create(Function<? super T, R> func) {
    return new ChainedFunction<T, R>() {
      @Override
      public <V> ChainedFunction<T, V> andThen(Function<? super R, ? extends V> after) {
        return chain(this, after);
      }

      @Override
      public ChainedFunction<T, ?> previous() {
        return null;
      }

      @SuppressWarnings("unchecked")
      @Override
      public Function<T, R> chained() {
        return (Function<T, R>) func;
      }

      @Override
      public R apply(T t) {
        return chained().apply(t);
      }

      @Override
      public String toString() {
        return chained().toString();
      }
    };
  }

  class Impl<T, S, R> implements ChainedFunction<T, R> {

    private final Function<? super S, ? extends R>        func;
    private final ChainedFunction<? super T, ? extends S> previous;

    Impl(ChainedFunction<? super T, ? extends S> previous, Function<? super S, ? extends R> func) {
      this.previous = previous;
      this.func = requireNonNull(func);
    }

    @SuppressWarnings("unchecked")
    @Override
    public R apply(T t) {
      return this.chained().apply(this.previous().apply(t));
    }

    @Override
    public <V> ChainedFunction<T, V> andThen(Function<? super R, ? extends V> after) {
      return ChainedFunction.chain(this, after);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChainedFunction<T, S> previous() {
      return (ChainedFunction<T, S>) previous;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Function<S, R> chained() {
      return (Function<S, R>) func;
    }

    @Override
    public String toString() {
      return previous().toString() + chained().toString();
    }
  }
}
