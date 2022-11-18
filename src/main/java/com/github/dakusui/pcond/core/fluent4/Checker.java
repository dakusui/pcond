package com.github.dakusui.pcond.core.fluent4;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.forms.Predicates.transform;

public interface Checker<
    V extends Checker<V, T, R>,
    T,
    R> {
  default V check(Predicate<R> predicate) {
    return addCheckPhrase(w -> transform(transformFunction()).check(predicate));
  }

  Function<T, R> transformFunction();

  <W extends Checker<W, T, T>> V addCheckPhrase(Function<W, Predicate<T>> clause);

  Predicate<T> toPredicate();
}
