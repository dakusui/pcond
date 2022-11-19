package com.github.dakusui.pcond.core.fluent4.sandbox;

import com.github.dakusui.pcond.core.fluent4.Checker;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.makeTrivial;
import static java.util.Objects.requireNonNull;

public interface BooleanChecker<T> extends Checker<
    BooleanChecker<T>,
    T,
    Boolean> {
  default BooleanChecker<T> isTrue() {
    return checkWithPredicate(Predicates.isTrue());
  }

  @SuppressWarnings("unchecked")
  default BooleanChecker<T> check(Function<BooleanChecker<Boolean>, Predicate<Boolean>> phrase) {
    requireNonNull(phrase);
    return this.addCheckPhrase(v -> phrase.apply((BooleanChecker<Boolean>) v));
  }

  class Impl<T> extends Checker.Base<BooleanChecker<T>, T, Boolean> implements BooleanChecker<T> {
    public Impl(Supplier<T> value, Function<T, Boolean> transformFunction) {
      super(value, transformFunction);
    }

    @Override
    public BooleanChecker<Boolean> rebase() {
      return new Impl<>(this::value, makeTrivial(Functions.identity()));
    }
  }
}
