package com.github.dakusui.thincrest.examples;

import com.github.dakusui.shared.FluentTestUtils;
import org.junit.Test;

import static com.github.dakusui.pcond.fluent.FluentsInternal.*;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;
import static com.github.dakusui.pcond.forms.Predicates.allOf;
import static java.util.Arrays.asList;

@SuppressWarnings("NewClassNamingConvention")
public class MultiValueAssertion {
  @Test//(expected = ComparisonFailure.class)
  public void test() {
    assertThat(
        FluentTestUtils.list(123, FluentTestUtils.list("Hello", "world")),
        allOf(
            FluentTestUtils.when().at(0).asInteger()
                .then().equalTo(122),
            FluentTestUtils.when().at(1).asListOfClass(String.class).thenVerifyWithAllOf(asList(
                $().at(0).asString()
                    .then().isEqualTo("hello"),
                $().at(1).asString()
                    .then().isEqualTo("world")))));
  }
}
