package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.utils.TestBase;
import org.junit.Test;
import org.opentest4j.AssertionFailedError;

import java.util.List;

import static com.github.dakusui.crest.Crest.*;
import static com.github.dakusui.pcond.functions.Predicates.equalsIgnoreCase;
import static java.util.Arrays.asList;

public class EaterTest extends TestBase {
  @Test(expected = ExpectedException.class)
  public void test() {
    String targetContainer = "ZabcZdefZXYZz";
    try {
      assertThat(
          targetContainer,
          asString(substringAfterRegex("ab.").after("d.f").after("XYZ").$()).equalTo("Z").$()
      );
    } catch (AssertionFailedError e) {
      e.printStackTrace();
      throw new ExpectedException();
    }
  }

  @Test(expected = ExpectedException.class)
  public void test2() {
    String targetContainer = "ZabcZdefZxyzZ";
    try {
      assertThat(
          targetContainer,
          asString(substringAfterRegex("ab.").after("d.f").after("XYZ").$()).equalTo("Z").$()
      );
    } catch (AssertionFailedError e) {
      e.printStackTrace();
      throw new ExpectedException();
    }
  }

  @Test(expected = ExpectedException.class)
  public void test2multiline() {
    String targetContainer = "Zabc\nZdef\nZxyzZ";
    try {
      assertThat(
          targetContainer,
          asString(substringAfterRegex("ab.").after("d.f").after("XYZ").$()).equalTo("Z").$()
      );
    } catch (AssertionFailedError e) {
      e.printStackTrace();
      throw new ExpectedException();
    }
  }

  @Test
  public void test3() {
    String targetContainer = "ZabcZdefZxyzZ";
    assertThat(
        targetContainer,
        allOf(
            asString(substringAfterRegex("ab.").after("d.f").after("xyz").$()).equalTo("Z").$()
        )
    );
  }

  @Test
  public void test3multiAssertions() {
    String targetContainer = "ZabcZdefZxyzZ";
    assertThat(
        targetContainer,
        allOf(
            asString(substringAfterRegex("ab.").after("d.f").after("xyz").$()).equalTo("Z").$(),
            asString(substringAfterRegex("ab.").after("d.f").after("xyz").$()).equalTo("Z").$()
        )
    );
  }

  @Test
  public void test3multiline() {
    String targetContainer = "ZabcZ\ndefZ\nxyzZ";
    assertThat(
        targetContainer,
        asString(substringAfterRegex("ab.").after("d.f").after("xyz").$()).equalTo("Z").$()
    );
  }

  @Test(expected = ExpectedException.class)
  public void test4() {
    List<String> targetContainer = asList("Z", "abc", "Z", "def", "Z", "xyz", "Z");
    try {
      assertThat(
          targetContainer,
          Crest.allOf(
              asListOf(
                  String.class,
                  sublistAfterElement("abc").afterElement("def").afterElement("XYZ").$()
              ).$()));
    } catch (AssertionFailedError e) {
      e.printStackTrace();
      throw new ExpectedException();
    }
  }

  @Test
  public void test5() {
    List<String> targetContainer = asList("Z", "abc", "Z", "def", "Z", "xyz", "Z");
    assertThat(
        targetContainer,
        asListOf(String.class,
            sublistAfterElement("abc")
                .afterElement("def")
                .after(equalsIgnoreCase("XYZ")).$())
            .isNotEmpty().$()
    );
  }

  private static class ExpectedException extends RuntimeException {
  }
}
