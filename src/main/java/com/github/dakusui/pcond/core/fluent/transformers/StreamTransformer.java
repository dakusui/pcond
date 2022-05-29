package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.StreamVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.streamVerifier;

public interface StreamTransformer<OIN, E> extends Transformer<StreamTransformer<OIN, E>, OIN, Stream<E>>, Matcher.ForStream<OIN, E> {
  @Override
  StreamVerifier<OIN, E> then();

  class Impl<OIN, E> extends BaseTransformer<StreamTransformer<OIN, E>, OIN, Stream<E>>
      implements StreamTransformer<OIN, E> {

    /**
     *
     */
    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Stream<E>> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public StreamVerifier<OIN, E> then() {
      return streamVerifier(this.transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
    }
  }

}
