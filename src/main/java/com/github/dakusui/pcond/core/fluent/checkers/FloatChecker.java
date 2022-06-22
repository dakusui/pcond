package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Checker;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.floatChecker;

public interface FloatChecker<OIN> extends ComparableNumberChecker<FloatChecker<OIN>, OIN, Float>, Matcher.ForFloat<OIN> {
  class Impl<OIN> extends Checker.Base<FloatChecker<OIN>, OIN, Float> implements FloatChecker<OIN> {

    public Impl(String transformerName, Function<? super OIN, ? extends Float> function, Predicate<? super Float> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public FloatChecker<OIN> create(String transformerName, Function<? super OIN, ? extends Float> function, Predicate<? super Float> predicate, OIN originalInputValue) {
      return floatChecker(transformerName, function, predicate, originalInputValue);
    }
  }
}
