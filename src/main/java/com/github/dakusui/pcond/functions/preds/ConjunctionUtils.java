package com.github.dakusui.pcond.functions.preds;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.toEvaluableIfNecessary;

public enum ConjunctionUtils {
  ;
  public static <T, E> Factory<T, E> factory(Function<E, String> nameComposer, Function<E, Predicate<T>> ff) {
    return new Factory<T, E>(nameComposer) {
      @Override
      Predicate<? super T> createPredicate(E arg) {
        return ff.apply(arg);
      }
    };
  }

  static class ConjunctionPrintablePredicateFromFactory<T, E> extends Conjunction<T> implements PrintableLambdaFactory.Lambda<BasePredUtils.Factory<T, E>, E> {
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

  public static class Conjunction<T> extends BasePredUtils.Junction<T> implements Evaluable.Conjunction<T> {
    public Conjunction(Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> a, Evaluable<? super T> b) {
      super(s, predicate, a, b);
    }
  }

  public abstract static class Factory<T, E> extends BasePredUtils.Factory<T, E> {
    Factory(Function<E, String> s) {
      super(s);
    }
    public <P extends Predicate<? super T>> ConjunctionUtils.ConjunctionPrintablePredicateFromFactory<T, E> createConjunction(E arg, P p, P q) {
      Lambda.Spec<E> spec = new Lambda.Spec<>(Factory.this, arg, ConjunctionUtils.ConjunctionPrintablePredicateFromFactory.class);
      return new ConjunctionUtils.ConjunctionPrintablePredicateFromFactory<>(
          spec,
          () -> this.nameComposer().apply(arg),
          createPredicate(arg),
          toEvaluableIfNecessary(p),
          toEvaluableIfNecessary(q));
    }
  }
}
