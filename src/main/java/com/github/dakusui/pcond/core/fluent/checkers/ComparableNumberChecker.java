package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Checker;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Predicate;

public interface ComparableNumberChecker<
    TX extends ComparableNumberChecker<TX, OIN, N>,
    OIN,
    N extends Number & Comparable<N>> extends
    Identifiable, Predicate<OIN>,
    Evaluable.Transformation<OIN, N>,
    Checker<TX, OIN, N>,
    Matcher.ForNumber<OIN, N> {
  default TX equalTo(N v) {
    return predicate(Predicates.equalTo(v));
  }

  default TX lessThan(N v) {
    return predicate(Predicates.lessThan(v));
  }

  default TX lessThanOrEqualTo(N v) {
    return predicate(Predicates.lessThanOrEqualTo(v));
  }

  default TX greaterThan(N v) {
    return predicate(Predicates.greaterThan(v));
  }

  default TX greaterThanOrEqualTo(N v) {
    return predicate(Predicates.greaterThanOrEqualTo(v));
  }
}
