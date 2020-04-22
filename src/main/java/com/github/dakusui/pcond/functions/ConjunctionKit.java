package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum ConjunctionKit {
  ;

  static class ConjunctionPrintablePredicateFromFactory<T, E> extends Conjunction<T> implements PrintableLambdaFactory.Lambda<PrintablePredicate.Factory<T, E>, E> {
    private final Spec<E> spec;

    ConjunctionPrintablePredicateFromFactory(Spec<E> spec, Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> a, Evaluable<? super T> b) {
      super(s, predicate, a, b);
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

  public static class Conjunction<T> extends PrintablePredicate.Junction<T> implements Evaluable.Conjunction<T> {
    public Conjunction(Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> a, Evaluable<? super T> b) {
      super(s, predicate, a, b);
    }
  }
}
