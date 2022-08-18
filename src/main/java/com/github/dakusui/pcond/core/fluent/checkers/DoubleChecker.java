package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Checker;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.doubleChecker;

public interface DoubleChecker<OIN> extends ComparableNumberChecker<DoubleChecker<OIN>, OIN, Double>, Matcher.ForDouble<OIN> {
  class Impl<OIN> extends Checker.Base<DoubleChecker<OIN>, OIN, Double> implements DoubleChecker<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends Double> function, Predicate<? super Double> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public DoubleChecker<OIN> create(String transformerName, Function<? super OIN, ? extends Double> function, Predicate<? super Double> predicate, OIN originalInputValue) {
      return doubleChecker(transformerName, function, predicate, originalInputValue);
    }
  }
}
