package com.github.dakusui.pcond.core.fluent.transformers.extendable;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.ObjectVerifier;
import com.github.dakusui.pcond.forms.Functions;

import java.util.function.Function;

public abstract class AbstractObjectTransformer<TX extends AbstractObjectTransformer<TX, OIN, OUT>, OIN, OUT>
    extends Transformer<TX, OIN, OUT> {

  /**
   * @param transformerName
   * @param parent
   * @param function
   */
  public <IN> AbstractObjectTransformer(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function) {
    super(transformerName, parent, function);
  }

  @Override
  public ObjectVerifier<OIN, OUT> then() {
    return then(Functions.identity());
  }

  @Override
  public ObjectVerifier<OIN, OUT> then(Function<OUT, OUT> converter) {
    return thenAsObject(converter);
  }

  @SuppressWarnings("unchecked")
  public <T, RTX extends AbstractObjectTransformer<RTX, OIN, T>> RTX castTo(T value) {
    return (RTX) this;
  }
}
