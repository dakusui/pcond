package com.github.dakusui.pcond.core.fluent3.typesupports;


import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface FloatChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN> extends
    ComparableNumberChecker<
        FloatChecker<R, OIN>,
        R,
        OIN,
        Integer> {

  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN> extends
      Base<
          FloatChecker<R, OIN>,
          R,
          OIN,
          Integer>
      implements FloatChecker<R, OIN> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
