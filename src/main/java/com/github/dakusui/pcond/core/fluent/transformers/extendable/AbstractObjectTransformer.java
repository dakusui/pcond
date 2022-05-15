package com.github.dakusui.pcond.core.fluent.transformers.extendable;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.ObjectVerifier;

import java.util.function.Function;

public class AbstractObjectTransformer<TX extends AbstractObjectTransformer<TX, OIN, OUT>, OIN, OUT>
    extends Transformer<TX, OIN, OUT> {

  /**
   * @param parent
   * @param function
   */
  public <IN> AbstractObjectTransformer(Transformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function) {
    super(parent, function);
  }

  @Override
  public ObjectVerifier<OIN, OUT> then() {
    return then(Function.identity());
  }

  @Override
  public ObjectVerifier<OIN, OUT> then(Function<OUT, OUT> converter) {
    return thenAsObject(converter);
  }
}
