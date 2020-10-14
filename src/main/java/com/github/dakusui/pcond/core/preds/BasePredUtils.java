package com.github.dakusui.pcond.core.preds;

import com.github.dakusui.pcond.core.printable.PrintablePredicate;
import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.function.Function;
import java.util.function.Predicate;

public enum BasePredUtils {
  ;

  public static abstract class Factory<T, E> extends PrintableLambdaFactory<E> {
    Factory(Function<E, String> s) {
      super(s);
    }

    abstract Predicate<? super T> createPredicate(E arg);

    public PrintablePredicate<T> create(E arg) {
      return createLeafPred(arg);
    }

    public LeafPredUtils.LeafPredPrintablePredicateFromFactory<T, E> createLeafPred(E arg) {
      Lambda.Spec<E> spec = new Lambda.Spec<>(Factory.this, arg, LeafPredUtils.LeafPredPrintablePredicateFromFactory.class);
      return new LeafPredUtils.LeafPredPrintablePredicateFromFactory<>(spec, () -> this.nameComposer().apply(arg), createPredicate(arg));
    }
  }
}
