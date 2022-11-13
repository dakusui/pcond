package com.github.dakusui.pcond.core.fluent3;

import com.github.dakusui.pcond.core.fluent3.builtins.ObjectTransformer;
import com.github.dakusui.pcond.core.fluent3.builtins.StringTransformer;
import com.github.dakusui.pcond.forms.Functions;

import static com.github.dakusui.pcond.core.refl.MethodQuery.classMethod;
import static com.github.dakusui.pcond.core.refl.MethodQuery.instanceMethod;
import static com.github.dakusui.pcond.forms.Functions.call;
import static com.github.dakusui.pcond.forms.Functions.parameter;

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
  default StringTransformer<OIN, RX> stringify() {
    return this.toString(Functions.stringify());
  }

  default <E> ObjectTransformer<RX, OIN, E> invoke(String methodName, Object... args) {
    return this.toObject(call(instanceMethod(parameter(), methodName, args)));
  }

  default <E> ObjectTransformer<RX, OIN, E> invokeStatic(Class<?> klass, String methodName, Object... args) {
    return this.toObject(call(classMethod(klass, methodName, args)));
  }
}