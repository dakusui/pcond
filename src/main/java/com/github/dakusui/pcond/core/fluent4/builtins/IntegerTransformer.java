package com.github.dakusui.pcond.core.fluent4.builtins;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface IntegerTransformer<
    OIN
    > extends
    ComparableNumberTransformer<
        IntegerTransformer<OIN>,
        IntegerChecker<OIN>,
        OIN,
        Integer> {
  static IntegerTransformer<Integer> create(Supplier<Integer> value) {
    return new Impl<>(value, trivialIdentityFunction());
  }

  class Impl<
      T
      > extends
      Base<
          IntegerTransformer<T>,
          IntegerChecker<T>,
          T,
          Integer> implements
      IntegerTransformer<T> {
    public Impl(Supplier<T> baseValue, Function<T, Integer> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    public IntegerChecker<T> toChecker(Function<T, Integer> transformFunction) {
      return new IntegerChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    public IntegerTransformer<Integer> rebase() {
      return new IntegerTransformer.Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
