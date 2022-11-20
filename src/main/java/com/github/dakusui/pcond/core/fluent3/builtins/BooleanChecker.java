package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectChecker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Supplier;

public interface BooleanChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN> extends
    AbstractObjectChecker<
        BooleanChecker<R, OIN>,
        R,
        OIN,
        Boolean> {

  default BooleanChecker<R, OIN> isTrue() {
    return this.checkWithPredicate(Predicates.isTrue());
  }

  default BooleanChecker<R, OIN> isFalse() {
    return this.checkWithPredicate(Predicates.isFalse());
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
    public Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }
  }
}
