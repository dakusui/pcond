package com.github.dakusui.pcond.provider;

import static java.lang.String.format;

public class Explanation {
  private final String message;
  private final String expected;
  private final String actual;

  public Explanation(String message, String expected, String actual) {
    this.message = message;
    this.expected = expected;
    this.actual = actual;
  }

  public String message() {
    return this.message;
  }

  public String expected() {
    return this.expected;
  }

  public String actual() {
    return this.actual;
  }

  public String toString() {
    // Did not include "expected" because it is too much overlapping "actual" in most cases.
    return actual != null ?
        format("%s%n%s", message, actual) :
        message;
  }

  public static Explanation fromMessage(String msg) {
    return new Explanation(msg, ReportComposer.Utils.composeReport(null, null), ReportComposer.Utils.composeReport(null, null));
  }
}
