package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.function.Function;
import java.util.function.Predicate;

public class BooleanVerifier<OIN> extends Verifier<BooleanVerifier<OIN>, OIN, Boolean> implements Matcher.ForBoolean<OIN> {
  public BooleanVerifier(String transformerName, Function<? super OIN, ? extends Boolean> function, Predicate<? super Boolean> predicate) {
    super(transformerName, function, predicate);
  }

  @Override
  protected BooleanVerifier<OIN> create() {
    return null;
  }
}
