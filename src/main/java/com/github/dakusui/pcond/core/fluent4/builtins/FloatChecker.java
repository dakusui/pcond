package com.github.dakusui.pcond.core.fluent4.builtins;


import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.function.Supplier;

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
      Base<
          FloatChecker<R, OIN>,
          R,
          OIN,
          Float>
      implements FloatChecker<R, OIN> {
    public Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }
  }
}
