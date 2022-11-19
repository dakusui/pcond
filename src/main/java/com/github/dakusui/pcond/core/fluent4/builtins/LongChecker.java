package com.github.dakusui.pcond.core.fluent4.builtins;


import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.function.Supplier;

public interface LongChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN
    > extends
    ComparableNumberChecker<
                LongChecker<R, OIN>,
                R,
                OIN,
                Long> {

  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN> extends
      Base<
          LongChecker<R, OIN>,
          R,
          OIN,
          Long> implements
      LongChecker<
          R,
          OIN> {
    public Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }
  }
}
