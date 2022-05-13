package com.github.dakusui.pcond.core.matchers.transformers;

import com.github.dakusui.pcond.core.printable.Matcher;

import java.util.function.Function;

public class ObjectMatcherBuilderBuilder0<OIN> extends Matcher.Builder.Builder0<ObjectMatcherBuilderBuilder0<OIN>, OIN, Object> {

  /**
   */
  public ObjectMatcherBuilderBuilder0(Function<? super OIN, Object> function) {
    super(function);
  }
}
