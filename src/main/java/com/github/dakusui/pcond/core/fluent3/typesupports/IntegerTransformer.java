package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface IntegerTransformer<
    OIN,
    R extends Matcher<R, R, OIN, OIN>> extends
    ComparableNumberTransformer<
        IntegerTransformer<OIN, R>,
        R,
        IntegerChecker<OIN, R>,
        OIN,
        Integer> {
  class Impl<
      OIN,
      R extends Matcher<R, R, OIN, OIN>> extends
      Matcher.Base<
          IntegerTransformer<OIN, R>,
          R,
          OIN,
          Integer> implements
      IntegerTransformer<OIN, R> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public IntegerChecker<OIN, R> createCorrespondingChecker(R root) {
      return new IntegerChecker.Impl<>(this.rootValue(), this.root());
    }
  }
}
