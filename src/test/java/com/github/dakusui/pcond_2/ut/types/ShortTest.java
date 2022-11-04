package com.github.dakusui.pcond_2.ut.types;

import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.github.dakusui.thincrest.ut.FluentsInternalTest.Utils.when;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;

public class ShortTest {
  @Test
  public void shortTest() {
    short v = 123;
    assertThat(
        v,
        when().asShort().then().lessThan((short)124)
    );
  }

  @Test(expected = ComparisonFailure.class)
  public void shortTestFail() {
    short v = 123;
    assertThat(
        v,
        when().asShort().then().lessThan((short)122)
    );
  }

  @Test
  public void shortTransformerTest() {
    short v = 123;
    assertThat(
        v,
        when().asObject().asShort().then().lessThan((short)124)
    );
  }

  @Test(expected = ComparisonFailure.class)
  public void shortTransformerTestFail() {
    short v = 123;
    assertThat(
        v,
        when().asObject().asShort().then().lessThan((short)122)
    );
  }
}
