package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.doubleVerifier;

public interface DoubleVerifier<OIN> extends NumberVerifier<DoubleVerifier<OIN>, OIN, Double>, Matcher.ForDouble<OIN> {
  class Impl<OIN> extends Verifier.Base<DoubleVerifier<OIN>, OIN, Double> implements DoubleVerifier<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends Double> function, Predicate<? super Double> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public DoubleVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends Double> function, Predicate<? super Double> predicate, OIN originalInputValue) {
      return doubleVerifier(transformerName, function, predicate, originalInputValue);
    }
  }
}
