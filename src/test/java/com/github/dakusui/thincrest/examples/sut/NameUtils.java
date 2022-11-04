package com.github.dakusui.thincrest.examples.sut;

public enum NameUtils {
  ;

  public static String firstNameOf(String yourName) {
    return yourName.substring(0, yourName.indexOf(' '));
  }
}
