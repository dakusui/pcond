package com.github.dakusui.pcond.core.fluent3.typesupports;


import com.github.dakusui.pcond.core.fluent.checkers.ComparableNumberChecker;
import com.github.dakusui.pcond.core.fluent3.Checker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Predicate;
import java.util.function.Supplier;

public interface IntegerChecker<OIN> extends Checker<IntegerChecker<OIN>, OIN, Integer> {
  default IntegerChecker<OIN> greaterThan(int i) {
    return this.appendPredicateAsChild(Predicates.greaterThan(i));
  }

  default IntegerChecker<OIN> lessThan(int i) {
    return this.appendPredicateAsChild(Predicates.lessThan(i));
  }

  class Impl<OIN> extends Matcher.Base<IntegerChecker<OIN>, OIN, Integer> implements IntegerChecker<OIN> {
    protected Impl(OIN rootValue, Matcher<?, OIN, OIN> root) {
      super(rootValue, root);
    }
  }
}
