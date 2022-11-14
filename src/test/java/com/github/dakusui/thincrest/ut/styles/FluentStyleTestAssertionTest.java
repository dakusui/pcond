package com.github.dakusui.thincrest.ut.styles;

import com.github.dakusui.thincrest.TestFluents;
import com.github.dakusui.pcond.fluent.Fluents;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.shared.utils.ut.TestBase;
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
import static com.github.dakusui.shared.utils.TestUtils.stringToLowerCase;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@RunWith(Enclosed.class)
public class FluentStyleTestAssertionTest {

  public static class ForTestAssertionsTest extends TestBase {
    @Test(expected = ComparisonFailure.class)
    public void expectingDifferentException_testFailing() {
      try {
        String givenValue = "helloWorld";
        TestFluents.assertStatement(stringStatement(givenValue)
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

    @SuppressWarnings("StringOperationCanBeSimplified")
    @Test(expected = ComparisonFailure.class)
    public void expectingExceptionButNotThrown_testFailing() {
      try {
        String givenValue = "helloWorld";
        TestFluents.assertStatement(stringStatement(givenValue)
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
      TestFluents.assertStatement(stringStatement(givenValue)
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
        TestFluents.assertStatement(Fluents.stringStatement(givenValue)
            .exercise(stringToLowerCase())
            .then()
            .asString()
            .isEqualTo("HELLOWORLD"));
      } catch (ComparisonFailure e) {
        MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("Value:\"helloWorld\" violated: WHEN(stringToLowerCase->castTo[String] stringIsEqualTo[HELLOWORLD]) "));
        throw e;
      }
    }

    @Test
    public void string_assertThatTest_passed() {
      String givenValue = "helloWorld";
      TestFluents.assertStatement(stringStatement(givenValue)
          .exercise(stringToLowerCase())
          .then()
          .asString()
          .isEqualTo("helloworld"));
    }

    @Test
    public void string_assertThat_useValue_passed() {
      String givenValue = "helloWorld";
      TestFluents.assertStatement(stringStatement(givenValue)
          .exercise(stringToLowerCase())
          .then()
          .asString()
          .isEqualTo("helloworld"));
    }

    @Test
    public void int_assertThatTest_passed() {
      int givenValue = 1234;
      TestFluents.assertStatement(Fluents.integerStatement(givenValue)
          .then()
          .isEqualTo(1234));
    }

    @Test
    public void boolean_assertThatTest_passed() {
      boolean givenValue = true;
      TestFluents.assertStatement(Fluents.booleanStatement(givenValue)
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
      TestFluents.assertStatement(Fluents.objectStatement(givenValue)
          .then()
          .toStringWith(Object::toString)
          .isEqualTo("OBJECT"));
    }

    @Test
    public void list_assertThatTest_passed() {
      List<String> givenValue = asList("hello", "world");
      TestFluents.assertStatement(listStatement(givenValue)
          .then()
          .isEqualTo(asList("hello", "world")));
    }

    @Test
    public void stream_assertThatTest_passed() {
      Stream<String> givenValue = Stream.of("hello", "world");
      TestFluents.assertStatement(Fluents.streamStatement(givenValue)
          .then()
          .toListWith(v -> v.collect(toList()))
          .isEqualTo(asList("hello", "world")));
    }

    @Test(expected = ComparisonFailure.class)
    public void multiAssertAll_failed() {
      try {
        TestFluents.assertAll(
            stringStatement("hello").toUpperCase().then().isEqualTo("HELLO"),
            stringStatement("world").toLowerCase().then().contains("WORLD"));
      } catch (ComparisonFailure e) {
        e.printStackTrace();
        System.err.println("actual:" + e.getActual());
        MatcherAssert.assertThat(e.getActual().replaceAll(" +", ""), CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        MatcherAssert.assertThat(e.getActual().replaceAll(" +", ""), CoreMatchers.containsString("containsString[WORLD]->false"));
        System.err.println("expected:" + e.getExpected());
        MatcherAssert.assertThat(e.getExpected().replaceAll(" +", ""), CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        MatcherAssert.assertThat(e.getExpected().replaceAll(" +", ""), CoreMatchers.containsString("containsString[WORLD]->true"));
        throw e;
      }
    }

    @Test
    public void assertAll_passed() {
      TestFluents.assertAll(
          stringStatement("hello").toUpperCase().then().isEqualTo("HELLO"),
          stringStatement("world").toLowerCase().then().contains("world"));
    }

    @Test(expected = AssumptionViolatedException.class)
    public void assumeThatTest_failed() {
      String givenValue = "helloWorld";
      try {
        TestFluents.assumeStatement(stringStatement(givenValue)
            .exercise(stringToLowerCase())
            .then()
            .asString()
            .isEqualTo("HELLOWORLD"));
      } catch (AssumptionViolatedException e) {
        e.printStackTrace();
        MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("Value:\"helloWorld\" violated: WHEN(stringToLowerCase->castTo[String] stringIsEqualTo[HELLOWORLD])"));
        throw e;
      }
    }

    @Test
    public void assumeThatTest_passed() {
      String givenValue = "helloWorld";
      TestFluents.assumeStatement(stringStatement(givenValue)
          .exercise(stringToLowerCase())
          .then()
          .asString()
          .isEqualTo("helloworld"));
    }

    @Test(expected = AssumptionViolatedException.class)
    public void multiAssumeAll_failed() {
      try {
        TestFluents.assumeAll(
            stringStatement("hello").toUpperCase().then().isEqualTo("HELLO"),
            stringStatement("world").toLowerCase().then().contains("WORLD"));
      } catch (AssumptionViolatedException e) {
        e.printStackTrace();
        MatcherAssert.assertThat(e.getMessage().replaceAll(" +", ""), CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        MatcherAssert.assertThat(e.getMessage().replaceAll(" +", ""), CoreMatchers.containsString("containsString[WORLD]->false"));
        throw e;
      }
    }

    @Test
    public void multiAssumeAll_passed() {
      TestFluents.assumeAll(
          stringStatement("hello").toUpperCase().then().isEqualTo("HELLO"),
          stringStatement("world").toLowerCase().then().contains("world"));
    }

    @Test
    public void test_valueMethod() {
      MatcherAssert.assertThat(Fluents.value(), CoreMatchers.equalTo(null));
    }
  }

}
