package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Checker;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.doubleChecker;

public interface DoubleChecker<OIN> extends ComparableNumberChecker<DoubleChecker<OIN>, OIN, Double>, Matcher.ForDouble<OIN> {
  class Impl<OIN> extends Checker.Base<DoubleChecker<OIN>, OIN, Double> implements DoubleChecker<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends Double> function, OIN originalInputValue) {
      super(originalInputValue, transformerName, function);
    }

    @Override
    public DoubleChecker<OIN> create(OIN originalInputValue, String transformerName, Function<? super OIN, ? extends Double> function) {
      return doubleChecker(transformerName, function, originalInputValue);
    }
  }
}
