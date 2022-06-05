package com.github.dakusui.pcond.ut.types;

import org.junit.Test;

import static com.github.dakusui.pcond.Fluents.when;
import static com.github.dakusui.pcond.TestAssertions.assertThat;

public class DoubleTest {
  @Test
  public void doubleTest() {
    double v = 1.23d;
    assertThat(
        v,
        when().asDouble().then().lessThan(1.24d)
    );
  }

  @Test
  public void doubleTestFail() {
    double v = 1.23d;
    assertThat(
        v,
        when().asDouble().then().lessThan(1.22d)
    );
  }

}
