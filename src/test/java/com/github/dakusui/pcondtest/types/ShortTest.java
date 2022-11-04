package com.github.dakusui.pcondtest.types;

import com.github.dakusui.shared.TestUtils;
import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.github.dakusui.shared.FluentTestUtils.when;
import static com.github.dakusui.shared.TestUtils.validate;

public class ShortTest {
  @Test
  public void shortTest() {
    short v = 123;
    validate(
        v,
        when().asShort().then().lessThan((short) 124));
  }

  @Test(expected = ComparisonFailure.class)
  public void shortTestFail() {
    short v = 123;
    validate(
        v,
        when().asShort().then().lessThan((short) 122));
  }

  @Test
  public void shortTransformerTest() {
    short v = 123;
    validate(
        v,
        when().asObject().asShort().then().lessThan((short) 124));
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void shortTransformerTestFail() {
    short v = 123;
    validate(
        v,
        when().asObject().asShort().then().lessThan((short) 122));
  }
}
