package com.github.dakusui.pcond.core.fluent3.typesupports;


import com.github.dakusui.pcond.core.fluent3.Matcher;

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
      Matcher.Base<
          IntegerChecker<R, OIN>,
          R,
          OIN,
          Integer>
      implements IntegerChecker<R, OIN> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
