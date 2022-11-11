package com.github.dakusui.pcond.core.fluent2;

import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;

public interface Checker<V extends Checker<V, OIN, T>, OIN, T> extends Matcher<V, OIN, T> {
  interface IntegerChecker<OIN> extends Checker<IntegerChecker<OIN>, OIN, Integer> {
    default IntegerChecker<OIN> greaterThan(int value) {
      return this.appendPredicateAsChild(Predicates.greaterThan(value));
    }

    default IntegerChecker<OIN> lessThan(int value) {
      return this.appendPredicateAsChild(Predicates.greaterThan(value));
    }

    class Impl<OIN> extends Matcher.Base<IntegerChecker<OIN>, OIN, Integer> implements IntegerChecker<OIN> {
      protected Impl(OIN originalInputValue, Function<OIN, Integer> base) {
        super(originalInputValue, base);
      }
    }
  }
}
