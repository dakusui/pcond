package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.AsComparable;

import java.util.function.Function;

public class AsChar<IN> extends AsComparable<IN, Character, AsChar<IN>> {
  public AsChar(Function<? super IN, ? extends Character> function) {
    super(function);
  }
}
