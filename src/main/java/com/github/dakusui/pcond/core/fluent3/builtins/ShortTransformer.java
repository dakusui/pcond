package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.function.Supplier;

public interface ShortTransformer<
    R extends Matcher<R, R, OIN, OIN>, OIN
    > extends
    ComparableNumberTransformer<
        ShortTransformer<R, OIN>,
            R,
        ShortChecker<R, OIN>,
            OIN,
            Short> {
  static <R extends Matcher<R, R, Short, Short>> ShortTransformer<R, Short> create(Supplier<Short> value) {
    return new Impl<>(value, null);
  }
  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN
      > extends
      Base<
          ShortTransformer<R, OIN>,
          R,
          OIN,
          Short> implements
      ShortTransformer<R, OIN> {
    public Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public ShortChecker<R, OIN> createCorrespondingChecker(R root) {
      return new ShortChecker.Impl<>(this::rootValue, this.root());
    }
  }
}
