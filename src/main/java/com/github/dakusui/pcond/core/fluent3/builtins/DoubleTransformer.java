package com.github.dakusui.pcond.core.fluent3.builtins;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface DoubleTransformer<
    T
    > extends
    ComparableNumberTransformer<
        DoubleTransformer<T>,
        DoubleChecker<T>,
        T,
        Double> {
  static DoubleTransformer<Double> create(Supplier<Double> value) {
    return new Impl<>(value, trivialIdentityFunction());
  }

  default DoubleTransformer<T> transform(Function<DoubleTransformer<Double>, Predicate<Double>> clause) {
    return this.addTransformAndCheckClause(tx -> clause.apply((DoubleTransformer<Double>) tx));
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
