package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.function.Supplier;

public interface FloatTransformer<
    R extends Matcher<R, R, OIN, OIN>, OIN
    > extends
    ComparableNumberTransformer<
        FloatTransformer<R, OIN>,
        R,
        FloatChecker<R, OIN>,
        OIN,
        Float> {
  static <R extends Matcher<R, R, Float, Float>> FloatTransformer<R, Float> create(Supplier<Float> value) {
    return new Impl<>(value, null);
  }
  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN
      > extends
      Base<
          FloatTransformer<R, OIN>,
          R,
          OIN,
          Float> implements
      FloatTransformer<R, OIN> {
    public Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public FloatChecker<R, OIN> createCorrespondingChecker(R root) {
      return new FloatChecker.Impl<>(this::rootValue, this.root());
    }
  }
}
