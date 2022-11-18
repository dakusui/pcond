package com.github.dakusui.pcond.core.fluent4.sandbox;

import com.github.dakusui.pcond.core.fluent4.Transformer;

import java.util.function.Function;
import java.util.function.Predicate;

public interface BooleanTransformer<T> extends
    Transformer<
        BooleanTransformer<T>,
        BooleanChecker<T>,
        T,
        Boolean> {

  class Impl<T> implements BooleanTransformer<T> {
    private final Function<T, Boolean> transformFunction;

    public Impl(Function<T, Boolean> func) {
      this.transformFunction = func;
    }


    @Override
    public BooleanTransformer<T> check(Predicate<Boolean> predicate) {
      return null;
    }

    @Override
    public <TY extends Transformer<TY, ?, Boolean, Boolean>> BooleanTransformer<T> addTransformPhrase(Function<TY, Predicate<Boolean>> nestedClause) {
      return null;
    }


    @Override
    public Function<T, Boolean> transformFunction() {
      return this.transformFunction;
    }

    @Override
    public BooleanChecker<T> createCorrespondingChecker(Function<T, Boolean> transformFunction) {
      return new BooleanChecker.Impl<>(transformFunction);
    }
  }

}
