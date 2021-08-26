package com.github.dakusui.pcond.utils;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
public @interface TestMethodExpectation {
  enum Result {
    PASS,
    FAIL,
    ERROR,
    IGNORED
  }

  enum TestMethodAction {
    STARTED,
    FINISHED,
    ASSUMPTION_FAILURE,
    IGNORED
  }

  Class<? extends Comparable<?>> value();
}
