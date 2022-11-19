package com.github.dakusui.pcond.core.fluent4;

import com.github.dakusui.pcond.fluent.Statement;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalChecks.requireState;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public interface Checker<
    V extends Checker<V, T, R>,
    T,
    R> extends
    Matcher<V, T, R>,
    Statement<T> {
  V addCheckPhrase(Function<Checker<?, R, R>, Predicate<R>> clause);

  default V checkWithPredicate(Predicate<R> predicate) {
    requireNonNull(predicate);
    return addCheckPhrase(w -> predicate);
  }

  R value();

  Predicate<T> toPredicate();

  Checker<?, R, R> rebase();

  abstract class Base<
      V extends Checker<V, T, R>,
      T,
      R> implements Checker<
      V,
      T,
      R> {
    private final Function<T, R> transformFunction;
    private final Supplier<T>    baseValue;

    private Matcher.JunctionType junctionType;

    private final List<Function<Checker<?, R, R>, Predicate<R>>> childPredicates = new LinkedList<>();
    private       Predicate<T>                                   builtPredicate;

    public Base(Supplier<T> baseValue, Function<T, R> transformFunction) {
      this.transformFunction = requireNonNull(transformFunction);
      this.baseValue = requireNonNull(baseValue);
      this.allOf();
    }

    @Override
    public V allOf() {
      return junctionType(Matcher.JunctionType.CONJUNCTION);
    }

    @Override
    public V anyOf() {
      return junctionType(Matcher.JunctionType.DISJUNCTION);
    }

    @Override
    public R value() {
      return this.transformFunction.apply(baseValue.get());
    }

    @Override
    public V addCheckPhrase(Function<Checker<?, R, R>, Predicate<R>> clause) {
      this.childPredicates.add(clause);
      return me();
    }

    @Override
    public Predicate<T> toPredicate() {
      if (this.builtPredicate == null)
        this.builtPredicate = buildPredicate();
      return this.builtPredicate;
    }

    private Predicate<T> buildPredicate() {
      Predicate<R> ret;
      requireState(this, v -> !v.childPredicates.isEmpty(), (v) -> "No child has been added yet.: <" + v + ">");
      if (this.childPredicates.size() == 1)
        ret = childPredicates.get(0).apply(rebase());
      else {
        ret = this.junctionType.connect(
            new ArrayList<>(this.childPredicates)
                .stream()
                .map(each -> each.apply(rebase()))
                .collect(toList()));
      }
      return Predicates.transform(transformFunction).check(ret);
    }

    private V junctionType(Matcher.JunctionType junctionType) {
      requireState(this, v -> childPredicates.isEmpty(), v -> "Child predicate(s) are already added.: <" + this + ">");
      this.junctionType = requireNonNull(junctionType);
      return me();
    }

    @SuppressWarnings("unchecked")
    private V me() {
      return (V) this;
    }

    @Override
    public T statementValue() {
      return this.baseValue.get();
    }

    @Override
    public Predicate<T> statementPredicate() {
      return toPredicate();
    }
  }
}
