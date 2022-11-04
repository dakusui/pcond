package com.github.dakusui.thincrest.ut.examples;

import com.github.dakusui.thincrest.examples.UTExample;
import com.github.dakusui.shared.utils.Metatest;
import com.github.dakusui.shared.utils.TestBase;
import org.junit.Test;

public class JUnitBasedExamplesTest extends TestBase {
  @Test
  public void testExampleUT() {
    Metatest.verifyTestClass(UTExample.class);
  }
}
