package com.github.dakusui.ut.thincrest.examples;

import com.github.dakusui.shared.utils.Metatest;
import com.github.dakusui.shared.utils.TestBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JUnitBasedExamplesTest extends TestBase {
  @Test
  public void testExampleUT() {
    Metatest.verifyTestClass(UTExample.class);
  }

  @Test
  public void testJUnit() {
    String expectation="Hello";
    assertEquals(expectation, "HI");
  }
}
