package com.github.dakusui.pcond.core.fluent3.typesupports;


import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface ShortChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN> extends
    ComparableNumberChecker<
        ShortChecker<R, OIN>,
        R,
        OIN,
        Integer> {

  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN> extends
      Base<
          ShortChecker<R, OIN>,
          R,
          OIN,
          Integer>
      implements ShortChecker<R, OIN> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
