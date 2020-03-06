package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.AsComparable;

import java.util.function.Function;

public class AsByte<IN> extends AsComparable<IN, Byte, AsByte<IN>> {
  public AsByte(Function<? super IN, ? extends Byte> function) {
    super(function);
  }
}
