package com.github.dakusui.pcond.types;

import com.github.dakusui.pcond.fluent.Fluents;
import com.github.dakusui.shared.IllegalValueException;
import org.junit.Test;

import static com.github.dakusui.shared.FluentTestUtils.when;
import static com.github.dakusui.shared.TestUtils.validate;
import static com.github.dakusui.shared.TestUtils.validateStatement;

public class FloatTest {
  @Test
  public void floatTest() {
    float v = 1.23f;
    validateStatement(Fluents.floatStatement(v).then().lessThan(1.24f));
  }

  @Test(expected = IllegalValueException.class)
  public void floatTestFail() {
    float v = 1.23f;
    validateStatement(
        Fluents.floatStatement(v).then().lessThan(1.22f));
  }

  @Test
  public void floatTransformerTest() {
    float v = 1.23f;
    validateStatement(
        Fluents.floatStatement(v).then().lessThan(1.24f));
  }

  @Test(expected = IllegalValueException.class)
  public void floatTransformerTestFail() {
    float v = 1.23f;
    validateStatement(
        Fluents.floatStatement(v).then().lessThan(1.22f));
  }

  @Test(expected = IllegalValueException.class)
  public void toFloatTest() {
    String v = "123";
    validateStatement(
        Fluents.stringStatement(v)
            .toFloat(Float::parseFloat)
            .then()
            .lessThan(122.0f));
  }
}
