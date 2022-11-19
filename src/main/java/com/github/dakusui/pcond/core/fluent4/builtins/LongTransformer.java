package com.github.dakusui.pcond.core.fluent4.builtins;

import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.function.Supplier;

public interface LongTransformer<
    R extends Matcher<R, R, OIN, OIN>, OIN
    > extends
    ComparableNumberTransformer<
                LongTransformer<R, OIN>,
                R,
        LongChecker<R, OIN>,
                OIN,
                Long> {
  static <R extends Matcher<R, R, Long, Long>> LongTransformer<R, Long> create(Supplier<Long> value) {
    return new Impl<>(value, null);
  }
  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN
      > extends
      Base<
          LongTransformer<R, OIN>,
          R,
          OIN,
          Long> implements
      LongTransformer<R, OIN> {
    public Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public LongChecker<R, OIN> createCorrespondingChecker(R root) {
      return new LongChecker.Impl<>(this::rootValue, this.root());
    }
  }
}
