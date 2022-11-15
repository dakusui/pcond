package com.github.dakusui.pcond.core.fluent3;

public interface Transformer<
    TX extends Transformer<TX, RX, V, OIN, T>,
    RX extends Matcher<RX, RX, OIN, OIN>,
    V extends Checker<V, RX, OIN, T>,
    OIN,
    T>
    extends Matcher<TX, RX, OIN, T> {
  default V then() {
    V ret = createCorrespondingChecker(this.root());
    this.check(tx -> ret.toPredicate());
    return ret;
  }

  V createCorrespondingChecker(RX root);
}
