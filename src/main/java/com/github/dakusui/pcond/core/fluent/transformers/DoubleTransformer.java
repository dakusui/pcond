package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.DoubleVerifier;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.doubleVerifier;

public interface DoubleTransformer<OIN> extends NumberTransformer<DoubleTransformer<OIN>, DoubleVerifier<OIN>, OIN, Double>, Matcher.ForDouble<OIN> {
  @Override
  DoubleVerifier<OIN> then();

  class Impl<OIN> extends Base<DoubleTransformer<OIN>, OIN, Double> implements DoubleTransformer<OIN> {
    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Double> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public DoubleVerifier<OIN> then() {
      return doubleVerifier(this.transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
    }
  }
}
