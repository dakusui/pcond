package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.List;
import java.util.function.Function;

public class ListMatcherBuilder<OIN, E> extends Verifier<ListMatcherBuilder<OIN, E>, OIN, List<E>> {
  public ListMatcherBuilder(Function<? super OIN, ? extends List<E>> function) {
    super(function);
  }
}
