package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.core.fluent.verifiers.StringVerifier;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.pcond.internals.InternalUtils;

public interface IStringTransformer<OIN>
    extends ITransformer<IStringTransformer<OIN>, OIN, String>,
    Matcher.ForString<OIN> {
  default IStringTransformer<OIN> substring(int begin) {
    return this.transformToString(Printables.function(() -> "substring[" + begin + "]", s -> s.substring(begin)));
  }

  default IStringTransformer<OIN> toUpperCase() {
    return this.transformToString(Printables.function("toUpperCase", String::toUpperCase));
  }

  @Override
  default StringVerifier<OIN> then() {
    return new StringVerifier<>(this.transformerName(), this.function(), InternalUtils.dummyPredicate());
  }
}
