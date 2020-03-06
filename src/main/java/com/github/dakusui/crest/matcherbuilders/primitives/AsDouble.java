package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.AsComparable;

import java.util.function.Function;

public class AsDouble<IN> extends AsComparable<IN, Double, AsDouble<IN>> {
  public AsDouble(Function<? super IN, ? extends Double> function) {
    super(function);
  }
}
