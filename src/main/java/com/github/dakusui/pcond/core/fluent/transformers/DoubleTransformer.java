package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.checkers.DoubleChecker;
import com.github.dakusui.pcond.core.fluent.Matcher;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.doubleChecker;

public interface DoubleTransformer<OIN> extends ComparableNumberTransformer<DoubleTransformer<OIN>, DoubleChecker<OIN>, OIN, Double>, Matcher.ForDouble<OIN> {
  @Override
  DoubleChecker<OIN> then();

  class Impl<OIN> extends Base<DoubleTransformer<OIN>, OIN, Double> implements DoubleTransformer<OIN> {
    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Double> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public DoubleChecker<OIN> then() {
      return doubleChecker(this.transformerName(), this.function(), this.originalInputValue());
    }
  }
}
