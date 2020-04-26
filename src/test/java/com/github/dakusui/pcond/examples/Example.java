package com.github.dakusui.pcond.examples;

// <1>
import static com.github.dakusui.pcond.Assertions.*;
import static com.github.dakusui.pcond.Postconditions.*;
import static com.github.dakusui.pcond.Preconditions.*;
import static com.github.dakusui.pcond.functions.Functions.*;
import static com.github.dakusui.pcond.functions.Predicates.*;

public class Example {
  public static String hello(String yourName) {
    // <2>
    requireArgument(yourName, and(isNotNull(), transform(length()).check(gt(0)), containsString(" ")));
    String ret = String.format("Hello, %s", firstNameOf(yourName));
    // <3>
    return ensureNonNull(ret);
  }

  private static String firstNameOf(String yourName) {
    assert precondition(yourName, containsString(" ")); // <4>
    return yourName.substring(0, yourName.indexOf(' '));
  }

  public static void main(String[] args) {
    System.out.println(hello("JohnDoe"));
  }
}
