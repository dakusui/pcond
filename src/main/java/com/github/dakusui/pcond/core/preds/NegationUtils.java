package com.github.dakusui.pcond.core.preds;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.printable.PrintablePredicate;
import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.toEvaluableIfNecessary;

public enum NegationUtils {
  ;
  public static <T, E> Factory<T, E> factory(Function<E, String> nameComposer, Function<E, Predicate<T>> ff) {
    return new Factory<T, E>(nameComposer) {
      @Override
      Predicate<? super T> createPredicate(E arg) {
        return ff.apply(arg);
      }
    };
  }

  static class NegationPrintablePredicateFromFactory<T, E> extends Negation<T> implements PrintableLambdaFactory.Lambda<BasePredUtils.Factory<T, E>, E> {
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

  public static class Negation<T> extends PrintablePredicate<T> implements Evaluable.Negation<T> {
    private final Evaluable<? super T> body;

    public Negation(Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> body) {
      super(s, predicate);
      this.body = body;
    }

    @Override
    public Evaluable<? super T> target() {
      return this.body;
    }
  }

  public abstract static class Factory<T, E> extends BasePredUtils.Factory<T, E> {
    Factory(Function<E, String> s) {
      super(s);
    }

    public <P extends Predicate<? super T>> NegationUtils.NegationPrintablePredicateFromFactory<T, E> createNegation(E arg, P p) {
      Lambda.Spec<E> spec = new Lambda.Spec<>(Factory.this, arg, NegationUtils.NegationPrintablePredicateFromFactory.class);
      return new NegationUtils.NegationPrintablePredicateFromFactory<>(
          spec,
          () -> this.nameComposer().apply(arg),
          createPredicate(arg),
          toEvaluableIfNecessary(p));
    }
  }
}
