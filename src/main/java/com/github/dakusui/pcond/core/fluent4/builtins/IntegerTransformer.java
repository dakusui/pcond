package com.github.dakusui.pcond.core.fluent4.builtins;

import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.function.Supplier;

public interface IntegerTransformer<
    R extends Matcher<R, R, OIN, OIN>,
    OIN
    > extends
    ComparableNumberTransformer<
                IntegerTransformer<R, OIN>,
                R,
        IntegerChecker<R, OIN>,
                OIN,
                Integer> {
  static <R extends Matcher<R, R, Integer, Integer>> IntegerTransformer<R, Integer> create(Supplier<Integer> value) {
    return new Impl<>(value, null);
  }
  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN
      > extends
      Base<
          IntegerTransformer<R, OIN>,
          R,
          OIN,
          Integer> implements
      IntegerTransformer<R, OIN> {
    public Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public IntegerChecker<R, OIN> createCorrespondingChecker(R root) {
      return new IntegerChecker.Impl<>(this::rootValue, this.root());
    }
  }
}
