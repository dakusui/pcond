package com.github.dakusui.pcond.core.fluent3.typesupports;


import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface LongChecker<
    OIN,
    R extends Matcher<R, R, OIN, OIN>>
    extends ComparableNumberChecker<LongChecker<OIN, R>, R, OIN, Integer> {

  class Impl<
      OIN,
      R extends Matcher<R, R, OIN, OIN>
      > extends Base<
      LongChecker<OIN, R>,
      R,
      OIN,
      Integer>
      implements LongChecker<OIN, R> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
