package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum NegationKit {
  ;

  static class NegationPrintablePredicateFromFactory<T, E> extends PrintablePredicate.Negation<T> implements PrintableLambdaFactory.Lambda<PrintablePredicate.Factory<T, E>, E> {
    private final Spec<E> spec;

    NegationPrintablePredicateFromFactory(Spec<E> spec, Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> target) {
      super(s, predicate, target);
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
