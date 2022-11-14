package com.github.dakusui.valid8j.ut.styles;

import com.github.dakusui.pcond.fluent.Fluents;
import com.github.dakusui.shared.utils.ut.TestBase;
import com.github.dakusui.pcond.validator.exceptions.PostconditionViolationException;
import com.github.dakusui.pcond.validator.exceptions.PreconditionViolationException;
import com.github.dakusui.valid8j.ValidationFluents;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static com.github.dakusui.pcond.forms.Predicates.containsString;
import static com.github.dakusui.pcond.forms.Predicates.not;
import static com.github.dakusui.shared.utils.TestUtils.stringToLowerCase;

@RunWith(Enclosed.class)
public class FluentStyleDbCTest {
  public static class ForRequiresTest extends TestBase {
    @Test(expected = IllegalArgumentException.class)
    public void requireArgumentsTest_failing() {
      try {
        ValidationFluents.requireArguments(
            Fluents.stringStatement("hello").toUpperCase().then().isEqualTo("HELLO"),
            Fluents.stringStatement("world").toLowerCase().then().contains("WORLD").verify(not(containsString("w"))));
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        MatcherAssert.assertThat(message, CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        MatcherAssert.assertThat(message, CoreMatchers.containsString("containsString[WORLD]->false"));
        throw e;
      }
    }

    @Test(expected = IllegalStateException.class)
    public void requireStatesTest_failing() {
      try {
        ValidationFluents.requireStates(
            Fluents.stringStatement("hello").toUpperCase().then().isEqualTo("HELLO"),
            Fluents.stringStatement("world").toLowerCase().then().contains("WORLD").verify(not(containsString("w"))));
      } catch (IllegalStateException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        MatcherAssert.assertThat(message, CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        MatcherAssert.assertThat(message, CoreMatchers.containsString("containsString[WORLD]->false"));
        throw e;
      }


    }

    @Test(expected = PreconditionViolationException.class)
    public void requireValuesTest_failing() {
      try {
        ValidationFluents.requireAll(
            Fluents.stringStatement("hello").toUpperCase().then().isEqualTo("HELLO"),
            Fluents.stringStatement("world").toLowerCase().then().contains("WORLD").verify(not(containsString("w"))));
      } catch (PreconditionViolationException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        MatcherAssert.assertThat(message, CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        MatcherAssert.assertThat(message, CoreMatchers.containsString("containsString[WORLD]->false"));
        throw e;
      }
    }

    @Test
    public void requireArgument_passing() {
      String givenValue = "helloWorld";
      ValidationFluents.requireArgument(Fluents.stringStatement(givenValue)
          .exercise(stringToLowerCase())
          .then()
          .asString()
          .isEqualTo("helloworld"));
    }

    @Test
    public void requireValue_passing() {
      String givenValue = "helloWorld";
      MatcherAssert.assertThat(
          ValidationFluents.requireStatement(Fluents.stringStatement(givenValue)
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
          ValidationFluents.requireState(Fluents.stringStatement(givenValue)
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
        ValidationFluents.ensureAll(
            Fluents.stringStatement("hello").toUpperCase().then().isEqualTo("HELLO"),
            Fluents.stringStatement("world").toLowerCase().then().contains("WORLD").verify(not(containsString("w"))));
      } catch (PostconditionViolationException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        MatcherAssert.assertThat(message, CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        MatcherAssert.assertThat(message, CoreMatchers.containsString("containsString[WORLD]->false"));
        throw e;
      }
    }

    @Test(expected = IllegalStateException.class)
    public void ensureStatesTest_failing() {
      try {
        ValidationFluents.ensureStates(
            Fluents.stringStatement("hello").toUpperCase().then().isEqualTo("HELLO"),
            Fluents.stringStatement("world").toLowerCase().then().contains("WORLD").verify(not(containsString("w"))));
      } catch (IllegalStateException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", "");
        MatcherAssert.assertThat(message, CoreMatchers.containsString("stringIsEqualTo[HELLO]->true"));
        MatcherAssert.assertThat(message, CoreMatchers.containsString("containsString[WORLD]->false"));
        throw e;
      }
    }

    @Test
    public void ensureValue_passing() {
      String givenValue = "helloWorld";
      MatcherAssert.assertThat(
          ValidationFluents.ensureStatement(Fluents.stringStatement(givenValue)
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
          ValidationFluents.ensureState(Fluents.stringStatement(givenValue)
              .exercise(stringToLowerCase())
              .then()
              .asString()
              .isEqualTo("helloworld")),
          CoreMatchers.equalTo(givenValue));
    }
  }
}
