package com.github.dakusui.thincrest.ut.types;

import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.github.dakusui.thincrest.ut.FluentsInternalTest.Utils.when;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;

public class IntegerTest {
  @Test
  public void intTest() {
    int v = 123_000_000;
    assertThat(v, when().asInteger().then().lessThan(124_000_000));
  }

  @Test(expected = ComparisonFailure.class)
  public void intTestFail() {
    int v = 123_000_000;
    assertThat(v, when().asInteger().then().lessThan(122_000_000));
  }

  @Test
  public void intTransformerTest() {
    int v = 123_000_000;
    assertThat(v, when().asObject().asInteger().then().lessThan(124_000_000));
  }

  @Test(expected = ComparisonFailure.class)
  public void intTransformerTestFail() {
    int v = 123_000_000;
    assertThat(v, when().asObject().asInteger().then().lessThan(122_000_000));
  }
}
