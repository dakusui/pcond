package com.github.dakusui.pcond.functions.preds;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.functions.PrintablePredicate;
import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum LeafPredUtils {
  ;

  public static <T, E> Factory<T, E> factory(Function<E, String> nameComposer, Function<E, Predicate<T>> ff) {
    return new Factory<T, E>(nameComposer) {
      @Override
      Predicate<? super T> createPredicate(E arg) {
        return ff.apply(arg);
      }
    };
  }

  public static class LeafPred<T> extends PrintablePredicate<T> implements Evaluable.LeafPred<T> {
    public LeafPred(Supplier<String> s, Predicate<? super T> predicate) {
      super(s, predicate);
    }

    @Override
    public Predicate<? super T> predicate() {
      return predicate;
    }
  }

  static class LeafPredPrintablePredicateFromFactory<T, E> extends LeafPred<T> implements PrintableLambdaFactory.Lambda<BasePredUtils.Factory<T, E>, E> {
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

  public abstract static class Factory<T, E> extends BasePredUtils.Factory<T, E> {
    Factory(Function<E, String> s) {
      super(s);
    }
  }
}
