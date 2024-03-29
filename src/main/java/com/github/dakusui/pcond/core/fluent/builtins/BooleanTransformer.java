package com.github.dakusui.pcond.core.fluent.builtins;


import com.github.dakusui.pcond.core.fluent.AbstractObjectTransformer;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.trivialIdentityFunction;
import static java.util.Objects.requireNonNull;

public interface BooleanTransformer<T> extends
    AbstractObjectTransformer<
        BooleanTransformer<T>,
        BooleanChecker<T>,
        T,
        Boolean
        > {
  static BooleanTransformer<Boolean> create(Supplier<Boolean> value) {
    return new Impl<>(value, trivialIdentityFunction());
  }

  @SuppressWarnings("unchecked")
  default BooleanTransformer<T> transform(Function<BooleanTransformer<Boolean>, Predicate<Boolean>> clause) {
    return this.addTransformAndCheckClause(tx -> clause.apply((BooleanTransformer<Boolean>) tx));
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
      return new Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
