package com.github.dakusui.pcond;

import java.util.function.Predicate;

public enum Assertions {
  ;

  public static <T> void assertValue(T t, Predicate<? super T> predicate) {
    assert predicate.test(t) : "Violated: " + predicate.toString();
  }

  public static void assertBoolean(boolean t, Predicate<? super Boolean> predicate) {
    assert predicate.test(t) : "Violated: " + predicate.toString();
  }

  public static void assertByte(byte t, Predicate<? super Byte> predicate) {
    assert predicate.test(t) : "Violated: " + predicate.toString();
  }

  public static void assertChar(char t, Predicate<? super Character> predicate) {
    assert predicate.test(t) : "Violated: " + predicate.toString();
  }

  public static void assertShort(short t, Predicate<? super Short> predicate) {
    assert predicate.test(t) : "Violated: " + predicate.toString();
  }

  public static void assertInt(int t, Predicate<? super Integer> predicate) {
    assert predicate.test(t) : "Violated: " + predicate.toString();
  }

  public static void assertLong(long t, Predicate<? super Long> predicate) {
    assert predicate.test(t) : "Violated: " + predicate.toString();
  }

  public static void assertFloat(float t, Predicate<? super Float> predicate) {
    assert predicate.test(t) : "Violated: " + predicate.toString();
  }

  public static void assertDouble(double t, Predicate<? super Double> predicate) {
    assert predicate.test(t) : "Violated: " + predicate.toString();
  }
}
