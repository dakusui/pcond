package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.transformers.extendable.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.extendable.IAbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent.verifiers.ObjectVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.IVerifier.Factory.objectVerifier;

public interface ObjectTransformer<OIN, OUT> extends ITransformer<ObjectTransformer<OIN, OUT>, OIN, OUT>, IAbstractObjectTransformer<ObjectTransformer<OIN, OUT>, OIN, OUT>, Matcher.ForObject<OIN, OUT> {
  @Override
  ObjectVerifier<OIN, OUT> then();

  class Impl<OIN, OUT> extends AbstractObjectTransformer<ObjectTransformer<OIN, OUT>, OIN, OUT> implements ObjectTransformer<OIN, OUT> {

    public <IN> Impl(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public ObjectVerifier<OIN, OUT> then() {
      return objectVerifier(this);
    }
  }
}
