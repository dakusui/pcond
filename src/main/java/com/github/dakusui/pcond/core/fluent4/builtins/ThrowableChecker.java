package com.github.dakusui.pcond.core.fluent4.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectChecker;
import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.function.Supplier;

public interface ThrowableChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN,
    T extends Throwable> extends
    AbstractObjectChecker<
        ThrowableChecker<R, OIN, T>,
        R,
        OIN,
        T> {
  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN,
      T extends Throwable
      > extends
      Base<
          ThrowableChecker<R, OIN, T>,
          R,
          OIN,
          T
          > implements
      ThrowableChecker<R, OIN, T> {
    protected Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }
  }
}
