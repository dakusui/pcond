package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;

import java.util.List;
import java.util.function.Function;

public class ToListTransformer<OIN, E> extends Transformer<ToListTransformer<OIN, E>, OIN, List<E>> {
  /**
   * @param chain
   */
  public ToListTransformer(Function chain) {
    super(chain);
  }
}
