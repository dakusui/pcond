package com.github.dakusui.pcond.core.fluent4.builtins;

import com.github.dakusui.pcond.core.fluent4.AbstractObjectChecker;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.makeTrivial;

public interface BooleanChecker<T> extends
    AbstractObjectChecker<
        BooleanChecker<T>,
        T,
        Boolean> {

  default BooleanChecker<T> isTrue() {
    return this.checkWithPredicate(Predicates.isTrue());
  }

  default BooleanChecker<T> isFalse() {
    return this.checkWithPredicate(Predicates.isFalse());
  }

  class Impl<T> extends
      Base<
          BooleanChecker<T>,
          T,
          Boolean> implements
      BooleanChecker<T> {
    public Impl(Supplier<T> baseValue, Function<T, Boolean> transformingFunction) {
      super(baseValue, transformingFunction);
    }

    @Override
    protected BooleanChecker<Boolean> rebase() {
      return new BooleanChecker.Impl<>(this::value, makeTrivial(Functions.identity()));
    }
  }
}
