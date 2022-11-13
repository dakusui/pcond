package com.github.dakusui.pcond.core.fluent3.typesupports;


import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface FloatChecker<
    OIN,
    R extends Matcher<R, R, OIN, OIN>>
    extends ComparableNumberChecker<FloatChecker<OIN, R>, R, OIN, Integer> {

  class Impl<
      OIN,
      R extends Matcher<R, R, OIN, OIN>
      > extends Base<
      FloatChecker<OIN, R>,
      R,
      OIN,
      Integer>
      implements FloatChecker<OIN, R> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
