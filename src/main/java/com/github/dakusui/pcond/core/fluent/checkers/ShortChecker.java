package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Checker;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.shortChecker;

public interface ShortChecker<OIN> extends ComparableNumberChecker<ShortChecker<OIN>, OIN, Short>, Matcher.ForShort<OIN> {
  class Impl<OIN> extends Checker.Base<ShortChecker<OIN>, OIN, Short> implements ShortChecker<OIN> {

    public Impl(String transformerName, Function<? super OIN, ? extends Short> function, OIN originalInputValue) {
      super(originalInputValue, transformerName, function);
    }

    @Override
    public ShortChecker<OIN> create(OIN originalInputValue, String transformerName, Function<? super OIN, ? extends Short> function) {
      return shortChecker(transformerName, function, originalInputValue);
    }
  }
}
