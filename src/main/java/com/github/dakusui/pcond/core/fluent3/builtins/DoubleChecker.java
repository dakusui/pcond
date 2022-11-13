package com.github.dakusui.pcond.core.fluent3.builtins;


import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface DoubleChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN
    > extends
    ComparableNumberChecker<
        DoubleChecker<R, OIN>,
        R,
        OIN,
        Integer> {

  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN
      > extends
      Base<
          DoubleChecker<R, OIN>,
          R,
          OIN,
          Integer> implements
      DoubleChecker<
          R,
          OIN> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
