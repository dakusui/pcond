package com.github.dakusui.pcond.core.fluent4.sandbox;


import com.github.dakusui.pcond.core.fluent4.Checker;
import com.github.dakusui.pcond.core.fluent4.Matcher;
import com.github.dakusui.pcond.core.fluent4.Transformer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalChecks.requireState;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public interface StringTransformer<T> extends
    Transformer<StringTransformer<T>, StringChecker<T>,
        T,
        String> {


  abstract class Base<
      TX extends Transformer<TX, V, T, R>,  // SELF
      V extends Checker<V, T, R>,
      T,
      R> implements
      Transformer<
          TX,
          V,
          T,
          R> {
    private Matcher.JunctionType junctionType;

    private final List<Function<TX, Predicate<? super T>>> childPredicates = new LinkedList<>();
    private       Predicate<T>                             builtPredicate;

    @Override
    public TX allOf() {
      return junctionType(Matcher.JunctionType.CONJUNCTION);
    }

    @Override
    public TX anyOf() {
      return junctionType(Matcher.JunctionType.DISJUNCTION);
    }

    @Override
    public Predicate<T> toPredicate() {
      if (this.builtPredicate == null)
        this.builtPredicate = buildPredicate();
      return this.builtPredicate;
    }

    @SuppressWarnings("unchecked")
    private Predicate<T> buildPredicate() {
      Predicate<T> ret;
      requireState(this, v -> !v.childPredicates.isEmpty(), (v) -> "No child has been added yet.: <" + v + ">");
      if (this.childPredicates.size() == 1)
        ret = (Predicate<T>) childPredicates.get(0).apply(cloneEmpty());
      else {
        ret = (Predicate<T>) this.junctionType.connect(
            new ArrayList<>(this.childPredicates)
                .stream()
                .map(each -> each.apply(cloneEmpty()))
                .collect(toList()));
      }
      return ret;
    }

    private TX cloneEmpty() {
      return null;
    }

    private TX junctionType(Matcher.JunctionType junctionType) {
      requireState(this, v -> childPredicates.isEmpty(), v -> "Child predicate(s) are already added.: <" + this + ">");
      this.junctionType = requireNonNull(junctionType);
      return me();
    }

    @SuppressWarnings("unchecked")
    private TX me() {
      return (TX) this;
    }
  }

  class Impl<T> extends
      Base<
          StringTransformer<T>,
          StringChecker<T>,
          T,
          String
          > implements
      StringTransformer<T> {
    private final Function<T, String> transformFunction;

    public Impl(Function<T, String> transformFunction) {
      this.transformFunction = requireNonNull(transformFunction);
    }

    @Override
    public StringTransformer<T> check(Predicate<String> predicate) {
      return null;
    }

    @Override
    public <TY extends Transformer<TY, ?, String, String>> StringTransformer<T> addTransformPhrase(Function<TY, Predicate<String>> nestedClause) {
      return null;
    }

    @Override
    public Function<T, String> transformFunction() {
      return this.transformFunction;
    }

    @Override
    public StringChecker<T> createCorrespondingChecker(Function<T, String> transformFunction) {
      return new StringChecker.Impl<>(requireNonNull(transformFunction));
    }
  }
}
