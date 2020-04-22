package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum LeafKit {
  ;

  public static class LeafPred<T> extends PrintablePredicate<T> implements Evaluable.LeafPred<T> {
    public LeafPred(Supplier<String> s, Predicate<? super T> predicate) {
      super(s, predicate);
    }

    @Override
    public Predicate<? super T> predicate() {
      return predicate;
    }
  }

  static class LeafPredPrintablePredicateFromFactory<T, E> extends LeafPred<T> implements PrintableLambdaFactory.Lambda<PrintablePredicate.Factory<T, E>, E> {
    private final Spec<E> spec;

    LeafPredPrintablePredicateFromFactory(Spec<E> spec, Supplier<String> s, Predicate<? super T> predicate) {
      super(s, predicate);
      this.spec = spec;
    }

    @Override
    public Spec<E> spec() {
      return spec;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(arg());
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object anotherObject) {
      return equals(anotherObject, type());
    }
  }
}
