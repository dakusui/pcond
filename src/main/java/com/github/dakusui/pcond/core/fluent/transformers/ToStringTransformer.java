package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.forms.Printables;

import java.util.function.Function;

public class ToStringTransformer<OIN> extends Transformer<ToStringTransformer<OIN>, OIN, String> {
  /**
   * @param chain
   */
  public <COUT> ToStringTransformer(Function<? super COUT, ? extends String> chain) {
    super(chain);
  }

  public ToStringTransformer<OIN> substring(int begin) {
    return this.transformToString(Printables.function(() -> "substring[" + begin + "]", s -> s.substring(begin)));
  }

  public ToStringTransformer<OIN> toUpperCase() {
    return this.transformToString(Printables.function("toUpperCase", String::toUpperCase));
  }
}
