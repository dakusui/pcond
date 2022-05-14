package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.List;
import java.util.function.Function;

public class ListVerifier<OIN, E> extends Verifier<ListVerifier<OIN, E>, OIN, List<E>> {
  public ListVerifier(Function<? super OIN, ? extends List<E>> function) {
    super(function);
  }
}
