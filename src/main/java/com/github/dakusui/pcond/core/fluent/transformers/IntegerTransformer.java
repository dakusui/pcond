package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.fluent.verifiers.IntegerVerifier;
import com.github.dakusui.pcond.forms.Functions;

import java.util.function.Function;

public class IntegerTransformer<OIN> extends Transformer<IntegerTransformer<OIN>, OIN, Integer> {
  /**
   * @param function
   */
  public IntegerTransformer(Function<? super OIN, ? extends Integer> function) {
    super(function);
  }

  @Override
  public Verifier<?, OIN, Integer> then() {
    return this.then(null);
  }

  @Override
  public Verifier<?, OIN, Integer> then(Function<Integer, Integer> converter) {
    return thenAsInteger(converter);
  }
}
