package com.github.dakusui.pcond.types;

import com.github.dakusui.shared.IllegalValueException;
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

  @Test(expected = IllegalValueException.class)
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

  @Test(expected = IllegalValueException.class)
  public void floatTransformerTestFail() {
    float v = 1.23f;
    validate(
        v,
        when().asObject().asFloat().then().lessThan(1.22f));
  }

  @Test(expected = IllegalValueException.class)
  public void toFloatTest() {
    String v = "123";
    validate(
        v,
        when().asString().toFloat(Float::parseFloat).then().lessThan(122.0f));
  }
}
