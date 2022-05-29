package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.function.Function;
import java.util.function.Predicate;

public class IntegerVerifier<OIN> extends Verifier<IIntegerVerifier<OIN>, OIN, Integer> implements IIntegerVerifier<OIN> {
  public IntegerVerifier(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue) {
    super(transformerName, function, predicate, originalInputValue);
  }

  @Override
  public IntegerVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue) {
    return new IntegerVerifier<>(transformerName, function, predicate, originalInputValue);
  }
}
