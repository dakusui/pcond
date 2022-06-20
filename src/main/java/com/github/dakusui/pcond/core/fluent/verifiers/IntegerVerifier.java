package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.integerVerifier;

public interface IntegerVerifier<OIN> extends ComparableNumberVerifier<IntegerVerifier<OIN>, OIN, Integer>, Matcher.ForInteger<OIN> {
  @Override
  IntegerVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue);

  class Impl<OIN> extends Verifier.Base<IntegerVerifier<OIN>, OIN, Integer> implements IntegerVerifier<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public IntegerVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue) {
      return integerVerifier(transformerName, function, predicate, originalInputValue);
    }
  }
}
