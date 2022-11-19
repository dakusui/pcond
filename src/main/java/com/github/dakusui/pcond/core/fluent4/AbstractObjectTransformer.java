package com.github.dakusui.pcond.core.fluent4;

import com.github.dakusui.pcond.core.fluent4.sandbox.StringTransformer;
import com.github.dakusui.pcond.forms.Functions;

public interface AbstractObjectTransformer<
    TX extends AbstractObjectTransformer<TX, V, T, R>,
    V extends AbstractObjectChecker<V, T, R>,
    T,
    R
    > extends
    Transformer<TX, V, T, R> {
  /**
   * Corresponds to {@code toString()} method.
   *
   * @return this object the method appended.
   */
  default StringTransformer<T> stringify() {
    return this.toString(Functions.stringify());
  }
}
