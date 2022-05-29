package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.fluent.verifiers.StringVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.forms.Printables;

import java.util.function.Function;

import static com.github.dakusui.pcond.internals.InternalUtils.dummyPredicate;

public interface StringTransformer<OIN> extends
    Transformer<StringTransformer<OIN>, OIN, String>,
    Matcher.ForString<OIN> {
  default StringTransformer<OIN> substring(int begin) {
    return this.transformToString(Printables.function(() -> "substring[" + begin + "]", s -> s.substring(begin)));
  }

  default StringTransformer<OIN> toUpperCase() {
    return this.transformToString(Printables.function("toUpperCase", String::toUpperCase));
  }

  @SuppressWarnings("unchecked")
  @Override
  default StringVerifier<OIN> then() {
    return Verifier.Factory.stringVerifier(
        this.transformerName(),
        (Function<? super OIN, String>) this.function(),
        dummyPredicate(),
        this.originalInputValue());
  }

  class Impl<OIN>
      extends BaseTransformer<StringTransformer<OIN>, OIN, String>
      implements StringTransformer<OIN> {
    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends String> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }
  }
}
