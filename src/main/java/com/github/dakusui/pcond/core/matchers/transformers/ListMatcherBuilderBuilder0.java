package com.github.dakusui.pcond.core.matchers.transformers;

import com.github.dakusui.pcond.core.matchers.chckers.ListMatcherBuilder;
import com.github.dakusui.pcond.core.printable.Matcher;

import java.util.List;
import java.util.function.Function;

public class ListMatcherBuilderBuilder0<OIN, E> extends Matcher.Builder.Builder0<ListMatcherBuilderBuilder0<OIN, E>, OIN, List<E>> {
  /**
   * @param chain
   */
  public ListMatcherBuilderBuilder0(Function chain) {
    super(chain);
  }
}
