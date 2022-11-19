package com.github.dakusui.pcond.core.fluent4.sandbox;

import com.github.dakusui.pcond.core.fluent4.Transformer;
import com.github.dakusui.pcond.forms.Functions;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.makeTrivial;
import static java.util.Objects.requireNonNull;

public interface BooleanTransformer<T> extends
    Transformer<
        BooleanTransformer<T>,
        BooleanChecker<T>,
        T,
        Boolean> {

  default StringTransformer<T> stringify() {
    return toString(Functions.stringify());
  }

  @SuppressWarnings("unchecked")
  default BooleanTransformer<T> transformAndCheck(Function<BooleanTransformer<Boolean>, Predicate<Boolean>> clause) {
    return this.addTransformAndCheckClause(tx -> clause.apply((BooleanTransformer<Boolean>) tx));
  }

  class Impl<T> extends
      Transformer.Base<
          BooleanTransformer<T>,
          BooleanChecker<T>,
          T,
          Boolean
          >
      implements BooleanTransformer<T> {

    public Impl(Supplier<T> baseValue, Function<T, Boolean> transformFunction) {
      super(baseValue, transformFunction);
    }


    @Override
    public BooleanChecker<T> toChecker(Function<T, Boolean> transformFunction) {
      return new BooleanChecker.Impl<>(this::baseValue, requireNonNull(transformFunction));
    }

    @Override
    public Transformer<?, ?, Boolean, Boolean> rebase() {
      return new Impl<>(this::value, makeTrivial(Functions.identity()));
    }
  }
}
