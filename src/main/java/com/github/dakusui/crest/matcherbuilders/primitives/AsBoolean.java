package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.ObjectMatcherBuilder;
import com.github.dakusui.pcond.functions.Predicates;

import java.util.function.Function;

public class AsBoolean<IN> extends ObjectMatcherBuilder<IN, Boolean, AsBoolean<IN>> {
  public AsBoolean(Function<? super IN, ? extends Boolean> function) {
    super(function);
  }

  public AsBoolean<? super IN> isTrue() {
    this.check(Predicates.isTrue());
    return this;
  }

  public AsBoolean<? super IN> isFalse() {
    this.check(Predicates.isFalse());
    return this;
  }
}
