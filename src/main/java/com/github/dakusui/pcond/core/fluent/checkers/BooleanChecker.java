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
  @Override
  BooleanChecker<OIN> create(String transformerName, Function<? super OIN, ? extends Boolean> function, Predicate<? super Boolean> predicate, OIN originalInputValue);

  default BooleanChecker<OIN> isTrue() {
    return this.addPredicate(Predicates.isTrue());
  }

  default BooleanChecker<OIN> isFalse() {
    return this.addPredicate(Predicates.isFalse());
  }

  class Impl<OIN> extends Checker.Base<BooleanChecker<OIN>, OIN, Boolean> implements BooleanChecker<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends Boolean> function, Predicate<? super Boolean> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public BooleanChecker<OIN> create(String transformerName, Function<? super OIN, ? extends Boolean> function, Predicate<? super Boolean> predicate, OIN originalInputValue) {
      return booleanChecker(transformerName, function, predicate, originalInputValue);
    }
  }
}
