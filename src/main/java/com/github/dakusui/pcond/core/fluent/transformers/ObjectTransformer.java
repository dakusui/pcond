package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;

import java.util.function.Function;

public class ObjectTransformer<OIN, COUT> extends Transformer<ObjectTransformer<OIN, COUT>, OIN, COUT> {

  /**
   */
  public ObjectTransformer(Function<? super OIN, COUT> function) {
    super(function);
  }
}
