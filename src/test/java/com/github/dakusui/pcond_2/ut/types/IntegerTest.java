package com.github.dakusui.pcond_2.ut.types;

import com.github.dakusui.shared.TestUtils;
import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.github.dakusui.shared.FluentTestUtils.when;
import static com.github.dakusui.shared.TestUtils.validate;

public class IntegerTest {
  @Test
  public void intTest() {
    int v = 123_000_000;
    validate(v, when().asInteger().then().lessThan(124_000_000));
  }

  @Test(expected = ComparisonFailure.class)
  public void intTestFail() {
    int v = 123_000_000;
    validate(v, when().asInteger().then().lessThan(122_000_000));
  }

  @Test
  public void intTransformerTest() {
    int v = 123_000_000;
    validate(v, when().asObject().asInteger().then().lessThan(124_000_000));
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void intTransformerTestFail() {
    int v = 123_000_000;
    validate(v, when().asObject().asInteger().then().lessThan(122_000_000));
  }
}
