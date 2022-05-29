package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.IIntegerVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.IVerifier.Factory.integerVerifier;

public interface IIntegerTransformer<OIN> extends ITransformer<IIntegerTransformer<OIN>, OIN, Integer>, Matcher.ForInteger<OIN> {
  @Override
  IIntegerVerifier<OIN> then();

  class Impl<OIN> extends Transformer<IIntegerTransformer<OIN>, OIN, Integer> implements IIntegerTransformer<OIN> {
    public <IN> Impl(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends Integer> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public IIntegerVerifier<OIN> then() {
      return integerVerifier(this.transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
    }
  }
}
