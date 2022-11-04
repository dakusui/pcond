package com.github.dakusui.thincrest.ut.styles;

import com.github.dakusui.thincrest.TestFluents;
import com.github.dakusui.pcond.fluent.Fluents;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.pcond.utils.ut.TestBase;
import com.github.dakusui.pcond.validator.exceptions.PostconditionViolationException;
import com.github.dakusui.pcond.validator.exceptions.PreconditionViolationException;
import com.github.dakusui.valid8j.ValidationFluents;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
    public void expectingDifferentException_testFailing() {
      try {
        String givenValue = "helloWorld";
        TestFluents.assertThat(value(givenValue)
            .expectException(ArrayIndexOutOfBoundsException.class, Printables.function("substring100", (String s) -> s.substring(100)))
            .getMessage()
            .then()
            .asString()
            .isEqualTo("HELLOWORLD"));
      } catch (ComparisonFailure e) {
        e.printStackTrace();
        MatcherAssert.assertThat(e.getMessage(), CoreMatchers.allOf(
            CoreMatchers.containsString("StringIndexOutOfBounds"),
            CoreMatchers.containsString("isInstanceOf")));
        throw e;
      }
    }

    @Test(expected = ComparisonFailure.class)
    public void expectingExceptionButNotThrown_testFailing() {
      try {
        String givenValue = "helloWorld";
        TestFluents.assertThat(value(givenValue)
            .expectException(StringIndexOutOfBoundsException.class, Printables.function("substring0", (String s) -> s.substring(0)))
            .getMessage()
            .then()
            .asString()
            .isEqualTo("HELLOWORLD"));
      } catch (ComparisonFailure e) {
        e.printStackTrace();
        MatcherAssert.assertThat(e.getMessage(), CoreMatchers.allOf(
            CoreMatchers.containsString("exceptionThrown"),
            CoreMatchers.containsString("exceptionClass:StringIndexOutOfBoundsException")));
        throw e;
      }
    }

    @Test
    public void expectedExceptionThrown_testPassing() {
      String givenValue = "helloWorld";
      TestFluents.assertThat(value(givenValue)
          .expectException(StringIndexOutOfBoundsException.class, Printables.function("substring100", (String s) -> s.substring(100)))
          .getMessage()
          .then()
          .asString()
          .isEqualTo("String index out of range: -90"));
    }

    @Test(expected = ComparisonFailure.class)
    public void string_assertThatTest_failed() {
      String givenValue = "helloWorld";
      try {
        TestFluents.assertThat(Fluents.value(givenValue)
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
      TestFluents.assertThat(value(givenValue)
          .exercise(stringToLowerCase())
          .then()
          .asString()
          .isEqualTo("helloworld"));
    }

    @Test
    public void string_assertThat_useValue_passed() {
      String givenValue = "helloWorld";
      TestFluents.assertThat(value(givenValue)
          .exercise(stringToLowerCase())
          .then()
          .asString()
          .isEqualTo("helloworld"));
    }

    @Test
    public void int_assertThatTest_passed() {
      int givenValue = 1234;
      TestFluents.assertThat(Fluents.value(givenValue)
          .then()
          .isEqualTo(1234));
    }

    @Test
    public void boolean_assertThatTest_passed() {
      boolean givenValue = true;
      TestFluents.assertThat(Fluents.value(givenValue)
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
      TestFluents.assertThat(Fluents.value(givenValue)
          .then()
          .intoStringWith(Object::toString)
          .isEqualTo("OBJECT"));
    }

    @Test
    public void list_assertThatTest_passed() {
      List<String> givenValue = asList("hello", "world");
      TestFluents.assertThat(value(givenValue)
          .then()
          .isEqualTo(asList("hello", "world")));
    }

    @Test
    public void stream_assertThatTest_passed() {
      Stream<String> givenValue = Stream.of("hello", "world");
      TestFluents.assertThat(Fluents.value(givenValue)
          .then()
          .intoListWith(v -> v.collect(toList()))
          .isEqualTo(asList("hello", "world")));
    }

    @Test(expected = ComparisonFailure.class)
    public void multiAssertAll_failed() {
      try {
        TestFluents.assertAll(
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
      TestFluents.assertAll(
          value("hello").toUpperCase().then().isEqualTo("HELLO"),
          value("world").toLowerCase().then().contains("world"));
    }

    @Test(expected = AssumptionViolatedException.class)
    public void assumeThatTest_failed() {
      String givenValue = "helloWorld";
      try {
        TestFluents.assumeThat(value(givenValue)
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
      TestFluents.assumeThat(value(givenValue)
          .exercise(stringToLowerCase())
          .then()
          .asString()
          .isEqualTo("helloworld"));
    }

    @Test(expected = AssumptionViolatedException.class)
    public void multiAssumeAll_failed() {
      try {
        TestFluents.assumeAll(
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
      TestFluents.assumeAll(
          value("hello").toUpperCase().then().isEqualTo("HELLO"),
          value("world").toLowerCase().then().contains("world"));
    }

    @Test
    public void test_valueMethod() {
      MatcherAssert.assertThat(Fluents.$(), CoreMatchers.equalTo(null));
    }
  }

  public static class ForRequiresTest extends TestBase {
    @Test(expected = IllegalArgumentException.class)
    public void requireArgumentsTest_failing() {
      try {
        ValidationFluents.requireArguments(
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
        ValidationFluents.requireStates(
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
        ValidationFluents.require(
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

    @Test
    public void requireArgument_passing() {
      String givenValue = "helloWorld";
      ValidationFluents.requireArgument(value(givenValue)
          .exercise(stringToLowerCase())
          .then()
          .asString()
          .isEqualTo("helloworld"));
    }

    @Test
    public void requireValue_passing() {
      String givenValue = "helloWorld";
      MatcherAssert.assertThat(
          ValidationFluents.require(value(givenValue)
              .exercise(stringToLowerCase())
              .then()
              .asString()
              .isEqualTo("helloworld")),
          Matchers.equalTo(givenValue));
    }

    @Test
    public void reqireState_passing() {
      String givenValue = "helloWorld";
      MatcherAssert.assertThat(
          ValidationFluents.requireState(value(givenValue)
              .exercise(stringToLowerCase())
              .then()
              .asString()
              .isEqualTo("helloworld")),
          Matchers.equalTo(givenValue));
    }
  }

  public static class ForEnsuresTest extends TestBase {
    @Test(expected = PostconditionViolationException.class)
    public void ensureValuesTest_failing() {
      try {
        ValidationFluents.ensure(
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
        ValidationFluents.ensureStates(
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

    @Test
    public void ensureValue_passing() {
      String givenValue = "helloWorld";
      MatcherAssert.assertThat(
          ValidationFluents.ensure(value(givenValue)
              .exercise(stringToLowerCase())
              .then()
              .asString()
              .isEqualTo("helloworld")),
          CoreMatchers.equalTo(givenValue));
    }

    @Test
    public void ensureState_passing() {
      String givenValue = "helloWorld";
      MatcherAssert.assertThat(
          ValidationFluents.ensureState(value(givenValue)
              .exercise(stringToLowerCase())
              .then()
              .asString()
              .isEqualTo("helloworld")),
          CoreMatchers.equalTo(givenValue));
    }
  }
}
