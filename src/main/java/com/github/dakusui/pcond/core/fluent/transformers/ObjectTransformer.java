package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.IVerifier;
import com.github.dakusui.pcond.core.fluent.transformers.extendable.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent.verifiers.IObjectVerifier;

import java.util.function.Function;

public class ObjectTransformer<OIN, OUT> extends AbstractObjectTransformer<IObjectTransformer<OIN, OUT>, OIN, OUT> implements IObjectTransformer<OIN, OUT> {

  public <IN> ObjectTransformer(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function, OIN originalInputValue) {
    super(transformerName, parent, function, originalInputValue);
  }

  @Override
  public IObjectVerifier<OIN, OUT> then() {
    return IVerifier.Factory.objectVerifier(this);
  }
}
