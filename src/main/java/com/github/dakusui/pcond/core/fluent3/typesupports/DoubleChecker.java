package com.github.dakusui.pcond.core.fluent3.typesupports;


import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface DoubleChecker<
    OIN,
    R extends Matcher<R, R, OIN, OIN>>
    extends ComparableNumberChecker<DoubleChecker<OIN, R>, R, OIN, Integer> {

  class Impl<
      OIN,
      R extends Matcher<R, R, OIN, OIN>
      > extends Base<
      DoubleChecker<OIN, R>,
      R,
      OIN,
      Integer>
      implements DoubleChecker<OIN, R> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
