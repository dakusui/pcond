package com.github.dakusui.ut.valid8j.ut.styles;

import com.github.dakusui.pcond.validator.exceptions.PostconditionViolationException;
import com.github.dakusui.pcond.validator.exceptions.PreconditionViolationException;
import com.github.dakusui.shared.utils.ut.TestBase;
import com.github.dakusui.valid8j.ValidationFluents;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static com.github.dakusui.pcond.fluent.Fluents.stringValue;
import static com.github.dakusui.pcond.forms.Predicates.containsString;
import static com.github.dakusui.pcond.forms.Predicates.not;
import static com.github.dakusui.shared.utils.TestUtils.stringToLowerCase;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Temporarily commented out for improving new fluent model.
 */
@RunWith(Enclosed.class)
public class FluentStyleDbCTest {
  public static class ForRequiresTest extends TestBase {
    @Test(expected = IllegalArgumentException.class)
    public void requireArgumentsTest_failing() {
      try {
        ValidationFluents.requireArguments(
            stringValue("hello").toUpperCase().then().isEqualTo("HELLO"),
            stringValue("world").toLowerCase().then().contains("WORLD").checkWithPredicate(not(containsString("w"))));
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        assertThat(message, CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        assertThat(message, CoreMatchers.containsString("containsString[WORLD]->false"));
        throw e;
      }
    }

    @Test(expected = IllegalStateException.class)
    public void requireStatesTest_failing() {
      try {
        ValidationFluents.requireStates(
            stringValue("hello").toUpperCase().then().isEqualTo("HELLO"),
            stringValue("world").toLowerCase().then().contains("WORLD").checkWithPredicate(not(containsString("w"))));
      } catch (IllegalStateException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        assertThat(message, CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        assertThat(message, CoreMatchers.containsString("containsString[WORLD]->false"));
        throw e;
      }
    }


    @Test(expected = PreconditionViolationException.class)
    public void requireValuesTest_failing() {
      try {
        ValidationFluents.requireAll(
            stringValue("hello").toUpperCase().then().isEqualTo("HELLO"),
            stringValue("world").toLowerCase().then().contains("WORLD").checkWithPredicate(not(containsString("w"))));
      } catch (PreconditionViolationException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        assertThat(message, CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        assertThat(message, CoreMatchers.containsString("containsString[WORLD]->false"));
        throw e;
      }
    }

    @Test
    public void requireArgument_passing() {
      String givenValue = "helloWorld";
      ValidationFluents.requireArgument(stringValue(givenValue)
          .toString(stringToLowerCase())
          .then()
          .isEqualTo("helloworld"));
    }


    @Test
    public void requireValue_passing() {
      String givenValue = "helloWorld";
      assertThat(
          ValidationFluents.requireStatement(stringValue(givenValue)
              .toObject(stringToLowerCase())
              .asString()
              .then()
              .isEqualTo("helloworld")),
          Matchers.equalTo(givenValue));
    }

    @Test
    public void reqireState_passing() {
      String givenValue = "helloWorld";
      assertThat(
          ValidationFluents.requireState(stringValue(givenValue)
              .toString(stringToLowerCase())
              .then()
              .isEqualTo("helloworld")),
          Matchers.equalTo(givenValue));
    }
  }

  public static class ForEnsuresTest extends TestBase {
    @Test(expected = PostconditionViolationException.class)
    public void ensureValuesTest_failing() {
      try {
        ValidationFluents.ensureAll(
            stringValue("hello").toUpperCase().then().isEqualTo("HELLO"),
            stringValue("world").toLowerCase().then().contains("WORLD").checkWithPredicate(not(containsString("w"))));
      } catch (PostconditionViolationException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        assertThat(message, CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        assertThat(message, CoreMatchers.containsString("containsString[WORLD]->false"));
        throw e;
      }
    }

    @Test(expected = IllegalStateException.class)
    public void ensureStatesTest_failing() {
      try {
        ValidationFluents.ensureStates(
            stringValue("hello").toUpperCase().then().isEqualTo("HELLO"),
            stringValue("world").toLowerCase().then().contains("WORLD").checkWithPredicate(not(containsString("w"))));
      } catch (IllegalStateException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        assertThat(message, CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        assertThat(message, CoreMatchers.containsString("containsString[WORLD]->false"));
        throw e;
      }
    }

    @Test
    public void ensureValue_passing() {
      String givenValue = "helloWorld";
      assertThat(
          ValidationFluents.ensureStatement(stringValue(givenValue)
              .toString(stringToLowerCase())
              .then()
              .isEqualTo("helloworld")),
          CoreMatchers.equalTo(givenValue));
    }


    @Test
    public void ensureState_passing() {
      String givenValue = "helloWorld";
      assertThat(
          ValidationFluents.ensureState(stringValue(givenValue)
              .toString(stringToLowerCase())
              .then()
              .isEqualTo("helloworld")),
          CoreMatchers.equalTo(givenValue));
    }


    @Test
    public void test_that() {
      assertThat(ValidationFluents.that(stringValue("").then().isNotNull()), CoreMatchers.is(true));
    }

    @Test
    public void test_all() {
      assertThat(
          ValidationFluents.all(stringValue("").then().isNotNull(), stringValue("").then().isNotNull()),
          CoreMatchers.is(true));
    }

    @Test
    public void test_precondition() {
      assertThat(ValidationFluents.precondition(stringValue("").then().isNotNull()), CoreMatchers.is(true));
    }

    @Test
    public void test_preconditions() {
      assertThat(
          ValidationFluents.preconditions(stringValue("").then().isNotNull(), stringValue("").then().isNotNull()),
          CoreMatchers.is(true));
    }

    @Test
    public void test_postcondition() {
      assertThat(ValidationFluents.postcondition(stringValue("").then().isNotNull()), CoreMatchers.is(true));
    }

    @Test
    public void test_postconditions() {
      assertThat(
          ValidationFluents.postconditions(stringValue("").then().isNotNull(), stringValue("").then().isNotNull()),
          CoreMatchers.is(true));
    }
  }
}
