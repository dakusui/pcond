package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.IStreamVerifier;
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
  public <IN> StreamTransformer(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends Stream<E>> function, OIN originalInputValue) {
    super(transformerName, parent, function, originalInputValue);
  }

  @Override
  public IStreamVerifier<OIN, E> then() {
    return new StreamVerifier<>(this.transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
  }
}
