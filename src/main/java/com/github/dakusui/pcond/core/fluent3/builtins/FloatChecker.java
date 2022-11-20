package com.github.dakusui.pcond.core.fluent3.builtins;


import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface FloatChecker<
    T> extends
    ComparableNumberChecker<
        FloatChecker<T>,
        T,
        Float> {
  class Impl<
      T> extends
      Base<
          FloatChecker<T>,
          T,
          Float>
      implements FloatChecker<T> {
    public Impl(Supplier<T> rootValue, Function<T, Float> root) {
      super(rootValue, root);
    }

    @Override
    public FloatChecker<Float> rebase() {
      return new FloatChecker.Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
