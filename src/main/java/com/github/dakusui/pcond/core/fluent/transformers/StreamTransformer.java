package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.StreamVerifier;
import com.github.dakusui.pcond.forms.Functions;

import java.util.function.Function;
import java.util.stream.Stream;

public class StreamTransformer<OIN, E> extends Transformer<StreamTransformer<OIN, E>, OIN, Stream<E>> {
  /**
   * @param parent
   * @param function
   */
  public <IN> StreamTransformer(Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Stream<E>> function) {
    super(parent, function);
  }

  @Override
  public StreamVerifier<OIN, E> then() {
    return then(Functions.identity());
  }

  @Override
  public StreamVerifier<OIN, E> then(Function<Stream<E>, Stream<E>> converter) {
    return thenAsStream(converter);
  }
}
