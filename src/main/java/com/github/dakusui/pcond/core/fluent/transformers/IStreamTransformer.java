package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.verifiers.IStreamVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;

import java.util.stream.Stream;

public interface IStreamTransformer<OIN, E> extends ITransformer<IStreamTransformer<OIN, E>, OIN, Stream<E>>, Matcher.ForStream<OIN, E> {
  @Override
  IStreamVerifier<OIN, E> then();
}
