package com.github.dakusui.pcond.core.fluent4.sandbox;

import com.github.dakusui.pcond.core.fluent4.Checker;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;

import static com.github.dakusui.pcond.internals.InternalUtils.makeTrivial;

public interface BooleanChecker<T> extends Checker<
    BooleanChecker<T>,
    T,
    Boolean> {
  default BooleanChecker<T> isTrue() {
    return check(Predicates.isTrue());
  }

  class Impl<T> extends Checker.Base<BooleanChecker<T>, T, Boolean> implements BooleanChecker<T> {
    public Impl(Function<T, Boolean> transformFunction) {
      super(transformFunction);
    }

    @Override
    public BooleanChecker<Boolean> rebase() {
      return new Impl<>(makeTrivial(Functions.identity()));
    }
  }
}
