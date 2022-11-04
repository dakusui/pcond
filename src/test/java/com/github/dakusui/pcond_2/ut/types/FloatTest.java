package com.github.dakusui.pcond_2.ut.types;

import com.github.dakusui.shared.TestUtils;
import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.github.dakusui.shared.FluentTestUtils.when;
import static com.github.dakusui.shared.TestUtils.validate;

public class FloatTest {
  @Test
  public void floatTest() {
    float v = 1.23f;
    validate(
        v,
        when().asFloat().then().lessThan(1.24f));
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void floatTestFail() {
    float v = 1.23f;
    validate(
        v,
        when().asFloat().then().lessThan(1.22f));
  }

  @Test
  public void floatTransformerTest() {
    float v = 1.23f;
    validate(v, when().asObject().asFloat().then().lessThan(1.24f));
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void floatTransformerTestFail() {
    float v = 1.23f;
    validate(
        v,
        when().asObject().asFloat().then().lessThan(1.22f));
  }
}
