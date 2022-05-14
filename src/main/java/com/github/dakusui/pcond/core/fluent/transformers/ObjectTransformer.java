package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.fluent.verifiers.ObjectVerifier;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class ObjectTransformer<OIN, COUT> extends Transformer<ObjectTransformer<OIN, COUT>, OIN, COUT> {

  /**
   *
   */
  public ObjectTransformer(Function<? super OIN, COUT> function) {
    super(function);
  }

  @Override
  public ObjectVerifier<OIN, COUT> then() {
    return then(Function.identity());
  }

  @Override
  public ObjectVerifier<OIN, COUT> then(Function<COUT, COUT> converter) {
    return thenAsObject(converter);
  }
}
