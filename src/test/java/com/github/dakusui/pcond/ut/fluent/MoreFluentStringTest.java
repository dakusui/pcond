package com.github.dakusui.pcond.ut.fluent;

import com.github.dakusui.pcond.fluent.Fluents;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;

import java.util.regex.Pattern;

import static com.github.dakusui.pcond.fluent.Fluents.assertWhen;
import static com.github.dakusui.pcond.fluent.Fluents.value;

public class MoreFluentStringTest extends TestBase {
  @Test
  public void test_contains() {
    String var = "world";
    assertWhen(Fluents.value(var).toUpperCase().then().contains("W"));
  }

  @Test
  public void test_startsWith() {
    String var = "Hello, world";
    assertWhen(Fluents.value(var).toUpperCase().then().startsWith("H"));
  }

  @Test
  public void test_isEmpty() {
    String var = "";
    assertWhen(Fluents.value(var).then().isEmpty());
  }

  @Test
  public void test_isEqualTo() {
    String var = "hello";
    assertWhen(Fluents.value(var).then().isEqualTo("hello"));
  }

  @Test
  public void test_isNullOrEmpty() {
    String var = "";
    assertWhen(Fluents.value(var).then().isNullOrEmpty());
  }

  @Test
  public void test_matchesRegex() {
    String var = "hello";
    assertWhen(Fluents.value(var).then().matchesRegex("he.+"));
  }

  @Test
  public void test_equalsIgnoreCase() {
    String var = "hello";
    assertWhen(Fluents.value(var).then().equalsIgnoreCase("HELLO"));
  }

  @Test
  public void test_findRegexes() {
    String var = "hello";
    assertWhen(Fluents.value(var).then().findRegexes("he.", "lo"));
  }

  @Test
  public void test_findRegexPatterns() {
    String var = "hello";
    assertWhen(Fluents.value(var).then().findRegexPatterns(Pattern.compile("he."), Pattern.compile("lo")));
  }


  @Test
  public void test_findSubstrings() {
    String var = "hello";
    assertWhen(Fluents.value(var).then().findSubstrings("hel", "lo"));
  }
}
