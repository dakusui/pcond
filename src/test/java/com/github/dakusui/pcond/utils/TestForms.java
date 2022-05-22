package com.github.dakusui.pcond.utils;

import java.util.function.Function;

import static com.github.dakusui.pcond.forms.Printables.function;

public enum TestForms {
  ;
  public static Function<? super Object, Integer> objectHashCode() {
    return function("objectHashCode", Object::hashCode);
  }
}
