package com.github.dakusui.pcond.functions.preds;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.printable.PrintablePredicate;
import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

  abstract static class Junction<T> extends PrintablePredicate<T> implements Evaluable.Composite<T> {
    private final Evaluable<? super T> a;
    private final Evaluable<? super T> b;

    public Junction(Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> a, Evaluable<? super T> b) {
      super(s, predicate);
      this.a = a;
      this.b = b;
    }

    @Override
    public Evaluable<? super T> a() {
      return this.a;
    }

    @Override
    public Evaluable<? super T> b() {
      return this.b;
    }
  }
}
