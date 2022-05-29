package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.identifieable.Identifiable;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.objectVerifier;

public interface ObjectVerifier<OIN, OUT> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, OUT>,
    Verifier<ObjectVerifier<OIN, OUT>, OIN, OUT>,
    Matcher.ForObject<OIN, OUT> {
  @Override
  ObjectVerifier<OIN, OUT> create(String transformerName, Function<? super OIN, ? extends OUT> function, Predicate<? super OUT> predicate, OIN originalInputValue);

  class Impl<OIN, OUT>
      extends Verifier.Base<ObjectVerifier<OIN, OUT>, OIN, OUT>
      implements ObjectVerifier<OIN, OUT> {
    public Impl(String transformerName, Function<? super OIN, ? extends OUT> function, Predicate<? super OUT> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public ObjectVerifier<OIN, OUT> create(String transformerName, Function<? super OIN, ? extends OUT> function, Predicate<? super OUT> predicate, OIN originalInputValue) {
      return objectVerifier(transformerName, function, predicate, originalInputValue);
    }
  }
}
