package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface DoubleTransformer<
    R extends Matcher<R, R, OIN, OIN>, OIN
    > extends
    ComparableNumberTransformer<
        DoubleTransformer<R, OIN>,
        R,
        DoubleChecker<R, OIN>,
        OIN,
        Double> {
  static <R extends Matcher<R, R, Double, Double>> DoubleTransformer<R, Double> create(Double value) {
    return new Impl<>(value, null);
  }
  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN
      > extends
      Matcher.Base<
          DoubleTransformer<R, OIN>,
          R,
          OIN,
          Double> implements
      DoubleTransformer<R, OIN> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public DoubleChecker<R, OIN> createCorrespondingChecker(R root) {
      return new DoubleChecker.Impl<>(this.rootValue(), this.root());
    }
  }
}
