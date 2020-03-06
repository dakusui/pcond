package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.AsComparable;

import java.util.function.Function;

public class AsLong<IN> extends AsComparable<IN, Long, AsLong<IN>> {
  public AsLong(Function<? super IN, ? extends Long> function) {
    super(function);
  }
}
