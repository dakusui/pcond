package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.IVerifier;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.fluent.IVerifier.Factory.streamVerifier;

public interface IStreamVerifier<OIN, E> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, Stream<E>>,
    IVerifier<IStreamVerifier<OIN, E>, OIN, Stream<E>>,
    Matcher.ForStream<OIN, E> {
  @Override
  IStreamVerifier<OIN, E> create(String transformerName, Function<? super OIN, ? extends Stream<E>> function, Predicate<? super Stream<E>> predicate, OIN originalInputValue);

  default IStreamVerifier<OIN, E> noneMatch(Predicate<E> p) {
    return this.predicate(Predicates.noneMatch(p));
  }

  default IStreamVerifier<OIN, E> anyMatch(Predicate<E> p) {
    return this.predicate(Predicates.anyMatch(p));
  }

  default IStreamVerifier<OIN, E> allMatch(Predicate<E> p) {
    return this.predicate(Predicates.allMatch(p));
  }

  class Impl<OIN, E>
      extends Verifier<IStreamVerifier<OIN, E>, OIN, Stream<E>>
      implements IStreamVerifier<OIN, E> {
    public Impl(String transformerName, Function<? super OIN, ? extends Stream<E>> function, Predicate<? super Stream<E>> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public IStreamVerifier<OIN, E> create(String transformerName, Function<? super OIN, ? extends Stream<E>> function, Predicate<? super Stream<E>> predicate, OIN originalInputValue) {
      return streamVerifier(transformerName, function, predicate, originalInputValue);
    }
  }
}
