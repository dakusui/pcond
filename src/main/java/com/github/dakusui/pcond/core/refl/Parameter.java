package com.github.dakusui.pcond.core.refl;

public interface Parameter {
  Parameter INSTANCE = create();

  static Parameter create() {
    return new Parameter() {
      @Override
      public String toString() {
        return "";
      }
    };
  }
}
