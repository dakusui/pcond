package com.github.dakusui.thincrest.ut;

import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.pcond.utils.ut.TestBase;
import com.github.dakusui.thincrest.TestAssertions;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.Objects;

import static com.github.dakusui.pcond.core.refl.MethodQuery.instanceMethod;
import static com.github.dakusui.pcond.fluent.Fluents.statement;
import static com.github.dakusui.pcond.forms.Functions.parameter;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;
import static com.github.dakusui.thincrest.TestFluents.assertAll;

public class SummaryDetailTest extends TestBase {
  @Test
  public void givenLongString_whenCheckEqualnessWithSlightlyDifferentString_thenFailWithDetailsArePrinted$assertThat() {
    String actual = "helloHELLOhelloHELLOhelloXYZHELLOhelloHELLOhelloHELLO";
    String expected = "helloHELLOhelloHELLOhelloHELLOhelloHELLOhelloHELLO";
    try {
      TestAssertions.assertThat(actual, isEqualTo(expected));
      throw new Error(); // Make it fail if PC reaches here.
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      assertThat(
          e,
          allOf(
              transform(Functions.<Throwable, String>call(instanceMethod(parameter(), "getActual"))).check(containsString(actual)),
              transform(Functions.<Throwable, String>call(instanceMethod(parameter(), "getExpected"))).check(containsString(expected))));
    }
  }

  @Test
  public void givenLongString_whenCheckEqualnessWithSlightlyDifferentString_thenFailWithDetailsArePrinted() {
    String actual = "helloHELLOhelloHELLOhelloXYZHELLOhelloHELLOhelloHELLO";
    String expected = "helloHELLOhelloHELLOhelloHELLOhelloHELLOhelloHELLO";
    try {
      TestAssertions.assertThat(actual, isEqualTo(expected));
      throw new Error(); // Make it fail if PC reaches here.
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      assertAll(
          statement(
              e.getExpected(),
              allOf(
                  containsString(expected),
                  containsString("isEqualTo"))),
          statement(
              e.getActual(),
              containsString(actual)));
    }
  }


  @Test
  public void givenLongString_whenCheckEqualnessUsingCustomPredicateWithSlightlyDifferentString_thenFailWithDetailsArePrinted() {
    String actual = "helloHELLOhelloHELLOhelloXYZHELLOhelloHELLOhelloHELLO";
    String expected = "helloHELLOhelloHELLOhelloHELLOhelloHELLOhelloHELLO";
    try {
      TestAssertions.assertThat(actual, Printables.predicate("customEquals", v -> Objects.equals(v, expected)));
      throw new Error(); // Make it fail if PC reaches here.
    } catch (ComparisonFailure e) {
      assertThat(
          e.getMessage(),
          allOf(
              containsString(actual),
              containsString("customEquals")));
    }
  }

  @Test
  public void givenLongString_whenCheckEqualnessUsingCustomPredicateWithSlightlyDifferentString_thenExpectationSpecificFragmentsContainedOnlyByExplanationForExpectationAndActualValuesAreAlsoSo() {
    String actualValue = "HelloWorld, everyone, -----------------------------------------,  hi, there!";
    String expected = "EXPECTED:" + actualValue + ":EXPECTED";
    try {
      assertThat(actualValue, allOf(
          isNotNull(),
          isNull(),
          containsString("XYZ"),
          equalTo(actualValue),
          equalTo(expected)
      ));
      throw new Error(); // Make it fail if PC reaches here.
    } catch (ComparisonFailure e) {
      e.printStackTrace();
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
