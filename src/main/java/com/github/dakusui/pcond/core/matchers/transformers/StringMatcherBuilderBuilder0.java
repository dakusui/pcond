package com.github.dakusui.pcond.core.matchers.transformers;

import com.github.dakusui.pcond.core.printable.Matcher;
import com.github.dakusui.pcond.forms.Printables;

import java.util.function.Function;

public class StringMatcherBuilderBuilder0<IN> extends Matcher.Builder.Builder0<StringMatcherBuilderBuilder0<IN>, IN, String> {
  /**
   * @param chain
   */
  public StringMatcherBuilderBuilder0(Function<? super IN, ? extends String> chain) {
    super(chain);
  }

  public StringMatcherBuilderBuilder0<IN> substring(int begin) {
    return this.chain(Printables.function(() -> "substring[" + begin + "]", s -> s.substring(begin)));
  }

  public StringMatcherBuilderBuilder0<IN> toUpperCase() {
    return this.chain(Printables.function("toUpperCase", String::toUpperCase));
  }
}
