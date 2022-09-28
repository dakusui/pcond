package com.github.dakusui.pcond.ut.types;

import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.github.dakusui.pcond.ut.FluentsInternalTest.Utils.when;
import static com.github.dakusui.pcond.TestAssertions.assertThat;

public class FloatTest {
  @Test
  public void floatTest() {
    float v = 1.23f;
    assertThat(
        v,
        when().asFloat().then().lessThan(1.24f)
    );
  }

  @Test(expected = ComparisonFailure.class)
  public void floatTestFail() {
    float v = 1.23f;
    assertThat(
        v,
        when().asFloat().then().lessThan(1.22f)
    );
  }

  @Test
  public void floatTransformerTest() {
    float v = 1.23f;
    assertThat(v, when().asObject().asFloat().then().lessThan(1.24f));
  }

  @Test(expected = ComparisonFailure.class)
  public void floatTransformerTestFail() {
    float v = 1.23f;
    assertThat(v, when().asObject().asFloat().then().lessThan(1.22f));
  }
}
