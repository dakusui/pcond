package com.github.dakusui.pcond.provider;

import java.util.function.Function;

public interface ExceptionFactory<E extends Throwable> extends Function<Explanation, E> {
  static <E extends Throwable> ExceptionFactory<E> from(Function<String, E> exceptionComposingFunction) {
    return explanation -> exceptionComposingFunction.apply(explanation.toString());
  }
}
