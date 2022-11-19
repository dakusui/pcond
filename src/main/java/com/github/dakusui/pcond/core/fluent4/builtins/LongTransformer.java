package com.github.dakusui.pcond.core.fluent4.builtins;

import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface LongTransformer<
    T
    > extends
    ComparableNumberTransformer<
        LongTransformer<T>,
        LongChecker<T>,
        T,
        Long> {
  static <R extends Matcher<R, R, Long, Long>> LongTransformer<Long> create(Supplier<Long> value) {
    return new Impl<>(value, trivialIdentityFunction());
  }

  class Impl<
      T
      > extends
      Base<
          LongTransformer<T>,
          LongChecker<T>,
          T,
          Long> implements
      LongTransformer<T> {
    public Impl(Supplier<T> baseValue, Function<T, Long> transformFunction) {
      super(baseValue, transformFunction);
    }

    @Override
    public LongChecker<T> toChecker(Function<T, Long> transformFunction) {
      return new LongChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected LongTransformer<Long> rebase() {
      return new LongTransformer.Impl<>(this::value, trivialIdentityFunction());
    }
  }

}
