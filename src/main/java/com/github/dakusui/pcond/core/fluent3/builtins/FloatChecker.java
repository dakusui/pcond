package com.github.dakusui.pcond.core.fluent3.builtins;


import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface FloatChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN> extends
    ComparableNumberChecker<
        FloatChecker<R, OIN>,
        R,
        OIN,
        Float> {
  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN> extends
      Matcher.Base<
          FloatChecker<R, OIN>,
          R,
          OIN,
          Float>
      implements FloatChecker<R, OIN> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
