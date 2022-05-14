package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Printables;

import java.util.function.Function;

public class StringTransformer<OIN> extends Transformer<StringTransformer<OIN>, OIN, String> {
  /**
   * @param chain
   */
  public StringTransformer(Function<OIN, ? extends String> chain) {
    super(chain);
  }

  public StringTransformer<OIN> substring(int begin) {
    return this.transformToString(Printables.function(() -> "substring[" + begin + "]", s -> s.substring(begin)));
  }

  public StringTransformer<OIN> toUpperCase() {
    return this.transformToString(Printables.function("toUpperCase", String::toUpperCase));
  }

  public void method() {
    Functions.length();
    Functions.countLines();
  }
}
