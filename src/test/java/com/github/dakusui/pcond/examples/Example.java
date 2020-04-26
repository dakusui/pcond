package com.github.dakusui.pcond.examples;

import static com.github.dakusui.pcond.Assertions.that;
import static com.github.dakusui.pcond.Postconditions.ensureNonNull;
import static com.github.dakusui.pcond.Preconditions.requireArgument;
import static com.github.dakusui.pcond.functions.Predicates.*;

public class Example {
  public static String hello(String yourFirstName) {
    requireArgument(yourFirstName, and(isNotNull(), not(isEmptyString()), not(containsString(" "))));

    String format = "Hello, %s";
    assert that(format, isNotNull());

    String ret = String.format(format, yourFirstName);
    return ensureNonNull(ret);
  }

  public static void main(String[] args) {
    System.out.println(hello("John"));
    System.out.println(hello("John Doe"));
  }
}
