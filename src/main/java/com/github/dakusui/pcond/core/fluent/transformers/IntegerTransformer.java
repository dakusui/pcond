package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.IIntegerVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.IntegerVerifier;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;

public class IntegerTransformer<OIN> extends Transformer<IIntegerTransformer<OIN>, OIN, Integer> implements IIntegerTransformer<OIN> {
  public <IN> IntegerTransformer(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends Integer> function, OIN originalInputValue) {
    super(transformerName, parent, function, originalInputValue);
  }

  @Override
  public IIntegerVerifier<OIN> then() {
    return new IntegerVerifier<>(this.transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
  }
}
