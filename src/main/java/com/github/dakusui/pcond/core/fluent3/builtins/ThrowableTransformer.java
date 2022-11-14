package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface ThrowableTransformer<
    R extends Matcher<R, R, OIN, OIN>,
    OIN,
    T extends Throwable> extends
    AbstractObjectTransformer<
        ThrowableTransformer<R, OIN, T>,
        R,
        ThrowableChecker<R, OIN, T>,
        OIN,
        T> {
  default <OUT2 extends Throwable> ThrowableTransformer<R, OIN, T> getCause() {
    // TODO
    // return exercise(Printables.function("getCause", Throwable::getCause)).asThrowable();
    return null;
  }

  default StringTransformer<R, OIN> getMessage() {
    //return appendPredicateAsChild(Printables.function("getMessage", Throwable::getMessage));
    // TODO
    return null;
  }

  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN,
      T extends Throwable
      > extends
      Matcher.Base<
          ThrowableTransformer<R, OIN, T>,
          R,
          OIN,
          T> implements
      ThrowableTransformer<
          R,
          OIN,
          T
          > {

    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public ThrowableChecker<R, OIN, T> createCorrespondingChecker(R root) {
      return new ThrowableChecker.Impl<>(this.rootValue(), root);
    }
  }
}
