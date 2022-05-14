package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;

import java.util.function.Function;

public class ToObjectTransformer<OIN, COUT> extends Transformer<ToObjectTransformer<OIN, COUT>, OIN, COUT> {

  /**
   */
  public <P> ToObjectTransformer(Function<P, COUT> function) {
    super(function);
  }
}
