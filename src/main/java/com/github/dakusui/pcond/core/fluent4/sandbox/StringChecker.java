package com.github.dakusui.pcond.core.fluent4.sandbox;


import com.github.dakusui.pcond.core.fluent4.Checker;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.makeTrivial;

public interface StringChecker<T> extends Checker<StringChecker<T>, T, String> {

  default StringChecker<T> contains(String value) {
    return checkWithPredicate(Predicates.containsString(value));
  }

  @SuppressWarnings("unchecked")
  default StringChecker<T> check(Function<StringChecker<String>, Predicate<String>> phrase) {
    return this.addCheckPhrase(v -> phrase.apply((StringChecker<String>) v));
  }

  class Impl<T> extends
      Base<
          StringChecker<T>,
          T,
          String>
      implements StringChecker<T> {

    public Impl(Supplier<T> value, Function<T, String> transformFunction) {
      super(value, transformFunction);
    }

    @Override
    public StringChecker<String> rebase() {
      return new StringChecker.Impl<>(this::value, makeTrivial(Functions.identity()));
    }
  }
}
