package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Checker;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.identifieable.Identifiable;

import java.util.function.Function;
import java.util.function.Predicate;

public interface ObjectChecker<OIN, OUT> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, OUT>,
    Checker<ObjectChecker<OIN, OUT>, OIN, OUT>,
    Matcher.ForObject<OIN, OUT> {
  class Impl<OIN, OUT> extends Checker.Base<ObjectChecker<OIN, OUT>, OIN, OUT>
      implements ObjectChecker<OIN, OUT> {
    public Impl(String transformerName, Function<? super OIN, ? extends OUT> function, OIN originalInputValue) {
      super(originalInputValue, transformerName, function);
    }

    @Override
    public ObjectChecker<OIN, OUT> create(OIN originalInputValue, String transformerName, Function<? super OIN, ? extends OUT> function) {
      return Checker.Factory.objectChecker(transformerName, function, originalInputValue);
    }
  }
}
