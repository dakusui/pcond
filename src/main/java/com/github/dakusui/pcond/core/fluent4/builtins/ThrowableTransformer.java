package com.github.dakusui.pcond.core.fluent4.builtins;


import com.github.dakusui.pcond.core.fluent4.AbstractObjectTransformer;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface ThrowableTransformer<
    T,
    E extends Throwable> extends
    AbstractObjectTransformer<
        ThrowableTransformer<T, E>,
        ThrowableChecker<T, E>,
        T,
        E> {
  static <E extends Throwable> ThrowableTransformer<E, E> create(Supplier<E> value) {
    return new Impl<>(value, trivialIdentityFunction());
  }

  default <OUT2 extends Throwable> ThrowableTransformer<T, OUT2> getCause() {
    // TODO
    // return exercise(Printables.function("getCause", Throwable::getCause)).asThrowable();
    return null;
  }

  default StringTransformer<T> getMessage() {
    //return appendPredicateAsChild(Printables.function("getMessage", Throwable::getMessage));
    // TODO
    return null;
  }

  class Impl<
      T,
      E extends Throwable
      > extends
      Base<
          ThrowableTransformer<T, E>,
          ThrowableChecker<T, E>,
          T,
          E> implements
      ThrowableTransformer<
          T,
          E
          > {

    public Impl(Supplier<T> baseValue, Function<T, E> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    public ThrowableChecker<T, E> toChecker(Function<T, E> transformFunction) {
      return new ThrowableChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected ThrowableTransformer<E, E> rebase() {
      return new ThrowableTransformer.Impl<>(this::value, trivialIdentityFunction());
    }

  }
}
