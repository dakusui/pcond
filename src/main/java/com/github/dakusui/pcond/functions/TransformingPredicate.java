package com.github.dakusui.pcond.functions;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class TransformingPredicate<P, O> implements Predicate<O> {
  public interface Factory<P, O> {
    default TransformingPredicate<P, O> then(String condName, Predicate<? super P> cond) {
      return then(Printable.predicate(condName, cond));
    }

    TransformingPredicate<P, O> then(Predicate<? super P> cond);
  }

  private final Predicate<? super P>             predicate;
  private final Function<? super O, ? extends P> function;
  private final String                           name;

  public TransformingPredicate(Predicate<? super P> predicate, Function<? super O, ? extends P> function) {
    this(null, predicate, function);
  }

  public TransformingPredicate(String name, Predicate<? super P> predicate, Function<? super O, ? extends P> function) {
    this.predicate = predicate;
    this.function = function;
    this.name = name;
  }

  @Override
  public boolean test(O v) {
    ////
    // This method is usually not called. Because Assertion class invokes function
    // and predicate of this object by itself and do not use this method.
    return predicate.test(function.apply(v));
  }

  public Predicate<? super P> predicate() {
    return this.predicate;
  }

  public Function<? super O, ? extends P> function() {
    return this.function;
  }

  @Override
  public int hashCode() {
    return this.predicate.hashCode() + this.function.hashCode();
  }

  @Override
  public boolean equals(Object anotherObject) {
    if (anotherObject == this)
      return true;
    if (!(anotherObject instanceof TransformingPredicate))
      return false;
    TransformingPredicate<?, ?> another = (TransformingPredicate<?, ?>) anotherObject;
    return Objects.equals(this.name, another.name) &&
        Objects.equals(this.function, another.function) &&
        Objects.equals(this.predicate, another.predicate);
  }

  @Override
  public String toString() {
    return String.format("%s%s %s", this.name == null ? "" : this.name, function(), predicate());
  }
}

