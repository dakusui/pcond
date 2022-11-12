package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.Checker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Predicates;

public interface StringChecker<OIN> extends Checker<StringChecker<OIN>, OIN, String> {
  default StringChecker<OIN> isNotNull() {
    return this.appendPredicateAsChild(Predicates.isNotNull());
  }

  default StringChecker<OIN> isNull() {
    return this.appendPredicateAsChild(Predicates.isNull());
  }
  class Impl<OIN> extends Matcher.Base<StringChecker<OIN>, OIN, String> implements StringChecker<OIN> {
    protected Impl(OIN rootValue, Matcher<?, OIN, OIN> root) {
      super(rootValue, root);
    }
  }
}
