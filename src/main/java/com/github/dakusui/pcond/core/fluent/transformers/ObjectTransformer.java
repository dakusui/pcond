package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.fluent.transformers.extendable.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.core.fluent.verifiers.ObjectVerifier;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;

public class ObjectTransformer<OIN, OUT> extends AbstractObjectTransformer<ObjectTransformer<OIN, OUT>, OIN, OUT> implements Matcher.ForObject<OIN, OUT> {

  /**
   * @param transformerName
   * @param parent
   * @param function
   */
  public <IN> ObjectTransformer(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function) {
    super(transformerName, parent, function);
  }

  @Override
  public Verifier<?, OIN, OUT> then() {
    return new ObjectVerifier<>(transformerName(), this.function(), InternalUtils.dummyPredicate());
  }
}
