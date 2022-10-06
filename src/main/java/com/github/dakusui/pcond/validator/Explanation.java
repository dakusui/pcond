package com.github.dakusui.pcond.validator;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

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
    return actual != null ?
        format("%s%n%s", message, composeDiff(expected, actual)) :
        message;
  }

  private static String composeDiff(String expected, String actual) {
    String[] e = expected.split(format("%n"));
    String[] a = actual.split(format("%n"));
    List<String> b = new LinkedList<>();
    for (int i = 0; i < a.length; i++) {
      if (i < Math.min(e.length, a.length) && Objects.equals(e[i], a[i])) {
        b.add(format("         %s", a[i]));
      } else {
        b.add(format("Mismatch %s", a[i]));
      }
    }
    return b.stream().collect(joining(format("%n")));
  }

  public static Explanation fromMessage(String msg) {
    return new Explanation(msg, ReportComposer.Utils.composeReport(null, null), ReportComposer.Utils.composeReport(null, null));
  }
}
