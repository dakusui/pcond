package com.github.dakusui.pcond.core.fluent4;

import com.github.dakusui.pcond.fluent.Statement;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalChecks.requireState;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public interface Transformer<
    TX extends Transformer<TX, V, T, R>,  // SELF
    V extends Checker<V, T, R>,
    T,
    R> extends
    Matcher<TX, T, R>,
    Statement<T>,
    Predicate<T> {

  @SuppressWarnings("unchecked")
  default TX checkWithPredicate(Predicate<? super R> predicate) {
    requireNonNull(predicate);
    return addTransformAndCheckClause(tx -> (Predicate<R>) predicate);
  }

  TX addTransformAndCheckClause(Function<Transformer<?, ?, R, R>, Predicate<R>> clause);

  V then();

  <TY extends Transformer<TY, W, T, RR>,
      W extends Checker<W, T, RR>,
      RR>
  TY transform(Function<? super R, RR> func, BiFunction<Supplier<T>, Function<T, RR>, TY> transformerFactory);

  abstract class Base<
      TX extends Transformer<TX, V, T, R>,  // SELF
      V extends Checker<V, T, R>,
      T,
      R> extends
      Matcher.Base<
          TX,
          T,
          R> implements
      Transformer<
          TX,
          V,
          T,
          R> {

    protected Base(Supplier<T> baseValue, Function<T, R> transformFunction) {
      super(baseValue, transformFunction);
    }

    public V then() {
      requireState(this, Matcher.Base::hasNoChild, v -> format("Predicate is already added. %s", v.childPredicates()));
      return toChecker(this.transformFunction());
    }

    public <
        TY extends Transformer<TY, W, T, RR>,
        W extends Checker<W, T, RR>,
        RR>
    TY transform(Function<? super R, RR> func, BiFunction<Supplier<T>, Function<T, RR>, TY> transformerFactory) {
      return transformerFactory.apply(this::baseValue, transformFunction().andThen(func));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TX checkWithPredicate(Predicate<? super R> predicate) {
      return this.addTransformAndCheckClause(tx -> (Predicate<R>) predicate);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TX addTransformAndCheckClause(Function<Transformer<?, ?, R, R>, Predicate<R>> clause) {
      return this.addPredicate(tx -> clause.apply((Transformer<?, ?, R, R>) tx));
    }

    @Override
    public T statementValue() {
      return baseValue();
    }

    @Override
    public Predicate<T> statementPredicate() {
      return toPredicate();
    }

    @Override
    public boolean test(T value) {
      return statementPredicate().test(baseValue());
    }

    protected abstract V toChecker(Function<T, R> transformFunction);
  }
}
