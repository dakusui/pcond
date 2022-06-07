package com.github.dakusui.pcond.ut.styles;

import com.github.dakusui.pcond.MoreFluents;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.AssumptionViolatedException;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.MoreFluents.*;
import static com.github.dakusui.pcond.utils.TestUtils.stringToLowerCase;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class MoreFluentStyleTest {
  @Test(expected = ComparisonFailure.class)
  public void string_assertWhenTest_failed() {
    String givenValue = "helloWorld";
    try {
      assertWhen(valueOf(givenValue)
          .exercise(stringToLowerCase())
          .then()
          .asString()
          .isEqualTo("HELLOWORLD"));
    } catch (ComparisonFailure e) {
      MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("Value:\"helloWorld\" violated: WHEN(stringToLowerCase->castTo[String] stringIsEqualTo[\"HELLOWORLD\"]) "));
      throw e;
    }
  }

  @Test
  public void string_assertWhenTest_passed() {
    String givenValue = "helloWorld";
    assertWhen(valueOf(givenValue)
        .exercise(stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("helloworld"));
  }

  @Test
  public void string_assertWhen_useValue_passed() {
    String givenValue = "helloWorld";
    assertWhen(valueOf(givenValue)
        .exercise(stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("helloworld"));
  }

  @Test
  public void int_assertWhenTest_passed() {
    int givenValue = 1234;
    assertWhen(valueOf(givenValue)
        .then()
        .isEqualTo(1234));
  }

  @Test
  public void boolean_assertWhenTest_passed() {
    boolean givenValue = true;
    assertWhen(valueOf(givenValue)
        .then()
        .isEqualTo(true));
  }

  @Test
  public void object_assertWhenTest_passed() {
    Object givenValue = new Object() {
      @Override
      public String toString() {
        return "OBJECT";
      }
    };
    assertWhen(valueOf(givenValue)
        .then()
        .intoStringWith(Object::toString)
        .isEqualTo("OBJECT"));
  }

  @Test
  public void list_assertWhenTest_passed() {
    List<String> givenValue = asList("hello", "world");
    assertWhen(valueOf(givenValue)
        .then()
        .isEqualTo(asList("hello", "world")));
  }

  @Test
  public void stream_assertWhenTest_passed() {
    Stream<String> givenValue = Stream.of("hello", "world");
    assertWhen(valueOf(givenValue)
        .then()
        .intoListWith(v -> v.collect(toList()))
        .isEqualTo(asList("hello", "world")));
  }

  @Test(expected = ComparisonFailure.class)
  public void multiAssertWhen_failed() {
    try {
      assertWhen(
          valueOf("hello").toUpperCase().then().isEqualTo("HELLO"),
          valueOf("world").toLowerCase().then().contains("WORLD"));
    } catch (ComparisonFailure e) {
      MatcherAssert.assertThat(e.getActual(), CoreMatchers.containsString("stringIsEqualTo[\"HELLO\"]->true"));
      MatcherAssert.assertThat(e.getActual(), CoreMatchers.containsString("containsString[\"WORLD\"] ->false"));
      MatcherAssert.assertThat(e.getExpected(), CoreMatchers.containsString("stringIsEqualTo[\"HELLO\"]->true"));
      MatcherAssert.assertThat(e.getExpected(), CoreMatchers.containsString("containsString[\"WORLD\"] ->true"));
      throw e;
    }
  }

  @Test
  public void multiAssertWhen_passed() {
    assertWhen(
        valueOf("hello").toUpperCase().then().isEqualTo("HELLO"),
        valueOf("world").toLowerCase().then().contains("world"));
  }

  @Test(expected = AssumptionViolatedException.class)
  public void assumeWhenTest_failed() {
    String givenValue = "helloWorld";
    try {
      assumeWhen(valueOf(givenValue)
          .exercise(stringToLowerCase())
          .then()
          .asString()
          .isEqualTo("HELLOWORLD"));
    } catch (AssumptionViolatedException e) {
      MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("Value:\"helloWorld\" violated: WHEN(stringToLowerCase->castTo[String] stringIsEqualTo[\"HELLOWORLD\"])"));
      throw e;
    }
  }

  @Test
  public void assumeWhenTest_passed() {
    String givenValue = "helloWorld";
    assumeWhen(valueOf(givenValue)
        .exercise(stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("helloworld"));
  }

  @Test(expected = AssumptionViolatedException.class)
  public void multiAssumeWhen_failed() {
    try {
      assumeWhen(
          valueOf("hello").toUpperCase().then().isEqualTo("HELLO"),
          valueOf("world").toLowerCase().then().contains("WORLD"));
    } catch (AssumptionViolatedException e) {
      MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("stringIsEqualTo[\"HELLO\"]->true"));
      MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("containsString[\"WORLD\"] ->false"));
      throw e;
    }
  }

  @Test
  public void multiAssumeWhen_passed() {
    assumeWhen(
        valueOf("hello").toUpperCase().then().isEqualTo("HELLO"),
        valueOf("world").toLowerCase().then().contains("world"));
  }

  @Test
  public void test_valueMethod() {
    MatcherAssert.assertThat(MoreFluents.value(), CoreMatchers.equalTo(null));
  }
}
