package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.longVerifier;

public interface LongVerifier<OIN> extends NumberVerifier<LongVerifier<OIN>, OIN, Long>, Matcher.ForLong<OIN> {

  class Impl<OIN> extends Verifier.Base<LongVerifier<OIN>, OIN, Long> implements LongVerifier<OIN> {

    public Impl(String transformerName, Function<? super OIN, ? extends Long> function, Predicate<? super Long> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public LongVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends Long> function, Predicate<? super Long> predicate, OIN originalInputValue) {
      return longVerifier(transformerName, function, predicate, originalInputValue);
    }
  }
}
