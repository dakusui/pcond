package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.transformers.extendable.AbstractObjectTransformer;

import java.util.function.Function;

public class ObjectTransformer<OIN, OUT> extends AbstractObjectTransformer<ObjectTransformer<OIN, OUT>, OIN, OUT> {

  /**
   * @param transformerName
   * @param parent
   * @param function
   */
  public <IN> ObjectTransformer(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function) {
    super(transformerName, parent, function);
  }
}
