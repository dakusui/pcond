package com.github.dakusui.pcond.core.fluent.transformers.extendable;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.transformers.ObjectTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.StringTransformer;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.forms.Functions;

import java.util.function.Function;

public abstract class AbstractObjectTransformer<TX extends AbstractObjectTransformer<TX, OIN, OUT>, OIN, OUT>
    extends Transformer<TX, OIN, OUT> {

  public <IN> AbstractObjectTransformer(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function) {
    super(transformerName, parent, function);
  }

  /**
   * Corresponds to {@code toString()} method.
   *
   * @return this object the method appended.
   */
  public StringTransformer<OIN> stringify() {
    return this.transformToString(Functions.stringify());
  }


  ////
  // BEGIN: Methods for java.lang.Object come here.

  public <NOUT> ObjectTransformer<OIN, NOUT> cast(Class<NOUT> klass) {
    return this.transformToObject(Functions.cast(klass));
  }

  public <NOUT> ObjectTransformer<OIN, NOUT> invoke(String methodName, Object... args) {
    return this.transformToObject(Functions.call(MethodQuery.instanceMethod(
        Functions.parameter(), methodName, args)));
  }

  public <NOUT> ObjectTransformer<OIN, NOUT> invokeStatic(Class<?> klass, String methodName, Object... args) {
    return this.transformToObject(Functions.call(MethodQuery.classMethod(klass, methodName, args)));
  }
  // END: Methods for java.lang.Object come here.
  ////


  @SuppressWarnings("unchecked")
  public <T, RTX extends AbstractObjectTransformer<RTX, OIN, T>> RTX castTo(T value) {
    return (RTX) this.exercise(Functions.castTo(value));
  }
}
