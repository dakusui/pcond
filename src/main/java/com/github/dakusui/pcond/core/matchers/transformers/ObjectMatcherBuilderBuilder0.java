package com.github.dakusui.pcond.core.matchers.transformers;

import com.github.dakusui.pcond.core.printable.Matcher;

import java.util.function.Function;

public class ObjectMatcherBuilderBuilder0<OIN, COUT> extends Matcher.Builder.Builder0<ObjectMatcherBuilderBuilder0<OIN, COUT>, OIN, COUT> {

  /**
   */
  public <P> ObjectMatcherBuilderBuilder0(Function<P, COUT> function) {
    super(function);
  }
}
