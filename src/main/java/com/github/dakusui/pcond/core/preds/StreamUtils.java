package com.github.dakusui.pcond.core.preds;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.printable.PrintablePredicate;
import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.toEvaluableIfNecessary;
import static java.util.Objects.requireNonNull;

public enum StreamUtils {
  ;
  public static <T, E> Factory<T, E> factory(Function<E, String> nameComposer, Function<E, Predicate<T>> ff) {
    return new Factory<T, E>(nameComposer) {
      @Override
      Predicate<? super T> createPredicate(E arg) {
        return ff.apply(arg);
      }
    };
  }

  public static class StreamPred<E> extends PrintablePredicate<Stream<E>> implements Evaluable.StreamPred<E> {
    private final Evaluable<? super E> cut;
    private final boolean              defaultValue;
    private final boolean              cutOn;

    private StreamPred(Supplier<String> s, Predicate<? super Stream<E>> predicate, Evaluable<? super E> cut, boolean defaultValue, boolean cutOn) {
      super(s, predicate);
      this.cut = requireNonNull(cut);
      this.defaultValue = defaultValue;
      this.cutOn = cutOn;
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
    public boolean valueToCut() {
      return cutOn;
    }
  }

  static class StreamPredPrintablePredicateFromFactory<EE, E> extends StreamPred<EE> implements PrintableLambdaFactory.Lambda<BasePredUtils.Factory<Stream<EE>, E>, E> {
    private final Spec<E> spec;

    private StreamPredPrintablePredicateFromFactory(Spec<E> spec, Supplier<String> s, Predicate<? super Stream<EE>> predicate, Evaluable<? super EE> cut, boolean defaultValue, boolean cutOn) {
      super(s, predicate, cut, defaultValue, cutOn);
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

  public abstract static class Factory<T, E> extends BasePredUtils.Factory<T, E> {
    Factory(Function<E, String> s) {
      super(s);
    }

    @SuppressWarnings({ "RedundantClassCall", "unchecked" })
    public <EE> StreamUtils.StreamPredPrintablePredicateFromFactory<EE, E> createStreamPred(E arg, Predicate<? super EE> cut, boolean defaultValue, boolean cutOn) {
      Lambda.Spec<E> spec = new Lambda.Spec<>(Factory.this, arg, StreamUtils.StreamPredPrintablePredicateFromFactory.class);
      return new StreamUtils.StreamPredPrintablePredicateFromFactory<EE, E>(
          spec,
          () -> this.nameComposer().apply(arg),
          Predicate.class.cast(createPredicate(arg)),
          toEvaluableIfNecessary(cut),
          defaultValue,
          cutOn
      );
    }
  }
}
