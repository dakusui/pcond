package com.github.dakusui.pcond.ut.examples;

import com.github.dakusui.pcond.examples.UTExample;
import com.github.dakusui.pcond.utils.Metatest;
import com.github.dakusui.pcond.utils.TestBase;
import org.junit.Test;

public class JUnitBasedExamplesTest extends TestBase {
  @Test
  public void testExampleUT() {
    Metatest.verifyTestClass(UTExample.class);
  }
}
