package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.doubleVerifier;

public interface DoubleVerifier<OIN> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, Double>,
    Verifier<DoubleVerifier<OIN>, OIN, Double>,
    Matcher.ForDouble<OIN> {
  default DoubleVerifier<OIN> equalTo(double v) {
    return predicate(Predicates.equalTo(v));
  }

  default DoubleVerifier<OIN> lessThan(double v) {
    return predicate(Predicates.lessThan(v));
  }

  default DoubleVerifier<OIN> lessThanOrEqualTo(double v) {
    return predicate(Predicates.lessThanOrEqualTo(v));
  }

  default DoubleVerifier<OIN> greaterThan(double v) {
    return predicate(Predicates.lessThan(v));
  }

  default DoubleVerifier<OIN> greaterThanOrEqualTo(double v) {
    return predicate(Predicates.lessThan(v));
  }

  class Impl<OIN> extends Verifier.Base<DoubleVerifier<OIN>, OIN, Double> implements DoubleVerifier<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends Double> function, Predicate<? super Double> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public DoubleVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends Double> function, Predicate<? super Double> predicate, OIN originalInputValue) {
      return doubleVerifier(transformerName, function, predicate, originalInputValue);
    }
  }
}
