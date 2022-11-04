package com.github.dakusui.pcond.types;

import com.github.dakusui.shared.TestUtils;
import org.junit.Test;

import static com.github.dakusui.shared.FluentTestUtils.when;
import static com.github.dakusui.shared.TestUtils.validate;

public class DoubleTest {
  @Test
  public void doubleTest() {
    double v = 1.23d;
    validate(
        v,
        when().asDouble().then().lessThan(1.24d));
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void doubleTestFail() {
    double v = 1.23d;
    validate(
        v,
        when().asDouble().then().lessThan(1.22d));
  }

  @Test
  public void doubleTransformerTest() {
    double v = 1.23d;
    validate(
        v,
        when().asObject().asDouble().then().lessThan(1.24d));
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void doubleTransformerTestFail() {
    double v = 1.23d;
    validate(
        v,
        when().asObject().asDouble().then().lessThan(1.22d));
  }
}
