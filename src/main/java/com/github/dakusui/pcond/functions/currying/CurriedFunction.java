package com.github.dakusui.pcond.functions.currying;

import java.util.function.Function;

import static com.github.dakusui.pcond.functions.currying.CurryngUtils.*;

@FunctionalInterface
public interface CurriedFunction<T, R> extends Function<T, R> {
  R applyFunction(T value);

  default R apply(T value) {
    return ensureReturnedValueType(this.applyFunction(validateArgumentType(value, parameterType(), messageInvalidTypeArgument(value, parameterType()))), returnType());
  }

  @SuppressWarnings("unchecked")
  default <V> V applyLast(T value) {
    return (V) requireLast(this).apply(value);
  }

  default Class<?> parameterType() {
    return Object.class;
  }

  default Class<?> returnType() {
    return Object.class;
  }

  default boolean hasNext() {
    return CurriedFunction.class.isAssignableFrom(returnType());
  }

  default boolean isValidArgument(Object arg) {
    return CurryngUtils.isValidArgument(this.parameterType(), arg);
  }
}
