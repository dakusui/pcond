package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectChecker;
import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.stream.Stream;

public interface StreamChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN,
    E> extends
    AbstractObjectChecker<
        StreamChecker<R, OIN, E>,
        R,
        OIN,
        Stream<E>> {

  class Impl<R extends Matcher<R, R, OIN, OIN>,
      OIN,
      E> extends
      Matcher.Base<
          StreamChecker<R, OIN, E>,
          R,
          OIN,
          Stream<E>
          >

      implements StreamChecker<R, OIN, E> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
