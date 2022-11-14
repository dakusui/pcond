package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectChecker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Predicates;

public interface BooleanChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN> extends
    AbstractObjectChecker<
        BooleanChecker<R, OIN>,
        R,
        OIN,
        Boolean> {

  static <R extends Matcher<R, R, Boolean, Boolean>> BooleanChecker<R, Boolean> create(Boolean value) {
    return new Impl<>(value, null);
  }
  default BooleanChecker<R, OIN> isTrue() {
    return this.appendPredicateAsChild(Predicates.isTrue());
  }

  default BooleanChecker<R, OIN> isFalse() {
    return this.appendPredicateAsChild(Predicates.isFalse());
  }

  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN> extends
      Matcher.Base<
          BooleanChecker<R, OIN>,
          R,
          OIN,
          Boolean
          > implements
      BooleanChecker<R, OIN> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
