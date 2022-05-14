package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.function.Function;

public class ObjectMatcherBuilder<OIN, IM> extends Verifier<ObjectMatcherBuilder<OIN, IM>, OIN, IM> {
  public ObjectMatcherBuilder(Function<? super OIN, ? extends IM> function) {
    super(function);
  }
}
