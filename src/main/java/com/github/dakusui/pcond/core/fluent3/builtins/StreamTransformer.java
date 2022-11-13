package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.stream.Stream;

public interface StreamTransformer<
    R extends Matcher<R, R, OIN, OIN>, OIN,
    E> extends
    AbstractObjectTransformer<
        StreamTransformer<R, OIN, E>,
        R,
        StreamChecker<R, OIN, E>,
        OIN,
        Stream<E>
        > {
  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN,
      E> extends
      Matcher.Base<StreamTransformer<R, OIN, E>,
          R,
          OIN,
          Stream<E>> implements
      StreamTransformer<R, OIN, E> {

    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public StreamChecker<R, OIN, E> createCorrespondingChecker(R root) {
      return new StreamChecker.Impl<>(this.rootValue(), root);
    }
  }
}
