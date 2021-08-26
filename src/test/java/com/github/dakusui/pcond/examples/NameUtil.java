package com.github.dakusui.pcond.examples;

import static com.github.dakusui.pcond.Assertions.precondition;
import static com.github.dakusui.pcond.Postconditions.ensureNonNull;
import static com.github.dakusui.pcond.Preconditions.requireArgument;
import static com.github.dakusui.pcond.functions.Functions.length;
import static com.github.dakusui.pcond.functions.Predicates.*;

public enum NameUtil {
  ;

  public static String firstNameOf(String yourName) {
    assert precondition(yourName, containsString(" ")); // <4>
    return yourName.substring(0, yourName.indexOf(' '));
  }
}
