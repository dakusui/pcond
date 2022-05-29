package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.fluent.transformers.extendable.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent.verifiers.ObjectVerifier;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;

public class ObjectTransformer<OIN, OUT> extends AbstractObjectTransformer<IObjectTransformer<OIN, OUT>, OIN, OUT> implements IObjectTransformer<OIN, OUT> {

  public <IN> ObjectTransformer(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function, OIN originalInputValue) {
    super(transformerName, parent, function, originalInputValue);
  }

  @Override
  public Function<? super OIN, ? extends OUT> function() {
    return null;
  }

  @Override
  public String transformerName() {
    return null;
  }

  @Override
  public OIN originalInputValue() {
    return null;
  }

  @Override
  public Verifier<?, OIN, OUT> then() {
    return new ObjectVerifier<>(transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
  }
}
