package com.github.dakusui.pcond.examples;

import static com.github.dakusui.valid8j.Assertions.precondition;
import static com.github.dakusui.pcond.forms.Predicates.*;

public enum NameUtils {
  ;

  public static String firstNameOf(String yourName) {
    assert precondition(yourName, containsString(" ")); // <4>
    return yourName.substring(0, yourName.indexOf(' '));
  }
}
