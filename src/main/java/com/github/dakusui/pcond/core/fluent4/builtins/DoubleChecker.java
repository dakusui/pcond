package com.github.dakusui.pcond.core.fluent4.builtins;


import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.function.Supplier;

public interface DoubleChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN
    > extends
    ComparableNumberChecker<
                DoubleChecker<R, OIN>,
                R,
                OIN,
                Double> {

  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN
      > extends
      Base<
          DoubleChecker<R, OIN>,
          R,
          OIN,
          Double> implements
      DoubleChecker<
          R,
          OIN> {
    public Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }
  }
}
