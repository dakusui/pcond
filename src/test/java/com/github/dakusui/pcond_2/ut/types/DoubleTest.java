package com.github.dakusui.pcond_2.ut.types;

import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.github.dakusui.thincrest.ut.FluentsInternalTest.Utils.when;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;

public class DoubleTest {
  @Test
  public void doubleTest() {
    double v = 1.23d;
    assertThat(
        v,
        when().asDouble().then().lessThan(1.24d)
    );
  }

  @Test(expected = ComparisonFailure.class)
  public void doubleTestFail() {
    double v = 1.23d;
    assertThat(
        v,
        when().asDouble().then().lessThan(1.22d)
    );
  }

  @Test
  public void doubleTransformerTest() {
    double v = 1.23d;
    assertThat(
        v,
        when().asObject().asDouble().then().lessThan(1.24d)
    );
  }

  @Test(expected = ComparisonFailure.class)
  public void doubleTransformerTestFail() {
    double v = 1.23d;
    assertThat(
        v,
        when().asObject().asDouble().then().lessThan(1.22d)
    );
  }
}
