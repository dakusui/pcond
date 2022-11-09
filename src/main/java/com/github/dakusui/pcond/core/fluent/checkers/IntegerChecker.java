package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Checker;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.integerChecker;

public interface IntegerChecker<OIN> extends ComparableNumberChecker<IntegerChecker<OIN>, OIN, Integer>, Matcher.ForInteger<OIN> {
  class Impl<OIN> extends Checker.Base<IntegerChecker<OIN>, OIN, Integer> implements IntegerChecker<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public IntegerChecker<OIN> create(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue) {
      return integerChecker(transformerName, function, predicate, originalInputValue);
    }
  }
}
