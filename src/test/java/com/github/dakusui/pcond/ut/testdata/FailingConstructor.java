package com.github.dakusui.pcond.ut.testdata;

public class FailingConstructor {
  public FailingConstructor() {
    throw new IntentionalException("Hello!");
  }
}
