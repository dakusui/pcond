package com.github.dakusui.pcond.examples;

import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.List;

import static com.github.dakusui.pcond.Fluents.*;
import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.forms.Predicates.allOf;
import static java.util.Arrays.asList;

@SuppressWarnings("NewClassNamingConvention")
public class MultiValueAssertion {
  @Test(expected = ComparisonFailure.class)
  public void test() {
    List<?> givenValues = asList(123, asList("Hello", "World"));

    assertThat(
        givenValues,
        allOf(
            when().asListOfClass(Object.class).elementAt(0)
                .then().asInteger()
                .equalTo(122)
                .verify(),
            when().asListOfClass(Object.class).elementAt(1)
                .tee(
                    $().asListOfClass(Object.class).elementAt(0).asString().then().isEqualTo("hello").verify(),
                    $().asListOfClass(Object.class).elementAt(0).asString().then().isEqualTo("world").verify()).verify()));
  }
}
