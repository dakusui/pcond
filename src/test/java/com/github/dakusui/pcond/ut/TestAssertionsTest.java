package com.github.dakusui.pcond.ut;

import junit.framework.AssertionFailedError;
import org.junit.Test;

import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.TestAssertions.assumeThat;
import static com.github.dakusui.pcond.functions.Predicates.*;

public class TestAssertionsTest {
  @Test(expected = AssertionFailedError.class)
  public void testAllOf() {
    assertThat("hello", allOf(startsWith("H"), endsWith("o")));
  }

  @Test(expected = AssertionFailedError.class)
  public void testAnyOf() {
    assertThat("hello", anyOf(startsWith("H"), endsWith("!")));
  }

  @Test
  public void testAssumeThat() {
    assumeThat("hello", allOf(startsWith("H"), endsWith("o")));
  }
}
