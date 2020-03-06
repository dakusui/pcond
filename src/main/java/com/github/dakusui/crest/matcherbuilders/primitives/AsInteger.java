package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.AsComparable;

import java.util.function.Function;

public class AsInteger<IN> extends AsComparable<IN, Integer, AsInteger<IN>> {
  public AsInteger(Function<? super IN, ? extends Integer> function) {
    super(function);
  }
}
