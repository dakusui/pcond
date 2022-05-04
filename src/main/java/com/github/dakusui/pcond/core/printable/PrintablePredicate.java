package com.github.dakusui.pcond.core.printable;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class PrintablePredicate<T> extends Identifiable.Base implements Predicate<T>, Evaluable<T> {
  protected final Predicate<? super T> predicate;
  final           Supplier<String>     formatter;

  protected PrintablePredicate(Object creator, List<Object> args, Supplier<String> formatter, Predicate<? super T> predicate) {
    super(creator, args);
    this.formatter = Objects.requireNonNull(formatter);
    this.predicate = requireNonPrintablePredicate(unwrap(Objects.requireNonNull(predicate)));
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
    return Predicates.and(this, other);
  }

  @Override
  public Predicate<T> or(Predicate<? super T> other) {
    return Predicates.or(this, other);
  }

  @Override
  public Predicate<T> negate() {
    return PrintablePredicateFactory.not(this);
  }

  @SuppressWarnings("unchecked")
  static <T> Predicate<? super T> unwrap(Predicate<? super T> predicate) {
    Predicate<? super T> ret = predicate;
    if (predicate instanceof PrintablePredicate) {
      ret = ((PrintablePredicate<? super T>) predicate).predicate;
      assert !(ret instanceof PrintablePredicate);
    }
    return ret;
  }

  private static <T> Predicate<T> requireNonPrintablePredicate(Predicate<T> predicate) {
    assert !(predicate instanceof PrintablePredicate);
    return predicate;
  }

}
