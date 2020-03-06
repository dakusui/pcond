package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.AsComparable;

import java.util.function.Function;

public class AsFloat<IN> extends AsComparable<IN, Float, AsFloat<IN>> {
  public AsFloat(Function<? super IN, ? extends Float> function) {
    super(function);
  }
}
