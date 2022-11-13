package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.stream.Stream;

public interface StreamTransformer<
    OIN,
    R extends Matcher<R, R, OIN, OIN>,
    E> extends
    AbstractObjectTransformer<
        StreamTransformer<OIN, R, E>,
        R,
        StreamChecker<R, OIN, E>,
        OIN,
        Stream<E>
        > {
  class Impl<OIN,
      R extends Matcher<R, R, OIN, OIN>,
      E> extends
      Matcher.Base<StreamTransformer<OIN, R, E>,
          R,
          OIN,
          Stream<E>> implements
      StreamTransformer<OIN, R, E> {

    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public StreamChecker<R, OIN, E> createCorrespondingChecker(R root) {
      return new StreamChecker.Impl<>(this.rootValue(), root);
    }
  }
}
