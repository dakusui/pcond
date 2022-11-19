package com.github.dakusui.pcond.core.fluent4.builtins;


import com.github.dakusui.pcond.core.fluent3.AbstractObjectChecker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Predicates;

public interface ComparableNumberChecker<
    TX extends ComparableNumberChecker<TX, R, OIN, N>,
    R extends Matcher<R, R, OIN, OIN>,
    OIN,
    N extends Number & Comparable<N>
    > extends AbstractObjectChecker<TX, R, OIN, N> {
  default TX equalTo(N v) {
    return checkWithPredicate(Predicates.equalTo(v));
  }

  default TX lessThan(N v) {
    return checkWithPredicate(Predicates.lessThan(v));
  }

  default TX lessThanOrEqualTo(N v) {
    return checkWithPredicate(Predicates.lessThanOrEqualTo(v));
  }

  default TX greaterThan(N v) {
    return checkWithPredicate(Predicates.greaterThan(v));
  }

  default TX greaterThanOrEqualTo(N v) {
    return checkWithPredicate(Predicates.greaterThanOrEqualTo(v));
  }
}
