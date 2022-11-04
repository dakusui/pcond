package com.github.dakusui.pcond.validator;

import java.util.Arrays;
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
    this.expected = joinStringArray(splitAndTrim(expected));
    this.actual = joinStringArray(splitAndTrim(actual));
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
    String[] e = splitAndTrim(expected);
    String[] a = splitAndTrim(actual);
    List<String> b = new LinkedList<>();
    for (int i = 0; i < Math.max(a.length, e.length); i++) {
      if (i < Math.min(e.length, a.length) && Objects.equals(e[i], a[i])) {
        b.add(format("          %s", a[i]));
      } else {
        b.add(format("Mismatch<:%s", i < e.length ? e[i] : ""));
        b.add(format("Mismatch>:%s", i < a.length ? a[i] : ""));
      }
    }
    return b.stream().collect(joining(format("%n")));
  }

  public static Explanation fromMessage(String msg) {
    return new Explanation(msg, ReportComposer.Utils.composeReport(null, null), ReportComposer.Utils.composeReport(null, null));
  }

  private static String[] splitAndTrim(String expected) {
    String[] in = expected.split(format("%n"));
    List<String> out = new LinkedList<>();
    boolean nonEmptyFound = false;
    for (int i = in.length - 1; i >= 0; i--) {
      if (!"".equals(in[i]))
        nonEmptyFound = true;
      if (nonEmptyFound)
        out.add(0, in[i]);
    }
    return out.toArray(new String[0]);
  }

  private static String joinStringArray(String[] stringArray) {
    return Arrays.stream(stringArray).collect(joining(format("%n")));
  }
}
