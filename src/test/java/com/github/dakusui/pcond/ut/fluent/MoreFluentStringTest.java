package com.github.dakusui.pcond.ut.fluent;

import org.junit.Test;

import java.util.regex.Pattern;

import static com.github.dakusui.pcond.fluent.MoreFluents.assertWhen;
import static com.github.dakusui.pcond.fluent.MoreFluents.valueOf;

public class MoreFluentStringTest {
  @Test
  public void test_contains() {
    String var = "world";
    assertWhen(valueOf(var).toUpperCase().then().contains("W"));
  }

  @Test
  public void test_startsWith() {
    String var = "Hello, world";
    assertWhen(valueOf(var).toUpperCase().then().startsWith("H"));
  }

  @Test
  public void test_isEmpty() {
    String var = "";
    assertWhen(valueOf(var).then().isEmpty());
  }

  @Test
  public void test_isEqualTo() {
    String var = "hello";
    assertWhen(valueOf(var).then().isEqualTo("hello"));
  }

  @Test
  public void test_isNullOrEmpty() {
    String var = "";
    assertWhen(valueOf(var).then().isNullOrEmpty());
  }

  @Test
  public void test_matchesRegex() {
    String var = "hello";
    assertWhen(valueOf(var).then().matchesRegex("he.+"));
  }

  @Test
  public void test_equalsIgnoreCase() {
    String var = "hello";
    assertWhen(valueOf(var).then().equalsIgnoreCase("HELLO"));
  }

  @Test
  public void test_findRegexes() {
    String var = "hello";
    assertWhen(valueOf(var).then().findRegexes("he.", "lo"));
  }

  @Test
  public void test_findRegexPatterns() {
    String var = "hello";
    assertWhen(valueOf(var).then().findRegexPatterns(Pattern.compile("he."), Pattern.compile("lo")));
  }


  @Test
  public void test_findSubstrings() {
    String var = "hello";
    assertWhen(valueOf(var).then().findSubstrings("hel", "lo"));
  }
}
