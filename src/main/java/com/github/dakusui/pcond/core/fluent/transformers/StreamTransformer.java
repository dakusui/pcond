package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.core.fluent.verifiers.StreamVerifier;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.stream.Stream;

public class StreamTransformer<OIN, E> extends Transformer<StreamTransformer<OIN, E>, OIN, Stream<E>>
    implements Matcher.ForStream<OIN, E> {

  /**
   *
   */
  public <IN> StreamTransformer(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Stream<E>> function) {
    super(transformerName, parent, function);
  }

  @Override
  public StreamVerifier<OIN, E> then() {
    return new StreamVerifier<>(this.transformerName(), this.function(), InternalUtils.dummyPredicate());
  }
}
