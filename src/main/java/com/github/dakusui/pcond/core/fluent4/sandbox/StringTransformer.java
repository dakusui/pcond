package com.github.dakusui.pcond.core.fluent4.sandbox;


import com.github.dakusui.pcond.core.fluent4.Transformer;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Printables;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.makeTrivial;
import static java.util.Objects.requireNonNull;

public interface StringTransformer<T> extends
    Transformer<StringTransformer<T>, StringChecker<T>,
        T,
        String> {

  default BooleanTransformer<T> parseBoolean() {
    return toBoolean(Printables.function("parseBoolean", Boolean::parseBoolean));
  }

  default StringTransformer<T> toLowerCase() {
    return toString(Printables.function("toLowerCase", String::toLowerCase));
  }

  @SuppressWarnings("unchecked")
  default StringTransformer<T> transformAndCheck(Function<StringTransformer<String>, Predicate<String>> clause) {
    requireNonNull(clause);
    return this.addTransformAndCheckClause(tx -> clause.apply((StringTransformer<String>) tx));
  }

  class Impl<T> extends
      Base<
          StringTransformer<T>,
          StringChecker<T>,
          T,
          String
          > implements
      StringTransformer<T> {

    public Impl(Function<T, String> transformFunction) {
      super(transformFunction);
    }

    @Override
    public StringChecker<T> toChecker(Function<T, String> transformFunction) {
      return new StringChecker.Impl<>(requireNonNull(transformFunction));
    }

    @Override
    public StringTransformer<String> rebase() {
      return new StringTransformer.Impl<>(makeTrivial(Functions.identity()));
    }
  }
}
