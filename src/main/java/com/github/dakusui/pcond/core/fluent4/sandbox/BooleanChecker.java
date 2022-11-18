package com.github.dakusui.pcond.core.fluent4.sandbox;

import com.github.dakusui.pcond.core.fluent4.Checker;

import java.util.function.Function;
import java.util.function.Predicate;

public interface BooleanChecker<T> extends Checker<
    BooleanChecker<T>,
    T,
    Boolean> {
  class Impl<T> implements BooleanChecker<T> {


    private final Function<T, Boolean> transformFunction;

    public Impl(Function<T, Boolean> transformFunction) {
      this.transformFunction = transformFunction;
    }

    @Override
    public BooleanChecker<T> check(Predicate<Boolean> predicate) {
      return null;
    }

    @Override
    public Function<T, Boolean> transformFunction() {
      return this.transformFunction;
    }

    @Override
    public <W extends Checker<W, T, T>> BooleanChecker<T> addCheckPhrase(Function<W, Predicate<T>> clause) {
      return null;
    }

    @Override
    public Predicate<T> toPredicate() {
      return null;
    }
  }
}
