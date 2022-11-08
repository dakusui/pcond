package com.github.dakusui.pcond.types;

import com.github.dakusui.pcond.internals.InternalException;
import com.github.dakusui.shared.IllegalValueException;
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

  @Test(expected = IllegalValueException.class)
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

  @Test(expected = IllegalValueException.class)
  public void longTransformerTestFail() {
    long v = 123_000_000_000L;
    validate(
        v,
        when().asObject().asLong().then().lessThan(122_000_000_000L));
  }

  @Test(expected = IllegalValueException.class)
  public void toLongTest() {
    String v = "123";
    validate(
        v,
        when().asString().toLong(Long::parseLong).then().lessThan((long) 122));
  }

  @Test(expected = InternalException.class)
  public void longTypeMismatch() {
    long v = 123L;
    validate(
        v,
        when()
            .asObject()
            .asLong()
            .asDouble()
            .then()
            .isNotNull());
  }
}
