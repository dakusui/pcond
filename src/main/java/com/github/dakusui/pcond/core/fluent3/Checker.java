package com.github.dakusui.pcond.core.fluent3;

import com.github.dakusui.pcond.fluent.Statement;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public interface Checker<
    V extends Checker<V, T, R>,
    T,
    R> extends
    Matcher<V, T, R>,
    Statement<T> {
  V addCheckPhrase(Function<Checker<?, R, R>, Predicate<R>> clause);

  @SuppressWarnings("unchecked")
  default V checkWithPredicate(Predicate<? super R> predicate) {
    requireNonNull(predicate);
    return addCheckPhrase(w -> (Predicate<R>) predicate);
  }
  default Predicate<T> done() {
    return statementPredicate();
  }

  abstract class Base<
      V extends Checker<V, T, R>,
      T,
      R> extends
      Matcher.Base<
          V,
          T,
          R
          > implements
      Checker<
          V,
          T,
          R> {
    public Base(Supplier<T> baseValue, Function<T, R> transformFunction) {
      super(baseValue, transformFunction);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V addCheckPhrase(Function<Checker<?, R, R>, Predicate<R>> clause) {
      return this.addPredicate((Matcher<?, R, R> v) -> clause.apply((Checker<?, R, R>) v));
    }

    @Override
    public T statementValue() {
      return this.baseValue();
    }

    @Override
    public Predicate<T> statementPredicate() {
      return toPredicate();
    }
  }
}
