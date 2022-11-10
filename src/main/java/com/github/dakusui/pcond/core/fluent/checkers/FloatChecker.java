package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Checker;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.floatChecker;

public interface FloatChecker<OIN> extends ComparableNumberChecker<FloatChecker<OIN>, OIN, Float>, Matcher.ForFloat<OIN> {
  class Impl<OIN> extends Checker.Base<FloatChecker<OIN>, OIN, Float> implements FloatChecker<OIN> {

    public Impl(String transformerName, Function<? super OIN, ? extends Float> function, OIN originalInputValue) {
      super(originalInputValue, transformerName, function);
    }

    @Override
    public FloatChecker<OIN> create(OIN originalInputValue, String transformerName, Function<? super OIN, ? extends Float> function) {
      return floatChecker(transformerName, function, originalInputValue);
    }
  }
}
