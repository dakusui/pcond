package com.github.dakusui.pcond.core.fluent3.typesupports;


import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface ShortChecker<
    OIN,
    R extends Matcher<R, R, OIN, OIN>>
    extends ComparableNumberChecker<ShortChecker<OIN, R>, R, OIN, Integer> {

  class Impl<
      OIN,
      R extends Matcher<R, R, OIN, OIN>
      > extends Base<
      ShortChecker<OIN, R>,
      R,
      OIN,
      Integer>
      implements ShortChecker<OIN, R> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
