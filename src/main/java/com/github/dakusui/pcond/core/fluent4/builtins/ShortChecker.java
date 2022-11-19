package com.github.dakusui.pcond.core.fluent4.builtins;


import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.function.Supplier;

public interface ShortChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN> extends
    ComparableNumberChecker<
                ShortChecker<R, OIN>,
                R,
                OIN,
                Short> {

  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN> extends
      Base<
          ShortChecker<R, OIN>,
          R,
          OIN,
          Short>
      implements ShortChecker<R, OIN> {
    public Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }
  }
}
