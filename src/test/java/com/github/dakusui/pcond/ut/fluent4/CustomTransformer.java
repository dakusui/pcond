package com.github.dakusui.pcond.ut.fluent4;

import com.github.dakusui.pcond.core.fluent4.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent4.Transformer;
import com.github.dakusui.pcond.core.fluent4.builtins.ObjectChecker;
import com.github.dakusui.pcond.forms.Functions;

import java.util.function.Function;
import java.util.function.Predicate;

abstract class CustomTransformer<
    TX extends AbstractObjectTransformer<
        TX,
        ObjectChecker<T, T>,
        T,
        T>,
    T> extends
    Transformer.Base<
        TX,
        ObjectChecker<T, T>,
        T,
        T> implements
    AbstractObjectTransformer<
        TX,
        ObjectChecker<T, T>,
        T,
        T> {
  public CustomTransformer(T baseValue) {
    super(() -> baseValue, Functions.identity());
  }

  @Override
  protected TX rebase() {
    return create(this.value());
  }

  @Override
  protected ObjectChecker<T, T> toChecker(Function<T, T> transformFunction) {
    return new ObjectChecker.Impl<>(this::value, transformFunction);
  }

  abstract protected TX create(T value);

  abstract public TX transform(Function<TX, Predicate<T>> clause);
}
