package com.github.dakusui.pcond.core.printable;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.functions.preds.ConjunctionUtils;
import com.github.dakusui.pcond.functions.preds.DisjunctionUtils;
import com.github.dakusui.pcond.functions.preds.NegationUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public abstract class PrintablePredicate<T> implements Predicate<T>, Evaluable<T> {
  private static final ConjunctionUtils.Factory<?, List<Predicate<Object>>> AND_FACTORY = ConjunctionUtils.factory(
      (arg) -> format("(%s&&%s)", arg.get(0), arg.get(1)),
      arg -> (Object t) -> unwrapIfPrintablePredicate(arg.get(0)).test(t) && (unwrapIfPrintablePredicate(arg.get(1))).test(t)
  );

  private static final DisjunctionUtils.Factory<?, List<Predicate<Object>>> OR_FACTORY = DisjunctionUtils.factory(
      (arg) -> format("(%s||%s)", arg.get(0), arg.get(1)),
      arg -> (Object t) -> unwrapIfPrintablePredicate(arg.get(0)).test(t) || (unwrapIfPrintablePredicate(arg.get(1))).test(t)
  );

  private static final NegationUtils.Factory<?, Predicate<Object>> NEGATE_FACTORY = NegationUtils.factory(
      (arg) -> format("!%s", arg),
      arg -> (Object t) -> unwrapIfPrintablePredicate(arg).negate().test(t)
  );

  protected final Predicate<? super T> predicate;
  final           Supplier<String>     s;

  protected PrintablePredicate(Supplier<String> s, Predicate<? super T> predicate) {
    this.predicate = requireNonNull(predicate);
    this.s = requireNonNull(s);
  }

  @Override
  public boolean test(T t) {
    return predicate.test(t);
  }

  @SuppressWarnings({ "unchecked" })
  @Override
  public Predicate<T> and(Predicate<? super T> other) {
    requireNonNull(other);
    Predicate<Object> p = (Predicate<Object>) this;
    Predicate<Object> q = (Predicate<Object>) other;
    return (Predicate<T>) AND_FACTORY.createConjunction(asList((Predicate<Object>) this, (Predicate<Object>) other), p, q);
  }

  @SuppressWarnings({ "unchecked" })
  @Override
  public Predicate<T> or(Predicate<? super T> other) {
    requireNonNull(other);
    Predicate<Object> p = (Predicate<Object>) this;
    Predicate<Object> q = (Predicate<Object>) other;
    return (Predicate<T>) OR_FACTORY.createDisjunction(asList((Predicate<Object>) this, (Predicate<Object>) other), p, q);
  }

  @SuppressWarnings({ "unchecked" })
  @Override
  public Predicate<T> negate() {
    Predicate<Object> p = (Predicate<Object>) this;
    return (Predicate<T>) NEGATE_FACTORY.createNegation((Predicate<Object>) this, p);
  }

  @Override
  public int hashCode() {
    return this.predicate.hashCode();
  }

  @Override
  public boolean equals(Object anotherObject) {
    if (this == anotherObject)
      return true;
    if (!(anotherObject instanceof PrintablePredicate))
      return false;
    @SuppressWarnings("unchecked") PrintablePredicate<T> another = (PrintablePredicate<T>) anotherObject;
    return this.predicate.equals(another.predicate);
  }

  @Override
  public String toString() {
    return s.get();
  }

  private static Predicate<Object> unwrapIfPrintablePredicate(Predicate<Object> predicate) {
    Predicate<Object> ret = predicate;
    if (predicate instanceof PrintablePredicate)
      ret = ((PrintablePredicate<Object>) predicate).predicate;
    return ret;
  }

}
