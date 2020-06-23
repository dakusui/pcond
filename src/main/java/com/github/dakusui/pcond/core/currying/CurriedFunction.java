package com.github.dakusui.pcond.core.currying;

import com.github.dakusui.pcond.functions.MultiParameterFunction;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static com.github.dakusui.pcond.core.currying.Checks.isValidValueForType;
import static com.github.dakusui.pcond.core.currying.Checks.validateArgumentType;

public interface CurriedFunction<T, R> extends Function<T, R> {
  R applyFunction(T value);

  default R apply(T value) {
    return Checks.ensureReturnedValueType(this.applyFunction(validateArg(value)), returnType());
  }

  @SuppressWarnings("unchecked")
  default <V> V applyLast(T value) {
    return (V) Checks.requireLast(this).apply(value);
  }

  @SuppressWarnings("unchecked")
  default <V extends CurriedFunction<T, R>> V applyNext(T value) {
    return (V) requireHasNext(this).apply(value);
  }

  static <V extends CurriedFunction<T, R>, T, R> V requireHasNext(V value) {
    if (!value.hasNext())
      throw new NoSuchElementException();
    return value;
  }

  Class<?> parameterType();

  Class<?> returnType();

  default boolean hasNext() {
    return CurriedFunction.class.isAssignableFrom(returnType());
  }

  default boolean isValidArg(Object arg) {
    return isValidValueForType(arg, this.parameterType());
  }

  default <V> V validateArg(V arg) {
    return validateArgumentType(arg, parameterType(), FormattingUtils.messageInvalidTypeArgument(arg, parameterType()));
  }

  class Impl implements CurriedFunction<Object, Object> {
    private final MultiParameterFunction<Object> function;
    private final List<? super Object>           ongoingContext;

    public Impl(MultiParameterFunction<Object> function, List<? super Object> ongoingContext) {
      this.function = function;
      this.ongoingContext = ongoingContext;
    }

    @Override
    public Class<?> parameterType() {
      return function.parameterType(ongoingContext.size());
    }

    @Override
    public Class<?> returnType() {
      if (ongoingContext.size() == function.arity() - 1)
        return function.returnType();
      else
        return CurriedFunction.class;
    }

    @Override
    public Object applyFunction(Object p) {
      if (ongoingContext.size() == function.arity() - 1)
        return function.apply(InternalUtils.append(ongoingContext, p));
      return CurryingUtils.curry(function, InternalUtils.append(ongoingContext, p));
    }
  }
}
