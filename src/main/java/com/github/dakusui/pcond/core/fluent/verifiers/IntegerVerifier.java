package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.integerVerifier;

public interface IntegerVerifier<OIN> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, Integer>,
    Verifier<IntegerVerifier<OIN>, OIN, Integer>,
    Matcher.ForInteger<OIN> {
  @Override
  IntegerVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue);

  default IntegerVerifier<OIN> equalTo(int v) {
    return predicate(Predicates.equalTo(v));
  }

  default IntegerVerifier<OIN> lessThan(int v) {
    return predicate(Predicates.lessThan(v));
  }

  default IntegerVerifier<OIN> lessThanOrEqualTo(int v) {
    return predicate(Predicates.lessThanOrEqualTo(v));
  }

  default IntegerVerifier<OIN> greaterThan(int v) {
    return predicate(Predicates.lessThan(v));
  }

  default IntegerVerifier<OIN> greaterThanOrEqualTo(int v) {
    return predicate(Predicates.lessThan(v));
  }

  class Impl<OIN> extends Verifier.Base<IntegerVerifier<OIN>, OIN, Integer> implements IntegerVerifier<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public Impl<OIN> create(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue) {
      return integerVerifier(transformerName, function, predicate, originalInputValue);
    }
  }
}
