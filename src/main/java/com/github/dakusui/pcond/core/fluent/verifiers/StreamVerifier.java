package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class StreamVerifier<OIN, E>
    extends Verifier<IStreamVerifier<OIN, E>, OIN, Stream<E>>
    implements IStreamVerifier<OIN, E> {
  public StreamVerifier(String transformerName, Function<? super OIN, ? extends Stream<E>> function, Predicate<? super Stream<E>> predicate, OIN originalInputValue) {
    super(transformerName, function, predicate, originalInputValue);
  }

  @Override
  public IStreamVerifier<OIN, E> create(String transformerName, Function<? super OIN, ? extends Stream<E>> function, Predicate<? super Stream<E>> predicate, OIN originalInputValue) {
    return new StreamVerifier<>(transformerName, function, predicate, originalInputValue);
  }

  void method() {
    Predicates.noneMatch(Predicate.isEqual(""));
    Predicates.allMatch(Predicate.isEqual(""));
    Predicates.anyMatch(Predicate.isEqual(""));
  }
}
