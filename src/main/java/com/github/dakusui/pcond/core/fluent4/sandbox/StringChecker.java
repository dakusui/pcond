package com.github.dakusui.pcond.core.fluent4.sandbox;


import com.github.dakusui.pcond.core.fluent4.Checker;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;

public interface StringChecker<T> extends Checker<StringChecker<T>, T, String> {

  default StringChecker<T> contains(String value) {
    return check(Predicates.containsString(value));
  }

  class Impl<T> extends
      Base<
          StringChecker<T>,
          T,
          String>
      implements StringChecker<T> {

    public Impl(Function<T, String> transformFunction) {
      super(transformFunction);
    }

    @Override
    public StringChecker<String> rebase() {
      return new StringChecker.Impl<>(Function.identity());
    }
  }
}
