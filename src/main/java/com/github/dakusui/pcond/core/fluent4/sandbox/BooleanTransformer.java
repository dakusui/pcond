package com.github.dakusui.pcond.core.fluent4.sandbox;

import com.github.dakusui.pcond.core.fluent4.Transformer;

import java.util.function.Function;

public interface BooleanTransformer<T> extends
    Transformer<
        BooleanTransformer<T>,
        BooleanChecker<T>,
        T,
        Boolean> {

  class Impl<T> extends
  Transformer.Base<
      BooleanTransformer<T>,
      BooleanChecker<T>,
      T,
      Boolean
      >
      implements BooleanTransformer<T> {

    public Impl(Function<T, Boolean> transformFunction) {
      super(transformFunction);
    }


    @Override
    public BooleanChecker<T> toChecker(Function<T, Boolean> transformFunction) {
      return new BooleanChecker.Impl<>(transformFunction);
    }

    @Override
    public Transformer<?, ?, Boolean, Boolean> rebase() {
      return new Impl<>(Function.identity());
    }
  }

}
