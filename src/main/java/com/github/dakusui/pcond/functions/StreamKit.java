package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public enum StreamKit {
  ;

  public static class StreamPred<E> extends PrintablePredicate<Stream<E>> implements Evaluable.StreamPred<E> {
    private final Evaluable<? super E> cut;
    private final boolean              defaultValue;

    protected StreamPred(Supplier<String> s, Predicate<? super Stream<E>> predicate, Evaluable<? super E> cut, boolean defaultValue) {
      super(s, predicate);
      this.cut = requireNonNull(cut);
      this.defaultValue = defaultValue;
    }

    @Override
    public boolean defaultValue() {
      return this.defaultValue;
    }

    @Override
    public Evaluable<? super E> cut() {
      return this.cut;
    }

    @Override
    public boolean valueOnCut() {
      return !defaultValue();
    }
  }

  static class StreamPredPrintablePredicateFromFactory<EE, E> extends StreamPred<EE> implements PrintableLambdaFactory.Lambda<PrintablePredicate.Factory<Stream<EE>, E>, E> {
    private final Spec<E> spec;

    StreamPredPrintablePredicateFromFactory(Spec<E> spec, Supplier<String> s, Predicate<? super Stream<EE>> predicate, Evaluable<? super EE> cut, boolean defaultValue) {
      super(s, predicate, cut, defaultValue);
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
