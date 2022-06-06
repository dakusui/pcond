package com.github.dakusui.pcond.ut.types;

import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.github.dakusui.pcond.Fluents.when;
import static com.github.dakusui.pcond.TestAssertions.assertThat;

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
