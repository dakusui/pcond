package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.function.Function;
import java.util.function.Predicate;

public class BooleanVerifier<OIN> extends Verifier<IBooleanVerifier<OIN>, OIN, Boolean> implements IBooleanVerifier<OIN> {
  public BooleanVerifier(String transformerName, Function<? super OIN, ? extends Boolean> function, Predicate<? super Boolean> predicate, OIN originalInputValue) {
    super(transformerName, function, predicate, originalInputValue);
  }

  @Override
  public IBooleanVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends Boolean> function, Predicate<? super Boolean> predicate, OIN originalInputValue) {
    return new BooleanVerifier<>(transformerName, function, predicate, originalInputValue);
  }
}
