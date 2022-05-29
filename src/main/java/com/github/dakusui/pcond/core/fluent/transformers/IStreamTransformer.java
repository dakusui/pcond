package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.IStreamVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.fluent.IVerifier.Factory.streamVerifier;

public interface IStreamTransformer<OIN, E> extends ITransformer<IStreamTransformer<OIN, E>, OIN, Stream<E>>, Matcher.ForStream<OIN, E> {
  @Override
  IStreamVerifier<OIN, E> then();

  class StreamTransformer<OIN, E> extends Transformer<IStreamTransformer<OIN, E>, OIN, Stream<E>>
      implements IStreamTransformer<OIN, E> {

    /**
     *
     */
    public <IN> StreamTransformer(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends Stream<E>> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public IStreamVerifier<OIN, E> then() {
      return streamVerifier(this.transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
    }
  }

}
