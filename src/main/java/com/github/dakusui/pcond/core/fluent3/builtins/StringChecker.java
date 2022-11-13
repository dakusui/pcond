package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.Checker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Predicates;

public interface StringChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN> extends
    Checker<
        StringChecker<R, OIN>,
        R,
        OIN,
        String> {
  default StringChecker<R, OIN> isNotNull() {
    return this.appendPredicateAsChild(Predicates.isNotNull());
  }

  default StringChecker<R, OIN> isNull() {
    return this.appendPredicateAsChild(Predicates.isNull());
  }

  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN>
      extends
      Matcher.Base<
          StringChecker<R, OIN>,
          R,
          OIN,
          String> implements
      StringChecker<R, OIN> {
    protected Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
