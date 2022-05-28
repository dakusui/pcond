package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.Transformer;

import java.util.function.Function;

public class StringTransformer<OIN>
    extends Transformer<IStringTransformer<OIN>, OIN, String>
    implements IStringTransformer<OIN> {
  public <IN> StringTransformer(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends String> function) {
    super(transformerName, parent, function);
  }
}
