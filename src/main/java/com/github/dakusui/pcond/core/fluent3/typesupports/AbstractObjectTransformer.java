package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.Checker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.core.fluent3.Transformer;
import com.github.dakusui.pcond.forms.Functions;

public interface AbstractObjectTransformer<
    TX extends Transformer<TX, RX, V, OIN, T>,
    RX extends Matcher<RX, RX, OIN, OIN>,
    V extends Checker<V, RX, OIN, T>,
    OIN,
    T> extends
    Transformer<TX, RX, V, OIN, T> {
  /**
   * Corresponds to {@code toString()} method.
   *
   * @return this object the method appended.
   */
  default StringTransformer<OIN, RX> stringify() {
    return this.toString(Functions.stringify());
  }

}
