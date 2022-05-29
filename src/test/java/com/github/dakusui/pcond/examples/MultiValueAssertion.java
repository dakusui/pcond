package com.github.dakusui.pcond.examples;

import org.junit.Test;

import static com.github.dakusui.pcond.Fluents.*;
import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.forms.Predicates.allOf;
import static java.util.Arrays.asList;

@SuppressWarnings("NewClassNamingConvention")
public class MultiValueAssertion {
  @Test//(expected = ComparisonFailure.class)
  public void test() {
    assertThat(
        list(123, list("Hello", "world")),
        allOf(
            when().at(0).asInteger()
                .then().equalTo(122),
            when().at(1).asListOfClass(String.class).thenWithAllOf(asList(
                value().at(0).asString()
                    .then().isEqualTo("hello"),
                value().at(1).asString()
                    .then().isEqualTo("world")))));
  }
}
