package com.github.dakusui.pcondtest.types;

import com.github.dakusui.shared.TestUtils;
import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.github.dakusui.shared.FluentTestUtils.when;
import static com.github.dakusui.shared.TestUtils.validate;

public class LongTest {
  @Test
  public void longTest() {
    long v = 123_000_000_000L;
    validate(
        v,
        when().asLong().then().lessThan(124_000_000_000L));
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void longTestFail() {
    long v = 123_000_000_000L;
    validate(
        v,
        when().asLong().then().lessThan(122_000_000_000L));
  }

  @Test
  public void longTransformerTest() {
    long v = 123_000_000_000L;
    validate(
        v,
        when().asObject().asLong().then().lessThan(124_000_000_000L));
  }

  @Test(expected = ComparisonFailure.class)
  public void longTransformerTestFail() {
    long v = 123_000_000_000L;
    validate(
        v,
        when().asObject().asLong().then().lessThan(122_000_000_000L));
  }
}
