package com.github.dakusui.pcond.internals;

import com.github.dakusui.pcond.functions.Evaluable;
import com.github.dakusui.pcond.functions.PrintablePredicate;
import com.github.dakusui.pcond.functions.Printables;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.toEvaluableIfNecessary;

public class TransformingPredicate<P, O> extends PrintablePredicate<O> implements Predicate<O>, Evaluable.Transformation<O, P> {
  public interface Factory<P, O> {
    default TransformingPredicate<P, O> check(String condName, Predicate<? super P> cond) {
      return check(Printables.predicate(condName, cond));
    }

    TransformingPredicate<P, O> check(Predicate<? super P> cond);
  }

  private final Predicate<? super P>             predicate;
  private final Function<? super O, ? extends P> function;
  private final String                           name;

  public TransformingPredicate(Predicate<? super P> predicate, Function<? super O, ? extends P> function) {
    this(null, predicate, function);
  }

  public TransformingPredicate(String name, Predicate<? super P> predicate, Function<? super O, ? extends P> function) {
    super(() -> "", v -> predicate.test(function.apply(v)));
    this.predicate = predicate;
    this.function = function;
    this.name = name;
  }

  public Predicate<? super P> predicate() {
    return this.predicate;
  }

  public Function<? super O, ? extends P> function() {
    return this.function;
  }

  @Override
  public Evaluable<? super O> mapper() {
    return toEvaluableIfNecessary(this.function());
  }

  @Override
  public Evaluable<? super P> checker() {
    return toEvaluableIfNecessary(this.predicate());
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
    return Objects.equals(this.function, another.function) &&
        Objects.equals(this.predicate, another.predicate);
  }

  @Override
  public String toString() {
    return String.format("%s%s %s", this.name == null ? "" : this.name, function(), predicate());
  }
}