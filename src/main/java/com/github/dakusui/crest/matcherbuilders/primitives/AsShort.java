package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.AsComparable;

import java.util.function.Function;

public class AsShort<IN> extends AsComparable<IN, Short, AsShort<IN>> {
  public AsShort(Function<? super IN, ? extends Short> function) {
    super(function);
  }
}
