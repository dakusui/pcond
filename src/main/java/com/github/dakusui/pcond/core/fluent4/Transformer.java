package com.github.dakusui.pcond.core.fluent4;

import com.github.dakusui.pcond.core.fluent4.sandbox.BooleanTransformer;
import com.github.dakusui.pcond.core.fluent4.sandbox.StringTransformer;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalChecks.requireState;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public interface Transformer<
    TX extends Transformer<TX, V, T, R>,  // SELF
    V extends Checker<V, T, R>,
    T,
    R> {
  TX allOf();

  TX anyOf();

  Predicate<T> toPredicate();

  R value();

  default <
      TY extends Transformer<TY, W, T, RR>,
      W extends Checker<W, T, RR>,
      RR>
  TY transform(Function<R, RR> func, BiFunction<Supplier<T>, Function<T, RR>, TY> transformerFactory) {
    return transformerFactory.apply(this::baseValue, transformFunction().andThen(func));
  }

  default BooleanTransformer<T> toBoolean(Function<R, Boolean> function) {
    return this.transform(function, BooleanTransformer.Impl::new);
  }

  default StringTransformer<T> toString(Function<R, String> function) {
    return this.transform(function, StringTransformer.Impl::new);
  }

  TX check(Predicate<? super R> predicate);

  TX addTransformAndCheckClause(Function<Transformer<?, ?, R, R>, Predicate<R>> clause);

  Function<T, R> transformFunction();

  default V then() {
    return toChecker(this.transformFunction());
  }

  V toChecker(Function<T, R> transformFunction);

  /**
   * Override this method so that it returns extending class.
   *
   * @return A rebased transformer.
   */
  Transformer<?, ?, R, R> rebase();

  T baseValue();

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
    private final Function<T, R> transformFunction;
    private final Supplier<T>    baseValue;

    private Matcher.JunctionType junctionType;

    private final List<Function<Transformer<?, ?, R, R>, Predicate<R>>> childPredicates = new LinkedList<>();

    private Predicate<T> builtPredicate;

    protected Base(Supplier<T> baseValue, Function<T, R> transformFunction) {
      this.transformFunction = requireNonNull(transformFunction);
      this.baseValue = requireNonNull(baseValue);
      this.allOf();
    }

    @Override
    public TX allOf() {
      return junctionType(Matcher.JunctionType.CONJUNCTION);
    }

    @Override
    public TX anyOf() {
      return junctionType(Matcher.JunctionType.DISJUNCTION);
    }

    @Override
    public R value() {
      return this.transformFunction.apply(this.baseValue.get());
    }

    @Override
    public Predicate<T> toPredicate() {
      if (this.builtPredicate == null)
        this.builtPredicate = buildPredicate();
      return this.builtPredicate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TX check(Predicate<? super R> predicate) {
      return this.addTransformAndCheckClause(tx -> (Predicate<R>) predicate);
    }

    @Override
    public TX addTransformAndCheckClause(Function<Transformer<?, ?, R, R>, Predicate<R>> clause) {
      this.childPredicates.add(clause);
      return me();
    }

    @Override
    public Function<T, R> transformFunction() {
      return this.transformFunction;
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

      System.out.println(ret);
      return Predicates.transform(this.transformFunction).check(ret);
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

    @Override
    public T baseValue() {
      return this.baseValue.get();
    }
  }
}
