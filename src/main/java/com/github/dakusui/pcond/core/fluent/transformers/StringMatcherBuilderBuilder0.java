package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.forms.Printables;

import java.util.function.Function;

public class StringMatcherBuilderBuilder0<OIN> extends Transformer<StringMatcherBuilderBuilder0<OIN>, OIN, String> {
  /**
   * @param chain
   */
  public <COUT> StringMatcherBuilderBuilder0(Function<? super COUT, ? extends String> chain) {
    super(chain);
  }

  public StringMatcherBuilderBuilder0<OIN> substring(int begin) {
    return this.chainToString(Printables.function(() -> "substring[" + begin + "]", s -> s.substring(begin)));
  }

  public StringMatcherBuilderBuilder0<OIN> toUpperCase() {
    return this.chainToString(Printables.function("toUpperCase", String::toUpperCase));
  }
}
