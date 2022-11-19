package com.github.dakusui.pcond.core.fluent4.builtins;


import com.github.dakusui.pcond.core.fluent4.AbstractObjectTransformer;
import com.github.dakusui.pcond.forms.Functions;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.makeTrivial;
import static com.github.dakusui.valid8j.Requires.requireNonNull;

public interface BooleanTransformer<T> extends
    AbstractObjectTransformer<
        BooleanTransformer<T>,
        BooleanChecker<T>,
        T,
        Boolean
        > {
  static BooleanTransformer<Boolean> create(Supplier<Boolean> value) {
    return new Impl<>(value, null);
  }

  class Impl<T> extends
      Base<
          BooleanTransformer<T>,
          BooleanChecker<T>,
          T,
          Boolean
          > implements
      BooleanTransformer<T> {
    public Impl(Supplier<T> value, Function<T, Boolean> transfomFunction) {
      super(value, transfomFunction);
    }

    @Override
    protected BooleanChecker<T> toChecker(Function<T, Boolean> transformFunction) {
      return new BooleanChecker.Impl<>(this::baseValue, requireNonNull(transformFunction));
    }

    @Override
    protected BooleanTransformer<Boolean> rebase() {
      return new Impl<>(this::value, makeTrivial(Functions.identity()));
    }
  }
}
