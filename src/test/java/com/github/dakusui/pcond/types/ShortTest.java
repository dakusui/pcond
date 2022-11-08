package com.github.dakusui.pcond.types;

import com.github.dakusui.shared.IllegalValueException;
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

  @Test(expected = IllegalValueException.class)
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

  @Test(expected = IllegalValueException.class)
  public void shortTransformerTestFail() {
    short v = 123;
    validate(
        v,
        when().asObject().asShort().then().lessThan((short) 122));
  }

  @Test(expected = IllegalValueException.class)
  public void toShortTest() {
    String v = "123";
    validate(
        v,
        when().asString().toShort(Short::parseShort).then().lessThan((short) 122));
  }
}
