package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.identifieable.Identifiable;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.booleanVerifier;

public interface BooleanVerifier<OIN> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, Boolean>,
    Verifier<BooleanVerifier<OIN>, OIN, Boolean>,
    Matcher.ForBoolean<OIN> {
  @Override
  BooleanVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends Boolean> function, Predicate<? super Boolean> predicate, OIN originalInputValue);

  class Impl<OIN> extends Verifier.Base<BooleanVerifier<OIN>, OIN, Boolean> implements BooleanVerifier<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends Boolean> function, Predicate<? super Boolean> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public BooleanVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends Boolean> function, Predicate<? super Boolean> predicate, OIN originalInputValue) {
      return booleanVerifier(transformerName, function, predicate, originalInputValue);
    }
  }
}
