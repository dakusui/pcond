package com.github.dakusui.pcond.examples;

import org.junit.Test;

import static com.github.dakusui.pcond.Fluents.*;
import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.forms.Predicates.allOf;

@SuppressWarnings("NewClassNamingConvention")
public class MultiValueAssertion {
  @Test//(expected = ComparisonFailure.class)
  public void test() {
    assertThat(
        list(123, list("Hello", "world")),
        allOf(
            when().valueAt(0)
                .then().asInteger()
                .equalTo(122),
            when().valueAt(1)
                .allOf(
                    $().valueAt(0).asString()
                        .then()
                        .isEqualTo("hello"),
                    $().valueAt(1).asString()
                        .then()
                        .isEqualTo("world"))));
  }
}
