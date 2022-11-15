package com.github.dakusui.pcond.core.fluent3;

import com.github.dakusui.pcond.core.fluent3.builtins.*;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.refl.MethodQuery.classMethod;
import static com.github.dakusui.pcond.core.refl.MethodQuery.instanceMethod;
import static com.github.dakusui.pcond.forms.Functions.call;
import static com.github.dakusui.pcond.forms.Functions.parameter;
import static java.util.Objects.requireNonNull;

/**
 * A base interface for all the "transformers".
 * This method defines methods that can be used for all the classes, such as `isNotNull`, etc.
 *
 * @param <TX>  The interface extending this interface itself.
 * @param <RX>  The root matcher, usually a transformer.
 * @param <V>   The corresponding checker interface. If `TX` is `StringTransformer`, this will be `StringChecker`.
 * @param <OIN> The type of "original input value".
 * @param <T>   The current target type.
 */
public interface AbstractObjectTransformer<
    TX extends Transformer<TX, RX, V, OIN, T>,
    RX extends Matcher<RX, RX, OIN, OIN>,
    V extends Checker<V, RX, OIN, T>,
    OIN,
    T> extends
    Transformer<TX, RX, V, OIN, T> {

  /**
   * Corresponds to {@code toString()} method.
   *
   * @return this object the method appended.
   */
  default <RY extends Matcher<RY, RY, String, String>> StringTransformer<RY, String> stringify() {
    return (StringTransformer<RY, String>) this.toString(Functions.stringify());
  }

  default <E> ObjectTransformer<RX, OIN, E> invoke(String methodName, Object... args) {
    return this.toObject(call(instanceMethod(parameter(), methodName, args)));
  }

  default <E> ObjectTransformer<RX, OIN, E> invokeStatic(Class<?> klass, String methodName, Object... args) {
    return this.toObject(call(classMethod(klass, methodName, args)));
  }

  default <O extends Throwable> ThrowableTransformer<RX, OIN, O> expectException(Class<O> exceptionClass, Function<? super T, ?> f) {
    requireNonNull(exceptionClass);
    return this.toThrowable(Functions.expectingException(exceptionClass, f));
  }

  default <E> ObjectTransformer<RX, OIN, E> toObject(Function<? super T, E> func) {
    requireNonNull(func);
    ObjectTransformer<RX, OIN, E> ret = new ObjectTransformer.Impl<>(this::rootValue, this.root());
    this.check(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default BooleanTransformer<RX, OIN> toBoolean(Function<? super T, Boolean> func) {
    requireNonNull(func);
    BooleanTransformer<RX, OIN> ret = new BooleanTransformer.Impl<>(this::rootValue, this.root());
    this.check(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default IntegerTransformer<RX, OIN> toInteger(Function<? super T, Integer> func) {
    requireNonNull(func);
    IntegerTransformer<RX, OIN> ret = new IntegerTransformer.Impl<>(this::rootValue,this.root());
    this.check(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default LongTransformer<RX, OIN> toLong(Function<? super T, Long> func) {
    requireNonNull(func);
    LongTransformer<RX, OIN> ret = new LongTransformer.Impl<>(this::rootValue, this.root());
    this.check(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default ShortTransformer<RX, OIN> toShort(Function<? super T, Short> func) {
    requireNonNull(func);
    ShortTransformer<RX, OIN> ret = new ShortTransformer.Impl<>(this::rootValue, this.root());
    this.check(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default DoubleTransformer<RX, OIN> toDouble(Function<? super T, Double> func) {
    requireNonNull(func);
    DoubleTransformer<RX, OIN> ret = new DoubleTransformer.Impl<>(this::rootValue, this.root());
    this.check(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default FloatTransformer<RX, OIN> toFloat(Function<? super T, Float> func) {
    requireNonNull(func);
    FloatTransformer<RX, OIN> ret = new FloatTransformer.Impl<>(this::rootValue, this.root());
    this.check(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default StringTransformer<RX, OIN> toString(Function<? super T, String> func) {
    requireNonNull(func);
    StringTransformer<RX, OIN> ret = new StringTransformer.Impl<>(this::rootValue, this.root());
    this.check(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default <E> ListTransformer<RX, OIN, E> toList(Function<? super T, List<E>> func) {
    requireNonNull(func);
    ListTransformer<RX, OIN, E> ret = new ListTransformer.Impl<>(this::rootValue, this.root());
    this.check(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default <E> StreamTransformer<RX, OIN, E> toStream(Function<? super T, Stream<E>> func) {
    requireNonNull(func);
    StreamTransformer<RX, OIN, E> ret = new StreamTransformer.Impl<>(this::rootValue, this.root());
    this.check(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default <E extends Throwable> ThrowableTransformer<RX, OIN, E> toThrowable(Function<? super T, E> func) {
    requireNonNull(func);
    ThrowableTransformer<RX, OIN, E> ret = new ThrowableTransformer.Impl<>(this::rootValue, this.root());
    this.check(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }
}