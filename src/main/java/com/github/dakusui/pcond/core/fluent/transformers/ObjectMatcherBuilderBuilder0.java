package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;

import java.util.function.Function;

public class ObjectMatcherBuilderBuilder0<OIN, COUT> extends Transformer<ObjectMatcherBuilderBuilder0<OIN, COUT>, OIN, COUT> {

  /**
   */
  public <P> ObjectMatcherBuilderBuilder0(Function<P, COUT> function) {
    super(function);
  }
}
