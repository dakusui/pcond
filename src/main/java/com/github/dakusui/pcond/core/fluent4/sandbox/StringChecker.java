package com.github.dakusui.pcond.core.fluent4.sandbox;


import com.github.dakusui.pcond.core.fluent4.Checker;

import java.util.function.Function;
import java.util.function.Predicate;

public interface StringChecker<T> extends Checker<StringChecker<T>, T, String> {
  class Impl<T> implements StringChecker<T> {
    private final Function<T, String> transformFunction;

    public Impl(Function<T, String> transformFunction) {
      this.transformFunction = transformFunction;
    }

    @Override
    public Function<T, String> transformFunction() {
      return this.transformFunction;
    }

    @Override
    public <W extends Checker<W, T, T>> StringChecker<T> addCheckPhrase(Function<W, Predicate<T>> clause) {
      return null;
    }

    @Override
    public Predicate<T> toPredicate() {
      return null;
    }
  }
}
