package com.github.dakusui.thincrest.ut.types;

import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.github.dakusui.thincrest.ut.FluentsInternalTest.Utils.when;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;

public class LongTest {
  @Test
  public void longTest() {
    long v = 123_000_000_000L;
    assertThat(
        v,
        when().asLong().then().lessThan(124_000_000_000L)
    );
  }

  @Test(expected = ComparisonFailure.class)
  public void longTestFail() {
    long v = 123_000_000_000L;
    assertThat(
        v,
        when().asLong().then().lessThan(122_000_000_000L)
    );
  }

  @Test
  public void longTransformerTest() {
    long v = 123_000_000_000L;
    assertThat(
        v,
        when().asObject().asLong().then().lessThan(124_000_000_000L)
    );
  }

  @Test(expected = ComparisonFailure.class)
  public void longTransformerTestFail() {
    long v = 123_000_000_000L;
    assertThat(
        v,
        when().asObject().asLong().then().lessThan(122_000_000_000L)
    );
  }
}
