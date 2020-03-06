package com.github.dakusui.crest.matcherbuilders;

import java.util.function.Function;

public class AsObject<IN, OUT> extends ObjectMatcherBuilder<IN, OUT, AsObject<IN, OUT>> {
  public AsObject(Function<? super IN, ? extends OUT> function) {
    super(function);
  }
}
