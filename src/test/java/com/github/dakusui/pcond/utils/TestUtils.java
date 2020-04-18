package com.github.dakusui.pcond.utils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Objects;

public enum TestUtils {
  ;

  static final        PrintStream STDOUT = System.out;
  static final        PrintStream STDERR = System.err;
  public static final PrintStream NOP    = new PrintStream(new OutputStream() {
    @Override
    public void write(int b) {
    }
  });

  /**
   * Typically called from a method annotated with {@literal @}{@code Before} method.
   */
  public static void suppressStdOutErrIfRunUnderSurefire() {
    if (isRunUnderPitest() || TestUtils.isRunUnderSurefire()) {
      System.setOut(NOP);
      System.setErr(NOP);
    }
  }

  /**
   * Typically called from a method annotated with {@literal @}{@code After} method.
   */
  public static void restoreStdOutErr() {
    System.setOut(STDOUT);
    System.setErr(STDERR);
  }

  public static boolean isRunUnderSurefire() {
    return System.getProperty("surefire.real.class.path") != null;
  }

  public static boolean isRunUnderPitest() {
    return Objects.equals(System.getProperty("underpitest"), "yes");
  }

  public static String firstLineOf(String multilineString) {
    return lineAt(multilineString, 0);
  }

  public static String lineAt(String multilineString, int position) {
    return split(multilineString)[position];
  }

  public static String[] split(String multilineString) {
    return multilineString.split("\\r?\\n");
  }

  public static int numLines(String multilineString) {
    return split(multilineString).length;
  }
}
