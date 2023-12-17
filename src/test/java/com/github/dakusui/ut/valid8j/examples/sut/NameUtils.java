package com.github.dakusui.ut.valid8j.examples.sut;

import static com.github.dakusui.pcond.forms.Predicates.containsString;
import static com.github.dakusui.valid8j.Assertions.precondition;

public enum NameUtils {
  ;

  public static String firstNameOf(String yourName) {
    assert precondition(yourName, containsString(" ")); // <4>
    return yourName.substring(0, yourName.indexOf(' '));
  }
}
