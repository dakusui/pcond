package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;

import java.util.function.Function;

public class IntegerTransformer<OIN> extends Transformer<IntegerTransformer<OIN>, OIN, Integer> {
  /**
   * @param function
   */
  public IntegerTransformer(Function<? super OIN, ? extends Integer> function) {
    super(function);
  }
}
