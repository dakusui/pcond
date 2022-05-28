package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.fluent.verifiers.IntegerVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;

public class IntegerTransformer<OIN> extends Transformer<IntegerTransformer<OIN>, OIN, Integer> implements Matcher.ForInteger<OIN> {


  public <IN> IntegerTransformer(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends Integer> function) {
    super(transformerName, parent, function);
  }

  @Override
  public Verifier<?, OIN, Integer> then() {
    return new IntegerVerifier<>(this.transformerName(), this.function(), InternalUtils.dummyPredicate());
  }
}
