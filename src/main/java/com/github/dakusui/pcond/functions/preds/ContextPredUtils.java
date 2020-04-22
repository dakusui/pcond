package com.github.dakusui.pcond.functions.preds;

import java.util.function.Function;
import java.util.function.Predicate;

public class ContextPredUtils {
  ;

  public static <T, E> Factory<T, E> factory(Function<E, String> nameComposer, Function<E, Predicate<T>> ff) {
    return new Factory<T, E>(nameComposer) {
      @Override
      Predicate<? super T> createPredicate(E arg) {
        return ff.apply(arg);
      }
    };
  }

  public abstract static class Factory<T, E> extends BasePredUtils.Factory<T, E> {
    Factory(Function<E, String> s) {
      super(s);
    }
  }
}
