package com.github.dakusui.pcond.examples;

import com.github.dakusui.pcond.ut.FluentsInternalTest;
import org.junit.Test;

import static com.github.dakusui.pcond.fluent.FluentsInternal.*;
import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.forms.Predicates.allOf;
import static java.util.Arrays.asList;

@SuppressWarnings("NewClassNamingConvention")
public class MultiValueAssertion {
  @Test//(expected = ComparisonFailure.class)
  public void test() {
    assertThat(
        FluentsInternalTest.Utils.list(123, FluentsInternalTest.Utils.list("Hello", "world")),
        allOf(
            FluentsInternalTest.Utils.when().at(0).asInteger()
                .then().equalTo(122),
            FluentsInternalTest.Utils.when().at(1).asListOfClass(String.class).thenVerifyWithAllOf(asList(
                $().at(0).asString()
                    .then().isEqualTo("hello"),
                $().at(1).asString()
                    .then().isEqualTo("world")))));
  }
}
