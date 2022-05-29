package com.github.dakusui.pcond.core.fluent.transformers.extendable;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.transformers.IObjectTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.IStringTransformer;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.forms.Functions;

public interface IAbstractObjectTransformer<TX extends IAbstractObjectTransformer<TX, OIN, OUT>, OIN, OUT> extends ITransformer<TX, OIN, OUT> {
  /**
   * Corresponds to {@code toString()} method.
   *
   * @return this object the method appended.
   */
  default IStringTransformer<OIN> stringify() {
    return this.transformToString(Functions.stringify());
  }

  default <NOUT> IObjectTransformer<OIN, NOUT> cast(Class<NOUT> klass) {
    return this.transformToObject(Functions.cast(klass));
  }

  default <NOUT> IObjectTransformer<OIN, NOUT> invoke(String methodName, Object... args) {
    return this.transformToObject(Functions.call(MethodQuery.instanceMethod(
        Functions.parameter(), methodName, args)));
  }

  default <NOUT> IObjectTransformer<OIN, NOUT> invokeStatic(Class<?> klass, String methodName, Object... args) {
    return this.transformToObject(Functions.call(MethodQuery.classMethod(klass, methodName, args)));
  }

  @SuppressWarnings("unchecked")
  default <T, RTX extends IAbstractObjectTransformer<RTX, OIN, T>> RTX castTo(T value) {
    return (RTX) this.exercise(Functions.castTo(value));
  }
}
