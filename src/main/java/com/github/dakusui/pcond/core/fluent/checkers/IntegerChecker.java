package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Checker;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.integerChecker;

public interface IntegerChecker<OIN> extends ComparableNumberChecker<IntegerChecker<OIN>, OIN, Integer>, Matcher.ForInteger<OIN> {
  class Impl<OIN> extends Checker.Base<IntegerChecker<OIN>, OIN, Integer> implements IntegerChecker<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends Integer> function, OIN originalInputValue) {
      super(originalInputValue, transformerName, function);
    }

    @Override
    public IntegerChecker<OIN> create(OIN originalInputValue, String transformerName, Function<? super OIN, ? extends Integer> function) {
      return integerChecker(transformerName, function, originalInputValue);
    }
  }
}
