package com.github.dakusui.pcond.examples;

import org.junit.Test;

import java.util.List;
import java.util.function.Function;

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
        .exercise(stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("HELLOWORLD"));
  }

  private static Function<String, String> stringToLowerCase() {
    return function("stringToLowerCase", String::toLowerCase);
  }

  @Test
  public void test2() {
    List<String> givenValues = asList("hello", "world");
    assertWhen(valueOf(givenValues).elementAt(0)
        .exercise(stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("HELLO"));
  }

  @Test
  public void test3() {
    List<String> givenValues = asList("hello", "world");
    assertWhen(valueOf(givenValues).elementAt(0)
        .exercise(stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("HELLO"));
  }


  @Test
  public void test4() {
    assertWhen(
        valueOf("hello").toUpperCase().then().isEqualTo("HELLO"),
        valueOf("world").toLowerCase().then().contains("HELLO"));
  }

  @Test
  public void test5() {
    String s = "HI";
    List<String> strings = asList("HELLO", "WORLD");

    assertWhen(
        valueOf(s).asString().exercise(stringToLowerCase()).then().isEqualTo("HI"),
        valueOf(strings).asListOf((String)null).then().findElementsInOrder("hello", "world")
    );
  }

}
