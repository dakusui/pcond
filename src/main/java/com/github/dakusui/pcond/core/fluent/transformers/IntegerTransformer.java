package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.checkers.IntegerChecker;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.integerChecker;

public interface IntegerTransformer<OIN> extends Transformer<IntegerTransformer<OIN>, OIN, Integer>, Matcher.ForInteger<OIN> {
  @Override
  IntegerChecker<OIN> then();

  class Impl<OIN> extends Base<IntegerTransformer<OIN>, OIN, Integer> implements IntegerTransformer<OIN> {
    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Integer> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public IntegerChecker<OIN> then() {
      return integerChecker(this.transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
    }
  }
}
