package com.github.dakusui.pcond.examples;

import com.github.dakusui.pcond.utils.TestUtils;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.List;

import static com.github.dakusui.pcond.MoreFluents.assertWhen;
import static com.github.dakusui.pcond.MoreFluents.valueOf;
import static com.github.dakusui.pcond.forms.Printables.function;
import static java.util.Arrays.asList;

@SuppressWarnings("NewClassNamingConvention")
public class MoreFluentStyleExample {
  @Test
  public void test() {
    String givenValue = "helloWorld";
    assertWhen(valueOf(givenValue)
        .exercise(TestUtils.stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("HELLOWORLD"));
  }

  @Test
  public void test2() {
    List<String> givenValues = asList("hello", "world");
    assertWhen(valueOf(givenValues).elementAt(0)
        .exercise(TestUtils.stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("HELLO"));
  }

  @Test
  public void test3() {
    List<String> givenValues = asList("hello", "world");
    assertWhen(valueOf(givenValues).elementAt(0)
        .exercise(TestUtils.stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("HELLO"));
  }


  @Test//(expected = ComparisonFailure.class)
  public void test4() {
    try {
      assertWhen(
          valueOf("hello").toUpperCase().then().isEqualTo("HELLO"),
          valueOf("world").toLowerCase().then().contains("WORLD"));
    } catch (ComparisonFailure e) {
      throw e;
    }
  }

  @Test
  public void test5() {
    String s = "HI";
    List<String> strings = asList("HELLO", "WORLD");

    assertWhen(
        valueOf(s).asString().exercise(TestUtils.stringToLowerCase()).then().isEqualTo("HI"),
        valueOf(strings).asListOf((String)null).then().findElementsInOrder("hello", "world")
    );
  }

}
