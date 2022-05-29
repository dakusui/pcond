package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.IVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.IStringVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.forms.Printables;

import java.util.function.Function;

import static com.github.dakusui.pcond.internals.InternalUtils.dummyPredicate;

public interface IStringTransformer<OIN> extends
    ITransformer<IStringTransformer<OIN>, OIN, String>,
    Matcher.ForString<OIN> {
  default IStringTransformer<OIN> substring(int begin) {
    return this.transformToString(Printables.function(() -> "substring[" + begin + "]", s -> s.substring(begin)));
  }

  default IStringTransformer<OIN> toUpperCase() {
    return this.transformToString(Printables.function("toUpperCase", String::toUpperCase));
  }

  @SuppressWarnings("unchecked")
  @Override
  default IStringVerifier<OIN> then() {
    return IVerifier.Factory.stringVerifier(
        this.transformerName(),
        (Function<? super OIN, String>) this.function(),
        dummyPredicate(),
        this.originalInputValue());
  }
}
