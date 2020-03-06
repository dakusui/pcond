package com.github.dakusui.pcond.functions;

import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class PrintablePredicate<T> implements Predicate<T> {
  final Predicate<? super T> predicate;
  final String               s;

  public PrintablePredicate(String s, Predicate<? super T> predicate) {
    this.predicate = requireNonNull(predicate);
    this.s = requireNonNull(s);
  }

  @Override
  public boolean test(T t) {
    return predicate.test(t);
  }

  @Override
  public Predicate<T> and(Predicate<? super T> other) {
    requireNonNull(other);
    return new PrintablePredicate<T>(format("(%s&&%s)", s, other), t -> predicate.test(t) && other.test(t));
  }

  @Override
  public Predicate<T> negate() {
    return new PrintablePredicate<>(String.format("!%s", s), predicate.negate());
  }

  @Override
  public Predicate<T> or(Predicate<? super T> other) {
    requireNonNull(other);
    return new PrintablePredicate<T>(format("(%s||%s)", s, other), t -> predicate.test(t) || other.test(t));
  }

  @Override
  public String toString() {
    return s;
  }
}
