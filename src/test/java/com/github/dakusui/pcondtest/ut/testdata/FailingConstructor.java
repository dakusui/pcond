package com.github.dakusui.pcondtest.ut.testdata;

public class FailingConstructor {
  public FailingConstructor() {
    throw new IntentionalException("Hello!");
  }
}
