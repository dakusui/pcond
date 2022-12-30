package com.github.dakusui.pcond.ut.fluent4;

import com.github.dakusui.pcond.core.fluent.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.builtins.ObjectChecker;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.internals.InternalException;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

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

  @SuppressWarnings("unchecked")
  public TX transform(Function<TX, Predicate<T>> clause) {
    requireNonNull(clause);
    return this.addTransformAndCheckClause(tx -> clause.apply((TX) tx));
  }

  @SuppressWarnings("unchecked")
  protected TX create(T value) {
    try {
      return (TX) this.getClass().getConstructor(value.getClass()).newInstance(value);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new InternalException(String.format("Failed to create an instance of this class: <%s>", this.getClass()), e);
    }
  }
}
