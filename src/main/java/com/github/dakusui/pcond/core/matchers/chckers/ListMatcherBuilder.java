package com.github.dakusui.pcond.core.matchers.chckers;

import com.github.dakusui.pcond.core.printable.Matcher;

import java.util.List;
import java.util.function.Function;

public class ListMatcherBuilder<OIN, E> extends Matcher.Builder<ListMatcherBuilder<OIN, E>, OIN, List<E>> {
  public ListMatcherBuilder(Function<? super OIN, ? extends List<E>> function) {
    super(function);
  }
}
