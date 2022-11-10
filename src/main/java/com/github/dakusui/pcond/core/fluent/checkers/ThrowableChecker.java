package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Checker;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.identifieable.Identifiable;

import java.util.function.Function;
import java.util.function.Predicate;

public interface ThrowableChecker<OIN, OUT extends Throwable> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, OUT>,
    Checker<ThrowableChecker<OIN, OUT>, OIN, OUT>,
    Matcher.ForObject<OIN, OUT> {

  class Impl<OIN, OUT extends Throwable> extends Checker.Base<ThrowableChecker<OIN, OUT>, OIN, OUT>
      implements ThrowableChecker<OIN, OUT> {
    public Impl(String transformerName, Function<? super OIN, ? extends OUT> function, OIN originalInputValue) {
      super(originalInputValue, transformerName, function);
    }

    @Override
    public ThrowableChecker<OIN, OUT> create(OIN originalInputValue, String transformerName, Function<? super OIN, ? extends OUT> function) {
      return Checker.Factory.throwableChecker(transformerName, function, originalInputValue);
    }
  }

}
