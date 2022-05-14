package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;

public class StringVerifier<OIN> extends Verifier<StringVerifier<OIN>, OIN, String> {
  public StringVerifier(Function<? super OIN, ? extends String> function) {
    super(function);
  }

  public StringVerifier<OIN> contains(String token) {
    return (StringVerifier<OIN>) this.predicate(Predicates.containsString(token));
  }

  public void method() {

  }
}
