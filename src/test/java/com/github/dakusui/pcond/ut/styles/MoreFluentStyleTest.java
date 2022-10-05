package com.github.dakusui.pcond.ut.styles;

import com.github.dakusui.pcond.fluent.Fluents;
import com.github.dakusui.pcond.utils.ut.TestBase;
import com.github.dakusui.pcond.validator.exceptions.PostconditionViolationException;
import com.github.dakusui.pcond.validator.exceptions.PreconditionViolationException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.AssumptionViolatedException;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.fluent.Fluents.*;
import static com.github.dakusui.pcond.forms.Predicates.containsString;
import static com.github.dakusui.pcond.forms.Predicates.not;
import static com.github.dakusui.pcond.utils.TestUtils.stringToLowerCase;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@RunWith(Enclosed.class)
public class MoreFluentStyleTest {

  public static class ForTestAssertionsTest extends TestBase {
    @Test(expected = ComparisonFailure.class)
    public void string_assertThatTest_failed() {
      String givenValue = "helloWorld";
      try {
        assertThat(Fluents.value(givenValue)
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
    public void string_assertThatTest_passed() {
      String givenValue = "helloWorld";
      assertThat(value(givenValue)
          .exercise(stringToLowerCase())
          .then()
          .asString()
          .isEqualTo("helloworld"));
    }

    @Test
    public void string_assertThat_useValue_passed() {
      String givenValue = "helloWorld";
      assertThat(value(givenValue)
          .exercise(stringToLowerCase())
          .then()
          .asString()
          .isEqualTo("helloworld"));
    }

    @Test
    public void int_assertThatTest_passed() {
      int givenValue = 1234;
      assertThat(Fluents.value(givenValue)
          .then()
          .isEqualTo(1234));
    }

    @Test
    public void boolean_assertThatTest_passed() {
      boolean givenValue = true;
      assertThat(Fluents.value(givenValue)
          .then()
          .isEqualTo(true));
    }

    @Test
    public void object_assertThatTest_passed() {
      Object givenValue = new Object() {
        @Override
        public String toString() {
          return "OBJECT";
        }
      };
      assertThat(Fluents.value(givenValue)
          .then()
          .intoStringWith(Object::toString)
          .isEqualTo("OBJECT"));
    }

    @Test
    public void list_assertThatTest_passed() {
      List<String> givenValue = asList("hello", "world");
      assertThat(value(givenValue)
          .then()
          .isEqualTo(asList("hello", "world")));
    }

    @Test
    public void stream_assertThatTest_passed() {
      Stream<String> givenValue = Stream.of("hello", "world");
      assertThat(Fluents.value(givenValue)
          .then()
          .intoListWith(v -> v.collect(toList()))
          .isEqualTo(asList("hello", "world")));
    }

    @Test(expected = ComparisonFailure.class)
    public void multiAssertAll_failed() {
      try {
        assertAll(
            value("hello").toUpperCase().then().isEqualTo("HELLO"),
            value("world").toLowerCase().then().contains("WORLD"));
      } catch (ComparisonFailure e) {
        MatcherAssert.assertThat(e.getActual(), CoreMatchers.containsString("stringIsEqualTo[\"HELLO\"]->true"));
        MatcherAssert.assertThat(e.getActual(), CoreMatchers.containsString("containsString[\"WORLD\"] ->false"));
        MatcherAssert.assertThat(e.getExpected(), CoreMatchers.containsString("stringIsEqualTo[\"HELLO\"]->true"));
        MatcherAssert.assertThat(e.getExpected(), CoreMatchers.containsString("containsString[\"WORLD\"] ->true"));
        throw e;
      }
    }

    @Test
    public void assertAll_passed() {
      assertAll(
          value("hello").toUpperCase().then().isEqualTo("HELLO"),
          value("world").toLowerCase().then().contains("world"));
    }

    @Test(expected = AssumptionViolatedException.class)
    public void assumeThatTest_failed() {
      String givenValue = "helloWorld";
      try {
        assumeThat(value(givenValue)
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
    public void assumeThatTest_passed() {
      String givenValue = "helloWorld";
      assumeThat(value(givenValue)
          .exercise(stringToLowerCase())
          .then()
          .asString()
          .isEqualTo("helloworld"));
    }

    @Test(expected = AssumptionViolatedException.class)
    public void multiAssumeAll_failed() {
      try {
        assumeAll(
            value("hello").toUpperCase().then().isEqualTo("HELLO"),
            value("world").toLowerCase().then().contains("WORLD"));
      } catch (AssumptionViolatedException e) {
        MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("stringIsEqualTo[\"HELLO\"]->true"));
        MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("containsString[\"WORLD\"] ->false"));
        throw e;
      }
    }

    @Test
    public void multiAssumeAll_passed() {
      assumeAll(
          value("hello").toUpperCase().then().isEqualTo("HELLO"),
          value("world").toLowerCase().then().contains("world"));
    }

    @Test
    public void test_valueMethod() {
      MatcherAssert.assertThat(Fluents.value(), CoreMatchers.equalTo(null));
    }
  }

  public static class ForRequiresTest extends TestBase {
    @Test(expected = IllegalArgumentException.class)
    public void requireArgumentsTest_failing() {
      try {
        requireArguments(
            value("hello").toUpperCase().then().isEqualTo("HELLO"),
            value("world").toLowerCase().then().contains("WORLD").verifyWith(not(containsString("w"))));
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        MatcherAssert.assertThat(message, CoreMatchers.containsString("stringIsEqualTo[\"HELLO\"]->true"));
        MatcherAssert.assertThat(message, CoreMatchers.containsString("containsString[\"WORLD\"]->false"));
        throw e;
      }
    }

    @Test(expected = IllegalStateException.class)
    public void requireStatesTest_failing() {
      try {
        requireStates(
            value("hello").toUpperCase().then().isEqualTo("HELLO"),
            value("world").toLowerCase().then().contains("WORLD").verifyWith(not(containsString("w"))));
      } catch (IllegalStateException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        MatcherAssert.assertThat(message, CoreMatchers.containsString("stringIsEqualTo[\"HELLO\"]->true"));
        MatcherAssert.assertThat(message, CoreMatchers.containsString("containsString[\"WORLD\"]->false"));
        throw e;
      }


    }

    @Test(expected = PreconditionViolationException.class)
    public void requireValuesTest_failing() {
      try {
        requireValues(
            value("hello").toUpperCase().then().isEqualTo("HELLO"),
            value("world").toLowerCase().then().contains("WORLD").verifyWith(not(containsString("w"))));
      } catch (PreconditionViolationException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        MatcherAssert.assertThat(message, CoreMatchers.containsString("stringIsEqualTo[\"HELLO\"]->true"));
        MatcherAssert.assertThat(message, CoreMatchers.containsString("containsString[\"WORLD\"]->false"));
        throw e;
      }
    }
  }

  public static class ForEnsuresTest extends TestBase {
    @Test(expected = PostconditionViolationException.class)
    public void ensureValuesTest_failing() {
      try {
        ensureValues(
            value("hello").toUpperCase().then().isEqualTo("HELLO"),
            value("world").toLowerCase().then().contains("WORLD").verifyWith(not(containsString("w"))));
      } catch (PostconditionViolationException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        MatcherAssert.assertThat(message, CoreMatchers.containsString("stringIsEqualTo[\"HELLO\"]->true"));
        MatcherAssert.assertThat(message, CoreMatchers.containsString("containsString[\"WORLD\"]->false"));
        throw e;
      }
    }

    @Test(expected = IllegalStateException.class)
    public void ensureStatesTest_failing() {
      try {
        ensureStates(
            value("hello").toUpperCase().then().isEqualTo("HELLO"),
            value("world").toLowerCase().then().contains("WORLD").verifyWith(not(containsString("w"))));
      } catch (IllegalStateException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        MatcherAssert.assertThat(message, CoreMatchers.containsString("stringIsEqualTo[\"HELLO\"]->true"));
        MatcherAssert.assertThat(message, CoreMatchers.containsString("containsString[\"WORLD\"]->false"));
        throw e;
      }
    }
  }
}
