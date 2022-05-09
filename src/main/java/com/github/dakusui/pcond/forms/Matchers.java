package com.github.dakusui.pcond.forms;

import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;

import java.util.function.Function;

public enum Matchers {
  ;

  public static <O, P> PrintablePredicateFactory.TransformingPredicate.Factory<P, O> matcher(Function<? super O, ? extends P> function) {
    return Predicates.transform(function);
  }

  public static <O, P> PrintablePredicateFactory.TransformingPredicate.Factory<P, O> matcherFor(Class<O> klassIn, Class<P> klassOut, Function<? super O, ? extends P> function) {
    return Predicates.transform(function).castTo();
  }
}
