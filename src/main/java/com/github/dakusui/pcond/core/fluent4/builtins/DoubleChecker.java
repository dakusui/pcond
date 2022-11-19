package com.github.dakusui.pcond.core.fluent4.builtins;


import com.github.dakusui.pcond.forms.Functions;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.makeTrivial;

public interface DoubleChecker<
    T
    > extends
    ComparableNumberChecker<
        DoubleChecker<T>,
        T,
        Double> {

  class Impl<
      T
      > extends
      Base<
          DoubleChecker<T>,
          T,
          Double> implements
      DoubleChecker<
          T> {
    public Impl(Supplier<T> baseValue, Function<T, Double> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    protected DoubleChecker<Double> rebase() {
      return new DoubleChecker.Impl<>(this::value, makeTrivial(Functions.identity()));
    }
  }
}
