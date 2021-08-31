package com.github.dakusui.pcond.examples;

// <1>

import static com.github.dakusui.pcond.Postconditions.ensureNonNull;
import static com.github.dakusui.pcond.Preconditions.requireArgument;
import static com.github.dakusui.pcond.functions.Functions.length;
import static com.github.dakusui.pcond.functions.Predicates.*;

public class ExampleDbC {
  public static void main(String[] args) {
    System.out.println(hello("JohnDoe"));
  }

  public static String hello(String yourName) {
    // <2>
    requireArgument(yourName, and(isNotNull(), transform(length()).check(gt(0)), containsString(" ")));
    String ret = String.format("Hello, %s", NameUtils.firstNameOf(yourName));
    // <3>
    return ensureNonNull(ret);
  }
}
