package com.github.dakusui.pcond.core.identifieable;

import com.github.dakusui.pcond.core.Evaluable;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class PrintablePredicate<T> extends Identifiable.Base implements Predicate<T>, Evaluable<T> {
  protected final Predicate<? super T> predicate;
  final           Supplier<String>     formatter;

  protected PrintablePredicate(Object creator, List<Object> args, Supplier<String> formatter, Predicate<? super T> predicate) {
    super(creator, args);
    this.formatter = formatter;
    this.predicate = predicate;
  }

  @Override
  public boolean test(T t) {
    return this.predicate.test(t);
  }

  @Override
  public String toString() {
    return formatter.get();
  }

  @Override
  public Predicate<T> and(Predicate<? super T> other) {
    return IdentifiablePredicateFactory.and(this, other);
  }

  @Override
  public Predicate<T> or(Predicate<? super T> other) {
    return IdentifiablePredicateFactory.or(this, other);
  }

  @Override
  public Predicate<T> negate() {
    return IdentifiablePredicateFactory.not(this);
  }
}
