package com.github.dakusui.pcond.core.fluent2;

import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Predicate;
import java.util.function.Supplier;

public interface StringChecker<OIN> extends Checker<StringChecker<OIN>, OIN, String> {
  default StringChecker<OIN> isNotNull() {
    return this.appendPredicateAsChild(Predicates.isNotNull());
  }

  default StringChecker<OIN> isNull() {
    return this.appendPredicateAsChild(Predicates.isNull());
  }

  class Impl<OIN> extends Base<StringChecker<OIN>, OIN, String> implements StringChecker<OIN> {
    protected Impl(OIN originalInputValue, Supplier<Predicate<OIN>> root) {
      super(originalInputValue, root);
    }
  }
}
