package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.function.Function;

public class ObjectKVerifier<OIN, IM> extends Verifier<ObjectKVerifier<OIN, IM>, OIN, IM> {
  public ObjectKVerifier(Function<? super OIN, ? extends IM> function) {
    super(function);
  }
}
