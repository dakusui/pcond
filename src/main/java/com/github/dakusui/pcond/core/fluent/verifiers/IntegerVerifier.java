package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;

public class IntegerVerifier<OIN> extends Verifier<IntegerVerifier<OIN>, OIN, Integer> {
  public IntegerVerifier(Function<? super OIN, ? extends Integer> function) {
    super(function);
  }

  public IntegerVerifier<OIN> equalTo(int v) {
    return predicate(Predicates.equalTo(v));
  }

  public IntegerVerifier<OIN> lessThan(int v) {
    return predicate(Predicates.lessThan(v));
  }

  public IntegerVerifier<OIN> lessThanOrEqualTo(int v) {
    return predicate(Predicates.lessThanOrEqualTo(v));
  }

  public IntegerVerifier<OIN> greaterThan(int v) {
    return predicate(Predicates.lessThan(v));
  }

  public IntegerVerifier<OIN> greaterThanOrEqualTo(int v) {
    return predicate(Predicates.lessThan(v));
  }
}
