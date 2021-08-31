package com.github.dakusui.pcond.examples;

import static com.github.dakusui.pcond.Assertions.precondition;
import static com.github.dakusui.pcond.functions.Predicates.*;

public enum NameUtils {
  ;

  public static String firstNameOf(String yourName) {
    assert precondition(yourName, containsString(" ")); // <4>
    return yourName.substring(0, yourName.indexOf(' '));
  }
}
