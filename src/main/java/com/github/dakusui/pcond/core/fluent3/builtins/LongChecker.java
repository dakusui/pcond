package com.github.dakusui.pcond.core.fluent3.builtins;


import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface LongChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN
    > extends
    ComparableNumberChecker<
        LongChecker<R, OIN>,
        R,
        OIN,
        Integer> {

  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN> extends
      Base<
          LongChecker<R, OIN>,
          R,
          OIN,
          Integer> implements
      LongChecker<
          R,
          OIN> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
