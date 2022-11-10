package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Checker;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.booleanChecker;

public interface BooleanChecker<OIN> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, Boolean>,
    Checker<BooleanChecker<OIN>, OIN, Boolean>,
    Matcher.ForBoolean<OIN> {
  default BooleanChecker<OIN> isTrue() {
    return this.addPredicate(Predicates.isTrue());
  }

  default BooleanChecker<OIN> isFalse() {
    return this.addPredicate(Predicates.isFalse());
  }

  class Impl<OIN> extends Checker.Base<BooleanChecker<OIN>, OIN, Boolean> implements BooleanChecker<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends Boolean> function, OIN originalInputValue) {
      super(originalInputValue, transformerName, function);
    }

    @Override
    public BooleanChecker<OIN> create(OIN originalInputValue, String transformerName, Function<? super OIN, ? extends Boolean> function) {
      return booleanChecker(transformerName, function, originalInputValue);
    }
  }
}
