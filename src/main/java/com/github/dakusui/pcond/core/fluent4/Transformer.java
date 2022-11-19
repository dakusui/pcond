package com.github.dakusui.pcond.core.fluent4;

import com.github.dakusui.pcond.core.fluent4.sandbox.BooleanTransformer;
import com.github.dakusui.pcond.core.fluent4.sandbox.StringTransformer;
import com.github.dakusui.pcond.fluent.Statement;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Transformer<
    TX extends Transformer<TX, V, T, R>,  // SELF
    V extends Checker<V, T, R>,
    T,
    R> extends
    Matcher<TX, T, R>,
    Statement<T> {
  Predicate<T> toPredicate();

  R value();

  default <
      TY extends Transformer<TY, W, T, RR>,
      W extends Checker<W, T, RR>,
      RR>
  TY transform(Function<R, RR> func, BiFunction<Supplier<T>, Function<T, RR>, TY> transformerFactory) {
    return transformerFactory.apply(this::baseValue, transformFunction().andThen(func));
  }

  default BooleanTransformer<T> toBoolean(Function<R, Boolean> function) {
    return this.transform(function, BooleanTransformer.Impl::new);
  }

  default StringTransformer<T> toString(Function<R, String> function) {
    return this.transform(function, StringTransformer.Impl::new);
  }

  TX check(Predicate<? super R> predicate);

  TX addTransformAndCheckClause(Function<Transformer<?, ?, R, R>, Predicate<R>> clause);

  default V then() {
    return toChecker(this.transformFunction());
  }

  V toChecker(Function<T, R> transformFunction);

  abstract class Base<
      TX extends Transformer<TX, V, T, R>,  // SELF
      V extends Checker<V, T, R>,
      T,
      R> extends
      Matcher.Base<
          TX,
          T,
          R> implements
      Transformer<
          TX,
          V,
          T,
          R> {

    protected Base(Supplier<T> baseValue, Function<T, R> transformFunction) {
      super(baseValue, transformFunction);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TX check(Predicate<? super R> predicate) {
      return this.addTransformAndCheckClause(tx -> (Predicate<R>) predicate);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TX addTransformAndCheckClause(Function<Transformer<?, ?, R, R>, Predicate<R>> clause) {
      return this.addPredicate(tx -> clause.apply((Transformer<?, ?, R, R>)tx));
    }

    @Override
    public T statementValue() {
      return baseValue();
    }

    @Override
    public Predicate<T> statementPredicate() {
      return toPredicate();
    }
  }
}
