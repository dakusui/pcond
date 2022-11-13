package com.github.dakusui.pcond.core.fluent3.typesupports;


import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface IntegerChecker<
    OIN,
    R extends Matcher<R, R, OIN, OIN>>
    extends ComparableNumberChecker<IntegerChecker<OIN, R>, R, OIN, Integer> {

  class Impl<
      OIN,
      R extends Matcher<R, R, OIN, OIN>
      > extends Matcher.Base<
      IntegerChecker<OIN, R>,
      R,
      OIN,
      Integer>
      implements IntegerChecker<OIN, R> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
