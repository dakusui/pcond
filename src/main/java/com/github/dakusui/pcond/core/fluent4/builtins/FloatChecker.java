package com.github.dakusui.pcond.core.fluent4.builtins;


import com.github.dakusui.pcond.forms.Functions;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.makeTrivial;

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
      return new FloatChecker.Impl<>(this::value, makeTrivial(Functions.identity()));
    }
  }
}
