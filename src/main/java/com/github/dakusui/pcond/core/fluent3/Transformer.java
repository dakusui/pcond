package com.github.dakusui.pcond.core.fluent3;

import com.github.dakusui.pcond.core.fluent3.typesupports.IntegerTransformer;
import com.github.dakusui.pcond.core.fluent3.typesupports.ListTransformer;
import com.github.dakusui.pcond.core.fluent3.typesupports.StringTransformer;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.List;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public interface Transformer<
    TX extends Transformer<TX, RX, V, OIN, T>,
    RX extends Matcher<RX, RX, OIN, OIN>,
    V extends Checker<V, RX, OIN, T>,
    OIN,
    T>
    extends Matcher<TX, RX, OIN, T> {
  default V then() {
    V ret = createCorrespondingChecker(this.root());
    this.appendChild(tx -> ret.toPredicate());
    return ret;
  }

  default IntegerTransformer<OIN, RX> toInteger(Function<? super T, Integer> func) {
    requireNonNull(func);
    IntegerTransformer<OIN, RX> ret = new IntegerTransformer.Impl<>(this.rootValue(), this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.toPredicate())) ;
    return ret;
  }

  default StringTransformer<OIN, RX> toString(Function<? super T, String> func) {
    requireNonNull(func);
    StringTransformer<OIN, RX> ret = new StringTransformer.Impl<>(this.rootValue(), this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.toPredicate())) ;
    return ret;
  }

  default <E> ListTransformer<OIN, RX, E> toList(Function<? super T, List<E>> func) {
    requireNonNull(func);
    ListTransformer<OIN, RX, E> ret = new ListTransformer.Impl<>(this.rootValue(), this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.toPredicate())) ;
    return ret;
  }


  V createCorrespondingChecker(RX root);
}
