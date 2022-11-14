package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface IntegerTransformer<
    R extends Matcher<R, R, OIN, OIN>, OIN
    > extends
    ComparableNumberTransformer<
        IntegerTransformer<R, OIN>,
        R,
        IntegerChecker<R, OIN>,
        OIN,
        Integer> {
  static <R extends Matcher<R, R, Integer, Integer>> IntegerTransformer<R, Integer> create(Integer value) {
    return new Impl<>(value, null);
  }
  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN
      > extends
      Matcher.Base<
          IntegerTransformer<R, OIN>,
          R,
          OIN,
          Integer> implements
      IntegerTransformer<R, OIN> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public IntegerChecker<R, OIN> createCorrespondingChecker(R root) {
      return new IntegerChecker.Impl<>(this.rootValue(), this.root());
    }
  }
}
