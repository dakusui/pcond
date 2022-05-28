package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.function.Function;
import java.util.function.Predicate;

public class StringVerifier<OIN> extends Verifier<IStringVerifier<OIN>, OIN, String> implements IStringVerifier<OIN> {
  public StringVerifier(String transformerName, Function<? super OIN, ? extends String> function, Predicate<? super String> predicate) {
    super(transformerName, function, predicate);
  }

  @Override
  public IStringVerifier<OIN> create() {
    return new StringVerifier<>(this.transformerName(), this.function(), this.predicate());
  }
}
