package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.Requires;
import com.github.dakusui.pcond.TestAssertions;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.pcond.utils.ut.TestBase;
import com.github.dakusui.pcond.validator.exceptions.PreconditionViolationException;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.Objects;

import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.core.refl.MethodQuery.instanceMethod;
import static com.github.dakusui.pcond.fluent.Fluents.assertAll;
import static com.github.dakusui.pcond.fluent.Fluents.statement;
import static com.github.dakusui.pcond.forms.Functions.parameter;
import static com.github.dakusui.pcond.forms.Predicates.*;

public class SummaryDetailTest extends TestBase {
  @Test
  public void givenLongString_whenCheckEqualnessWithSlightlyDifferentString_thenFailWithDetailsArePrinted$assertThat() {
    String actual = "helloHELLOhelloHELLOhelloXYZHELLOhelloHELLOhelloHELLO";
    String expected = "helloHELLOhelloHELLOhelloHELLOhelloHELLOhelloHELLO";
    try {
      TestAssertions.assertThat(actual, isEqualTo(expected));
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      assertThat(
          e,
          allOf(
              transform(Functions.<Throwable, String>call(instanceMethod(parameter(), "getActual"))).check(containsString(actual)),
              transform(Functions.<Throwable, String>call(instanceMethod(parameter(), "getExpected"))).check(containsString(expected))));
    }
  }

  @Test(expected = PreconditionViolationException.class)
  public void givenLongString_whenCheckEqualnessWithSlightlyDifferentString_thenFailWithDetailsArePrinted() {
    String actual = "helloHELLOhelloHELLOhelloXYZHELLOhelloHELLOhelloHELLO";
    String expected = "helloHELLOhelloHELLOhelloHELLOhelloHELLOhelloHELLO";
    try {
      Requires.require(actual, isEqualTo(expected));
    } catch (PreconditionViolationException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage(),
          allOf(
              containsString(actual),
              containsString("isEqualTo")));
      throw e;
    }
  }


  @Test(expected = PreconditionViolationException.class)
  public void givenLongString_whenCheckEqualnessUsingCustomPredicateWithSlightlyDifferentString_thenFailWithDetailsArePrinted() {
    String actual = "helloHELLOhelloHELLOhelloXYZHELLOhelloHELLOhelloHELLO";
    String expected = "helloHELLOhelloHELLOhelloHELLOhelloHELLOhelloHELLO";
    try {
      Requires.require(actual, Printables.predicate("customEquals", v -> Objects.equals(v, expected)));
    } catch (PreconditionViolationException e) {
      assertThat(
          e.getMessage(),
          allOf(
              containsString(actual),
              containsString("customEquals")));
      throw e;
    }
  }

  @Test
  public void givenLongString_whenCheckEqualnessUsingCustomPredicateWithSlightlyDifferentString_thenExpectationSpecificFragmentsContainedOnlyByExplanationForExpectationAndActualValuesAreAlsoSo() {
    String actualValue = "HelloWorld, everyone, -----------------------------------------,  hi, there!";
    String expected = "EXPECTED:" + actualValue + ":EXPECTED";
    try {
      assertThat(actualValue, allOf(
          Predicates.isNotNull(),
          Predicates.isNull(),
          Predicates.containsString("XYZ"),
          Predicates.equalTo(actualValue),
          Predicates.equalTo(expected)
      ));
    } catch (ComparisonFailure e) {
      assertAll(
          statement(
              e.getExpected(),
              allOf(
                  containsString("isNull"),
                  containsString("containsString[\"XYZ\"]"),
                  containsString(expected),
                  not(matchesRegex("\n" + actualValue + "\n"))
              )),
          statement(
              e.getActual(),
              allOf(
                  not(containsString(expected)),
                  containsString(actualValue))));
    }
  }
}
