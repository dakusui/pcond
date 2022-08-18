package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.checkers.StreamChecker;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.streamChecker;

public interface StreamTransformer<OIN, E> extends Transformer<StreamTransformer<OIN, E>, OIN, Stream<E>>, Matcher.ForStream<OIN, E> {
  @Override
  StreamChecker<OIN, E> then();

  class Impl<OIN, E> extends Base<StreamTransformer<OIN, E>, OIN, Stream<E>>
      implements StreamTransformer<OIN, E> {

    /**
     *
     */
    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Stream<E>> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public StreamChecker<OIN, E> then() {
      return streamChecker(this.transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
    }
  }

}
