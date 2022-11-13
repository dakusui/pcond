package com.github.dakusui.pcond.core.fluent3.typesupports;


import com.github.dakusui.pcond.core.fluent3.Checker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Predicates;

public interface IntegerChecker<OIN, R extends Matcher<R, R, OIN, OIN>> extends Checker<IntegerChecker<OIN, R>, R, OIN, Integer> {
  default IntegerChecker<OIN, R> greaterThan(int i) {
    return this.appendPredicateAsChild(Predicates.greaterThan(i));
  }

  default IntegerChecker<OIN, R> lessThan(int i) {
    return this.appendPredicateAsChild(Predicates.lessThan(i));
  }

  class Impl<OIN, R extends Matcher<R, R, OIN, OIN>> extends Matcher.Base<
      IntegerChecker<OIN, R>,
      R,
      OIN,
      Integer>
      implements IntegerChecker<OIN, R> {
    protected Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
