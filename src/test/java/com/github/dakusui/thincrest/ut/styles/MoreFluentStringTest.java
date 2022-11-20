package com.github.dakusui.thincrest.ut.styles;

import com.github.dakusui.pcond.fluent.Fluents;
import com.github.dakusui.shared.utils.ut.TestBase;
import org.junit.Test;

import java.util.regex.Pattern;

import static com.github.dakusui.thincrest.TestFluents.assertStatement;

public class MoreFluentStringTest extends TestBase {
  @Test
  public void test_contains() {
    String var = "world";
    assertStatement(Fluents.stringValue(var).toUpperCase().then().contains("W"));
  }

  @Test
  public void test_startsWith() {
    String var = "Hello, world";
    assertStatement(Fluents.stringValue(var).toUpperCase().then().startsWith("H"));
  }

  @Test
  public void test_isEmpty() {
    String var = "";
    assertStatement(Fluents.stringValue(var).then().isEmpty());
  }

  @Test
  public void test_isEqualTo() {
    String var = "hello";
    assertStatement(Fluents.stringValue(var).then().isEqualTo("hello"));
  }

  @Test
  public void test_isNullOrEmpty() {
    String var = "";
    assertStatement(Fluents.stringValue(var).then().isNullOrEmpty());
  }

  @Test
  public void test_matchesRegex() {
    String var = "hello";
    assertStatement(Fluents.stringValue(var).then().matchesRegex("he.+"));
  }

  @Test
  public void test_equalsIgnoreCase() {
    String var = "hello";
    assertStatement(Fluents.stringValue(var).then().equalsIgnoreCase("HELLO"));
  }

  @Test
  public void test_findRegexes() {
    String var = "hello";
    assertStatement(Fluents.stringValue(var).then().findRegexes("he.", "lo"));
  }

  @Test
  public void test_findRegexPatterns() {
    String var = "hello";
    assertStatement(Fluents.stringValue(var).then().findRegexPatterns(Pattern.compile("he."), Pattern.compile("lo")));
  }


  @Test
  public void test_findSubstrings() {
    String var = "hello";
    assertStatement(Fluents.stringValue(var).then().findSubstrings("hel", "lo"));
  }
}
