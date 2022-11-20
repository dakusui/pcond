package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.builtins.*;
import com.github.dakusui.pcond.forms.Functions;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.refl.MethodQuery.classMethod;
import static com.github.dakusui.pcond.core.refl.MethodQuery.instanceMethod;
import static com.github.dakusui.pcond.forms.Functions.call;
import static com.github.dakusui.pcond.forms.Functions.parameter;
import static java.util.Objects.requireNonNull;

public interface AbstractObjectTransformer<
    TX extends AbstractObjectTransformer<TX, V, T, R>,
    V extends AbstractObjectChecker<V, T, R>,
    T,
    R
    > extends
    Transformer<TX, V, T, R> {

  /**
   * Corresponds to {@code toString()} method.
   *
   * @return this object the method appended.
   */
  @SuppressWarnings("unchecked")
  default StringTransformer<String> stringify() {
    return (StringTransformer<String>) this.toString(Functions.stringify());
  }

  default <E> ObjectTransformer<T, E> invoke(String methodName, Object... args) {
    return this.toObject(call(instanceMethod(parameter(), methodName, args)));
  }

  default <E> ObjectTransformer<T, E> invokeStatic(Class<?> klass, String methodName, Object... args) {
    return this.toObject(call(classMethod(klass, methodName, args)));
  }

  default <O extends Throwable> ThrowableTransformer<T, O> expectException(Class<O> exceptionClass, Function<? super R, ?> f) {
    requireNonNull(exceptionClass);
    return this.toThrowable(Functions.expectingException(exceptionClass, f));
  }

  default <E> ObjectTransformer<T, E> toObject(Function<R, E> function) {
    return this.transformValueWith(function, ObjectTransformer.Impl::new);
  }


  default BooleanTransformer<T> toBoolean(Function<? super R, Boolean> function) {
    return this.transformValueWith(function, BooleanTransformer.Impl::new);
  }

  default IntegerTransformer<T> toInteger(Function<? super R, Integer> function) {
    return this.transformValueWith(function, IntegerTransformer.Impl::new);
  }

  default LongTransformer<T> toLong(Function<? super R, Long> function) {
    return this.transformValueWith(function, LongTransformer.Impl::new);
  }

  default ShortTransformer<T> toShort(Function<? super R, Short> function) {
    return this.transformValueWith(function, ShortTransformer.Impl::new);
  }

  default DoubleTransformer<T> toDouble(Function<? super R, Double> function) {
    return this.transformValueWith(function, DoubleTransformer.Impl::new);
  }

  default FloatTransformer<T> toFloat(Function<? super R, Float> function) {
    return this.transformValueWith(function, FloatTransformer.Impl::new);
  }

  default StringTransformer<T> toString(Function<? super R, String> function) {
    return this.transformValueWith(function, StringTransformer.Impl::new);
  }


  default <E> ListTransformer<T, E> toList(Function<? super R, List<E>> function) {
    return this.transformValueWith(function, ListTransformer.Impl::new);
  }

  default <E> StreamTransformer<T, E> toStream(Function<? super R, Stream<E>> function) {
    return this.transformValueWith(function, StreamTransformer.Impl::new);
  }

  default <E extends Throwable> ThrowableTransformer<T, E> toThrowable(Function<? super R, E> function) {
    return this.transformValueWith(function, ThrowableTransformer.Impl::new);

  }

}
