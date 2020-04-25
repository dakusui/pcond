package com.github.dakusui.pcond.functions.preds;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.toEvaluableIfNecessary;

public enum DisjunctionUtils {
  ;
  public static <T, E> Factory<T, E> factory(Function<E, String> nameComposer, Function<E, Predicate<T>> ff) {
    return new Factory<T, E>(nameComposer) {
      @Override
      Predicate<? super T> createPredicate(E arg) {
        return ff.apply(arg);
      }
    };
  }

  static class DisjunctionPrintablePredicateFromFactory<T, E> extends Disjunction<T> implements PrintableLambdaFactory.Lambda<BasePredUtils.Factory<T, E>, E> {
    private final Spec<E> spec;

    DisjunctionPrintablePredicateFromFactory(Spec<E> spec, Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> a, Evaluable<? super T> b) {
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

  public static class Disjunction<T> extends BasePredUtils.Junction<T> implements Evaluable.Disjunction<T> {
    public Disjunction(Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> a, Evaluable<? super T> b) {
      super(s, predicate, a, b);
    }
  }

  public abstract static class Factory<T, E> extends BasePredUtils.Factory<T, E> {
    Factory(Function<E, String> s) {
      super(s);
    }

    public <P extends Predicate<? super T>> DisjunctionUtils.DisjunctionPrintablePredicateFromFactory<T, E> createDisjunction(E arg, P p, P q) {
      Lambda.Spec<E> spec = new Lambda.Spec<>(Factory.this, arg, DisjunctionUtils.DisjunctionPrintablePredicateFromFactory.class);
      return new DisjunctionUtils.DisjunctionPrintablePredicateFromFactory<>(
          spec,
          () -> this.nameComposer().apply(arg),
          createPredicate(arg),
          toEvaluableIfNecessary(p),
          toEvaluableIfNecessary(q));
    }
  }
}
