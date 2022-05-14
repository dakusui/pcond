package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class StreamVerifier<OIN, E> extends Verifier<StreamVerifier<OIN, E>, OIN, Stream<E>> {
  public StreamVerifier(Function<? super OIN, ? extends Stream<E>> function) {
    super(function);
  }

  public StreamVerifier<OIN, E> noneMatch(Predicate<E> p) {
    return this.predicate(Predicates.noneMatch(p));
  }

  public StreamVerifier<OIN, E> anyMatch(Predicate<E> p) {
    return this.predicate(Predicates.anyMatch(p));
  }

  public StreamVerifier<OIN, E> allMatch(Predicate<E> p) {
    return this.predicate(Predicates.allMatch(p));
  }

  void method() {
    Predicates.noneMatch(Predicate.isEqual(""));
    Predicates.allMatch(Predicate.isEqual(""));
    Predicates.anyMatch(Predicate.isEqual(""));
  }
}
