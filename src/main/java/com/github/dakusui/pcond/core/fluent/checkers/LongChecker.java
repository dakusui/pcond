package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Checker;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.longChecker;

public interface LongChecker<OIN> extends ComparableNumberChecker<LongChecker<OIN>, OIN, Long>, Matcher.ForLong<OIN> {

  class Impl<OIN> extends Checker.Base<LongChecker<OIN>, OIN, Long> implements LongChecker<OIN> {

    public Impl(String transformerName, Function<? super OIN, ? extends Long> function, OIN originalInputValue) {
      super(originalInputValue, transformerName, function);
    }

    @Override
    public LongChecker<OIN> create(OIN originalInputValue, String transformerName, Function<? super OIN, ? extends Long> function) {
      return longChecker(transformerName, function, originalInputValue);
    }
  }
}
