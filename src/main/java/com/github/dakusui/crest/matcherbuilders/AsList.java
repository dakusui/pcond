package com.github.dakusui.crest.matcherbuilders;

import java.util.List;
import java.util.function.Function;

public class AsList<IN, ENTRY> extends ListMatcherBuilder<IN, ENTRY, AsList<IN, ENTRY>> {
  public AsList(Function<? super IN, ? extends List<ENTRY>> function) {
    super(function);
  }
}
