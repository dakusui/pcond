package com.github.dakusui.pcond.core.fluent.transformers.extendable;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.transformers.ObjectTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.StringTransformer;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.forms.Functions;

import java.util.function.Function;

public interface AbstractObjectTransformer<TX extends AbstractObjectTransformer<TX, OIN, OUT>, OIN, OUT> extends Transformer<TX, OIN, OUT> {
  /**
   * Corresponds to {@code toString()} method.
   *
   * @return this object the method appended.
   */
  default StringTransformer<OIN> stringify() {
    return this.toString(Functions.stringify());
  }

  default <NOUT> ObjectTransformer<OIN, NOUT> cast(Class<NOUT> klass) {
    return this.toObject(Functions.cast(klass));
  }

  default <NOUT> ObjectTransformer<OIN, NOUT> invoke(String methodName, Object... args) {
    return this.toObject(Functions.call(MethodQuery.instanceMethod(
        Functions.parameter(), methodName, args)));
  }

  default <NOUT> ObjectTransformer<OIN, NOUT> invokeStatic(Class<?> klass, String methodName, Object... args) {
    return this.toObject(Functions.call(MethodQuery.classMethod(klass, methodName, args)));
  }


  abstract class Base<TX extends AbstractObjectTransformer<TX, OIN, OUT>, OIN, OUT>
      extends Transformer.Base<TX, OIN, OUT> implements AbstractObjectTransformer<TX, OIN, OUT> {

    public <IN> Base(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }
  }
}
