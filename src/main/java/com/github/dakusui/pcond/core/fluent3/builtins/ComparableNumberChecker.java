package com.github.dakusui.pcond.core.fluent3.builtins;


import com.github.dakusui.pcond.core.fluent3.Checker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Predicates;

public interface ComparableNumberChecker<
    TX extends ComparableNumberChecker<TX, R, OIN, N>,
    R extends Matcher<R, R, OIN, OIN>,
    OIN,
    N extends Number & Comparable<N>
    > extends Checker<TX, R, OIN, N> {
  default TX equalTo(N v) {
    return appendPredicateAsChild(Predicates.equalTo(v));
  }

  default TX lessThan(N v) {
    return appendPredicateAsChild(Predicates.lessThan(v));
  }

  default TX lessThanOrEqualTo(N v) {
    return appendPredicateAsChild(Predicates.lessThanOrEqualTo(v));
  }

  default TX greaterThan(N v) {
    return appendPredicateAsChild(Predicates.greaterThan(v));
  }

  default TX greaterThanOrEqualTo(N v) {
    return appendPredicateAsChild(Predicates.greaterThanOrEqualTo(v));
  }
}
