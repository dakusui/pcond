package com.github.dakusui.pcond.core.fluent3;

import com.github.dakusui.pcond.core.fluent3.typesupports.IntegerTransformer;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public interface Transformer<
    TX extends Transformer<TX, V, OIN, T>,
    V extends Checker<V, OIN, T>,
    OIN,
    T>
    extends Matcher<TX, OIN, T> {
  default V then() {
    V ret = createCorrespondingChecker(this.root());
    this.appendChild(tx -> ret.predicateForCurrentType());
    return ret;
  }

  default IntegerTransformer<OIN> toInteger(Function<? super T, Integer> func) {
    requireNonNull(func);
    IntegerTransformer<OIN> ret = new IntegerTransformer.Impl<>(this.originalInputValue(), this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.predicateForCurrentType())) ;
    return ret;
  }

  V createCorrespondingChecker(Matcher<?, OIN, OIN> root);
}
