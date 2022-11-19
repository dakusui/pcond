package com.github.dakusui.pcond.core.fluent4.builtins;


import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.function.Supplier;

public interface IntegerChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN
    > extends
    ComparableNumberChecker<
                IntegerChecker<R, OIN>,
                R,
                OIN,
                Integer> {

  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN> extends
      Base<
          IntegerChecker<R, OIN>,
          R,
          OIN,
          Integer>
      implements IntegerChecker<R, OIN> {
    public Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }
  }
}
