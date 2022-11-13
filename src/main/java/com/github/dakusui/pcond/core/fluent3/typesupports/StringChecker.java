package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.Checker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Predicates;

public interface StringChecker<OIN, R extends Matcher<R, R, OIN, OIN>> extends Checker<StringChecker<OIN, R>, R, OIN, String> {
  default StringChecker<OIN, R> isNotNull() {
    return this.appendPredicateAsChild(Predicates.isNotNull());
  }

  default StringChecker<OIN, R> isNull() {
    return this.appendPredicateAsChild(Predicates.isNull());
  }

  class Impl<
      OIN,
      R extends Matcher<R, R, OIN, OIN>>
      extends Matcher.Base<
      StringChecker<OIN, R>,
      R,
      OIN, String> implements StringChecker<OIN, R> {
    protected Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
