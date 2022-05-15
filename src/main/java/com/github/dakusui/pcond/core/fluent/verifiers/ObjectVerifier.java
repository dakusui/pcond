package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.function.Function;

public class ObjectVerifier<OIN, IM> extends Verifier<ObjectVerifier<OIN, IM>, OIN, IM> {
  public ObjectVerifier(String transformerName, Function<? super OIN, ? extends IM> function) {
    super(transformerName, function);
  }
}
