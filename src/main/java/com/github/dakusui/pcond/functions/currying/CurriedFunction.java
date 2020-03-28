package com.github.dakusui.pcond.functions.currying;

import java.util.NoSuchElementException;
import java.util.function.Function;

import static com.github.dakusui.pcond.functions.currying.CurryingUtils.*;

public interface CurriedFunction<T, R> extends Function<T, R> {
  R applyFunction(T value);

  default R apply(T value) {
    return ensureReturnedValueType(this.applyFunction(validateArg(value)), returnType());
  }

  @SuppressWarnings("unchecked")
  default <V> V applyLast(T value) {
    return (V) requireLast(this).apply(value);
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
    return CurryingUtils.isValidArgument(this.parameterType(), arg);
  }

  default <V> V validateArg(V arg) {
    return validateArgumentType(arg, parameterType(), messageInvalidTypeArgument(arg, parameterType()));
  }

}
