package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;

public class IntVerifier<OIN> extends Verifier<IntVerifier<OIN>, OIN, Integer> {
  public IntVerifier(Function<? super OIN, ? extends Integer> function) {
    super(function);
  }

  public IntVerifier<OIN> equalTo(int v) {
    return predicate(Predicates.equalTo(v));
  }

  public IntVerifier<OIN> lessThan(int v) {
    return predicate(Predicates.lessThan(v));
  }

  public IntVerifier<OIN> lessThanOrEqualTo(int v) {
    return predicate(Predicates.lessThanOrEqualTo(v));
  }

  public IntVerifier<OIN> greaterThan(int v) {
    return predicate(Predicates.lessThan(v));
  }

  public IntVerifier<OIN> greaterThanOrEqualTo(int v) {
    return predicate(Predicates.lessThan(v));
  }
}
