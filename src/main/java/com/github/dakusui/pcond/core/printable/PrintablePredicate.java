package com.github.dakusui.pcond.core.printable;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.preds.LeafPredUtils;

import java.util.Collections;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public abstract class PrintablePredicate<T> extends com.github.dakusui.pcond.core.identifieable.PrintablePredicate<T> implements Predicate<T>, Evaluable<T> {
  protected PrintablePredicate(Supplier<String> s, Predicate<? super T> predicate) {
    super(new Object(), Collections.emptyList(), s, predicate);
  }

  public static <T> Predicate<T> create(String s, Predicate<T> predicate) {
    requireNonNull(s);
    return new LeafPredUtils.LeafPred<>(() -> s, predicate);
  }
}
