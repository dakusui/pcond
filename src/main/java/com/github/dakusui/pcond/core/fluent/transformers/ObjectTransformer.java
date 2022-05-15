package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.transformers.extendable.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent.verifiers.ObjectVerifier;

import java.util.function.Function;

public class ObjectTransformer<OIN, OUT> extends AbstractObjectTransformer<ObjectTransformer<OIN, OUT>, OIN, OUT> {

  /**
   * @param parent
   * @param function
   */
  public <IN> ObjectTransformer(Transformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function) {
    super(parent, function);
  }
}
