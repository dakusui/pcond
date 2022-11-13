package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectChecker;
import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface BooleanChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN> extends
    AbstractObjectChecker<
        BooleanChecker<R, OIN>,
        R,
        OIN,
        Boolean> {
  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN> extends
      Matcher.Base<
          BooleanChecker<R, OIN>,
          R,
          OIN,
          Boolean
          > implements
      BooleanChecker<R, OIN> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
