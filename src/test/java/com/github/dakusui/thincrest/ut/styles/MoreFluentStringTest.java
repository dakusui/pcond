package com.github.dakusui.thincrest.ut.styles;

import com.github.dakusui.pcond.fluent.Fluents;
import com.github.dakusui.shared.utils.ut.TestBase;
import org.junit.Test;

import java.util.regex.Pattern;

import static com.github.dakusui.thincrest.TestFluents.assertStatemet;
import static com.github.dakusui.pcond.fluent.Fluents.value;

public class MoreFluentStringTest extends TestBase {
  @Test
  public void test_contains() {
    String var = "world";
    assertStatemet(Fluents.value(var).toUpperCase().then().contains("W"));
  }

  @Test
  public void test_startsWith() {
    String var = "Hello, world";
    assertStatemet(Fluents.value(var).toUpperCase().then().startsWith("H"));
  }

  @Test
  public void test_isEmpty() {
    String var = "";
    assertStatemet(Fluents.value(var).then().isEmpty());
  }

  @Test
  public void test_isEqualTo() {
    String var = "hello";
    assertStatemet(Fluents.value(var).then().isEqualTo("hello"));
  }

  @Test
  public void test_isNullOrEmpty() {
    String var = "";
    assertStatemet(Fluents.value(var).then().isNullOrEmpty());
  }

  @Test
  public void test_matchesRegex() {
    String var = "hello";
    assertStatemet(Fluents.value(var).then().matchesRegex("he.+"));
  }

  @Test
  public void test_equalsIgnoreCase() {
    String var = "hello";
    assertStatemet(Fluents.value(var).then().equalsIgnoreCase("HELLO"));
  }

  @Test
  public void test_findRegexes() {
    String var = "hello";
    assertStatemet(Fluents.value(var).then().findRegexes("he.", "lo"));
  }

  @Test
  public void test_findRegexPatterns() {
    String var = "hello";
    assertStatemet(Fluents.value(var).then().findRegexPatterns(Pattern.compile("he."), Pattern.compile("lo")));
  }


  @Test
  public void test_findSubstrings() {
    String var = "hello";
    assertStatemet(Fluents.value(var).then().findSubstrings("hel", "lo"));
  }
}
