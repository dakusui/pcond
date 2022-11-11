package com.github.dakusui.pcond.core.fluent2;

import java.util.function.Predicate;
import java.util.function.Supplier;

public interface IntegerTransformer<OIN> extends Transformer<IntegerTransformer<OIN>, IntegerChecker<OIN>, OIN, Integer> {
  class Impl<OIN> extends Base<IntegerTransformer<OIN>, OIN, Integer> implements IntegerTransformer<OIN> {
    protected Impl(OIN originalInputValue, Supplier<Predicate<OIN>> root) {
      super(originalInputValue, root);
    }

    @Override
    public IntegerChecker<OIN> then() {
      IntegerChecker<OIN> ret = new IntegerChecker.Impl<>(this.originalInputValue(), rootPredicateSupplier());
      this.appendChild(tx -> ret.connectChildPredicates());
      return ret;
    }
  }
}
