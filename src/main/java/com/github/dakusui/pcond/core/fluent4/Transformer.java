package com.github.dakusui.pcond.core.fluent4;

import com.github.dakusui.pcond.core.fluent4.sandbox.BooleanTransformer;

import java.util.function.Function;
import java.util.function.Predicate;

public interface Transformer<
    TX extends Transformer<TX, V, T, R>,  // SELF
    V extends Checker<V, T, R>,
    T,
    R> {
  TX allOf();

  TX anyOf();

  Predicate<T> toPredicate();

  default <
      TY extends Transformer<TY, W, T, RR>,
      W extends Checker<W, T, RR>,
      RR>
  TY transform(Function<R, RR> func, Function<Function<T, RR>, TY> transformerFactory) {
    return transformerFactory.apply(transformFunction().andThen(func));
  }

  default BooleanTransformer<T> toBoolean(Function<R, Boolean> function) {
    return this.transform(function, BooleanTransformer.Impl::new);
  }

  TX check(Predicate<R> predicate);

  <TY extends Transformer<TY, ?, R, R>> TX addTransformPhrase(Function<TY, Predicate<R>> nestedClause);

  Function<T, R> transformFunction();

  default V then() {
    return createCorrespondingChecker(this.transformFunction());
  }

  V createCorrespondingChecker(Function<T, R> transformFunction);
}
