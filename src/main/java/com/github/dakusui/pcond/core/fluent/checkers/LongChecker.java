package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Checker;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.longChecker;

public interface LongChecker<OIN> extends ComparableNumberChecker<LongChecker<OIN>, OIN, Long>, Matcher.ForLong<OIN> {

  class Impl<OIN> extends Checker.Base<LongChecker<OIN>, OIN, Long> implements LongChecker<OIN> {

    public Impl(String transformerName, Function<? super OIN, ? extends Long> function, Predicate<? super Long> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public LongChecker<OIN> create(String transformerName, Function<? super OIN, ? extends Long> function, Predicate<? super Long> predicate, OIN originalInputValue) {
      return longChecker(transformerName, function, predicate, originalInputValue);
    }
  }
}
