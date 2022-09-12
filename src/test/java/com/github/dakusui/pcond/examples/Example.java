package com.github.dakusui.pcond.examples;

// <1>
import static com.github.dakusui.pcond.Assertions.precondition;
import static com.github.dakusui.pcond.Ensures.ensureNonNull;
import static com.github.dakusui.pcond.Requires.requireArgument;
import static com.github.dakusui.pcond.forms.Functions.*;
import static com.github.dakusui.pcond.forms.Predicates.*;

public class Example {
  public static String hello(String yourName) {
    // <2>
    requireArgument(yourName, and(isNotNull(), transform(length()).check(gt(0)), containsString(" ")));
    String ret = String.format("Hello, %s", firstNameOf(yourName));
    // <3>
    return ensureNonNull(ret);
  }

  private static String firstNameOf(String yourName) {
    // <4>
    assert precondition(yourName, containsString(" "));
    return yourName.substring(0, yourName.indexOf(' '));
  }

  public static void main(String[] args) {
    System.out.println(hello("JohnDoe"));
  }
}
