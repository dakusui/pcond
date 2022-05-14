package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;

import java.util.List;
import java.util.function.Function;

public class ListMatcherBuilderBuilder0<OIN, E> extends Transformer<ListMatcherBuilderBuilder0<OIN, E>, OIN, List<E>> {
  /**
   * @param chain
   */
  public ListMatcherBuilderBuilder0(Function chain) {
    super(chain);
  }
}
