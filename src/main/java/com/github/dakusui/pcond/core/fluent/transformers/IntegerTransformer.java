package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;

import java.util.function.Function;

public class IntegerTransformer<OIN> extends Transformer<IntegerTransformer<OIN>, OIN, Integer> implements Matcher.ForInteger<OIN> {


  /**
   * @param transformerName
   * @param parent
   * @param function
   */
  public <IN> IntegerTransformer(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Integer> function) {
    super(transformerName, parent, function);
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
