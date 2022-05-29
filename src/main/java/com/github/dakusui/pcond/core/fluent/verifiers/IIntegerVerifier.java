package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.IVerifier;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.IVerifier.Factory.integerVerifier;

public interface IIntegerVerifier<OIN> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, Integer>,
    IVerifier<IIntegerVerifier<OIN>, OIN, Integer>,
    Matcher.ForInteger<OIN> {
  @Override
  IIntegerVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue);

  default IIntegerVerifier<OIN> equalTo(int v) {
    return predicate(Predicates.equalTo(v));
  }

  default IIntegerVerifier<OIN> lessThan(int v) {
    return predicate(Predicates.lessThan(v));
  }

  default IIntegerVerifier<OIN> lessThanOrEqualTo(int v) {
    return predicate(Predicates.lessThanOrEqualTo(v));
  }

  default IIntegerVerifier<OIN> greaterThan(int v) {
    return predicate(Predicates.lessThan(v));
  }

  default IIntegerVerifier<OIN> greaterThanOrEqualTo(int v) {
    return predicate(Predicates.lessThan(v));
  }

  class Impl<OIN> extends Verifier<IIntegerVerifier<OIN>, OIN, Integer> implements IIntegerVerifier<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public Impl<OIN> create(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue) {
      return integerVerifier(transformerName, function, predicate, originalInputValue);
    }
  }
}
