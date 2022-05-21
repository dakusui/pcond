package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.core.fluent.verifiers.StringVerifier;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Printables;

import java.util.function.Function;

public class StringTransformer<OIN> extends Transformer<StringTransformer<OIN>, OIN, String> implements Matcher.ForString<OIN> {


  /**
   * @param transformerName
   * @param parent
   * @param function
   */
  public <IN> StringTransformer(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends String> function) {
    super(transformerName, parent, function);
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

  @Override
  public StringVerifier<OIN> then() {
    return then(Functions.stringify());
  }

  @Override
  public StringVerifier<OIN> then(Function<String, String> converter) {
    return thenAsString(converter);
  }
}
