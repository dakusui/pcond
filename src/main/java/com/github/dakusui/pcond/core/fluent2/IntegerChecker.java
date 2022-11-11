package com.github.dakusui.pcond.core.fluent2;

import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface IntegerChecker<OIN> extends Checker<IntegerChecker<OIN>, OIN, Integer> {
  default com.github.dakusui.pcond.core.fluent2.IntegerChecker<OIN> greaterThan(int value) {
    return this.appendPredicateAsChild(Predicates.greaterThan(value));
  }

  default com.github.dakusui.pcond.core.fluent2.IntegerChecker<OIN> lessThan(int value) {
    return this.appendPredicateAsChild(Predicates.lessThan(value));
  }

  class Impl<OIN> extends Base<IntegerChecker<OIN>, OIN, Integer> implements IntegerChecker<OIN> {
    protected Impl(OIN originalInputValue, Supplier<Predicate<OIN>> root) {
      super(originalInputValue, root);
    }
  }
}
