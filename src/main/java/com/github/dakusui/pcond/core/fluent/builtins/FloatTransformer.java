package com.github.dakusui.pcond.core.fluent.builtins;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface FloatTransformer<
    T
    > extends
    ComparableNumberTransformer<
        FloatTransformer<T>,
        FloatChecker<T>,
        T,
        Float> {
  static FloatTransformer<Float> create(Supplier<Float> value) {
    return new Impl<>(value, trivialIdentityFunction());
  }

  @SuppressWarnings("unchecked")
  default FloatTransformer<T> transform(Function<FloatTransformer<Float>, Predicate<Float>> clause) {
    return this.addTransformAndCheckClause(tx -> clause.apply((FloatTransformer<Float>) tx));
  }
  class Impl<
      T
      > extends
      Base<
          FloatTransformer<T>,
          FloatChecker<T>,
          T,
          Float> implements
      FloatTransformer<T> {
    public Impl(Supplier<T> rootValue, Function<T, Float> root) {
      super(rootValue, root);
    }

    @Override
    protected FloatChecker<T> toChecker(Function<T, Float> transformFunction) {
      return new FloatChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected FloatTransformer<Float> rebase() {
      return new FloatTransformer.Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
