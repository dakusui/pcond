package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.function.Function;
import java.util.function.Predicate;

public class ObjectVerifier<OIN, OUT>
    extends Verifier<IObjectVerifier<OIN, OUT>, OIN, OUT>
    implements IObjectVerifier<OIN, OUT> {
  public ObjectVerifier(String transformerName, Function<? super OIN, ? extends OUT> function, Predicate<? super OUT> predicate, OIN originalInputValue) {
    super(transformerName, function, predicate, originalInputValue);
  }

  @Override
  public IObjectVerifier<OIN, OUT> create(String transformerName, Function<? super OIN, ? extends OUT> function, Predicate<? super OUT> predicate, OIN originalInputValue) {
    return new ObjectVerifier<>(transformerName, function, predicate, originalInputValue);
  }
}
