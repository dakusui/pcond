package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.shortVerifier;

public interface ShortVerifier<OIN> extends NumberVerifier<ShortVerifier<OIN>, OIN, Short>, Matcher.ForShort<OIN> {
  class Impl<OIN> extends Verifier.Base<ShortVerifier<OIN>, OIN, Short> implements ShortVerifier<OIN> {

    public Impl(String transformerName, Function<? super OIN, ? extends Short> function, Predicate<? super Short> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public ShortVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends Short> function, Predicate<? super Short> predicate, OIN originalInputValue) {
      return shortVerifier(transformerName, function, predicate, originalInputValue);
    }
  }
}
