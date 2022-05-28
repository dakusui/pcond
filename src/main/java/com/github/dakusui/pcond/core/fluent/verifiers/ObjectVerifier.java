package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.function.Function;
import java.util.function.Predicate;

public class ObjectVerifier<OIN, OUT>
    extends Verifier<ObjectVerifier<OIN, OUT>, OIN, OUT>
    implements Matcher.ForObject<OIN, OUT> {
  public ObjectVerifier(String transformerName, Function<? super OIN, ? extends OUT> function, Predicate<? super OUT> predicate) {
    super(transformerName, function, predicate);
  }

  @Override
  public ObjectVerifier<OIN, OUT> create() {
    return new ObjectVerifier<>(this.transformerName, this.function(), this.predicate());
  }
}
