package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.streamVerifier;

public interface StreamVerifier<OIN, E> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, Stream<E>>,
    Verifier<StreamVerifier<OIN, E>, OIN, Stream<E>>,
    Matcher.ForStream<OIN, E> {
  @Override
  StreamVerifier<OIN, E> create(String transformerName, Function<? super OIN, ? extends Stream<E>> function, Predicate<? super Stream<E>> predicate, OIN originalInputValue);

  default StreamVerifier<OIN, E> noneMatch(Predicate<E> p) {
    return this.predicate(Predicates.noneMatch(p));
  }

  default StreamVerifier<OIN, E> anyMatch(Predicate<E> p) {
    return this.predicate(Predicates.anyMatch(p));
  }

  default StreamVerifier<OIN, E> allMatch(Predicate<E> p) {
    return this.predicate(Predicates.allMatch(p));
  }

  class Impl<OIN, E>
      extends BaseVerifier<StreamVerifier<OIN, E>, OIN, Stream<E>>
      implements StreamVerifier<OIN, E> {
    public Impl(String transformerName, Function<? super OIN, ? extends Stream<E>> function, Predicate<? super Stream<E>> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public StreamVerifier<OIN, E> create(String transformerName, Function<? super OIN, ? extends Stream<E>> function, Predicate<? super Stream<E>> predicate, OIN originalInputValue) {
      return streamVerifier(transformerName, function, predicate, originalInputValue);
    }
  }
}
