package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectChecker;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface ThrowableChecker<
    T,
    E extends Throwable> extends
    AbstractObjectChecker<
        ThrowableChecker<T, E>,
        T,
        E> {
  class Impl<
      T,
      E extends Throwable
      > extends
      Base<
          ThrowableChecker<T, E>,
          T,
          E
          > implements
      ThrowableChecker<T, E> {
    protected Impl(Supplier<T> baseValue, Function<T, E> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    protected ThrowableChecker<E, E> rebase() {
      return new ThrowableChecker.Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
