package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.floatVerifier;

public interface FloatVerifier<OIN> extends NumberVerifier<FloatVerifier<OIN>, OIN, Float>, Matcher.ForFloat<OIN> {
  class Impl<OIN> extends Verifier.Base<FloatVerifier<OIN>, OIN, Float> implements FloatVerifier<OIN> {

    public Impl(String transformerName, Function<? super OIN, ? extends Float> function, Predicate<? super Float> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public FloatVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends Float> function, Predicate<? super Float> predicate, OIN originalInputValue) {
      return floatVerifier(transformerName, function, predicate, originalInputValue);
    }
  }
}
