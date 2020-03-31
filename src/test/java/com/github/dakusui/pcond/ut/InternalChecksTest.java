package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.internals.InternalChecks;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InternalChecksTest {
  @Test(expected = IllegalArgumentException.class)
  public void testRequireArgument$fails() {
    String message = "value is not zero";
    try {
      InternalChecks.requireArgument(1, i -> i == 0, () -> message);
    } catch (IllegalArgumentException e) {
      assertEquals(message, e.getMessage());
      throw e;
    }
  }
}
