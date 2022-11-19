package com.github.dakusui.pcond.core.fluent4.builtins;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface DoubleTransformer<
    OIN
    > extends
    ComparableNumberTransformer<
        DoubleTransformer<OIN>,
        DoubleChecker<OIN>,
        OIN,
        Double> {
  static DoubleTransformer<Double> create(Supplier<Double> value) {
    return new Impl<>(value, trivialIdentityFunction());
  }

  class Impl<
      OIN
      > extends
      Base<
          DoubleTransformer<OIN>,
          DoubleChecker<OIN>,
          OIN,
          Double> implements
      DoubleTransformer<OIN> {
    public Impl(Supplier<OIN> baseValue, Function<OIN, Double> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    protected DoubleChecker<OIN> toChecker(Function<OIN, Double> transformFunction) {
      return new DoubleChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected DoubleTransformer<Double> rebase() {
      return new DoubleTransformer.Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
