package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.TestAssertions.assumeThat;
import static com.github.dakusui.pcond.forms.Predicates.*;

public class TestAssertionsTest extends TestBase {
  @Test(expected = ComparisonFailure.class)
  public void testAllOf() {
    try {
      assertThat("hello", allOf(startsWith("H"), endsWith("o")));
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test(expected = ComparisonFailure.class)
  public void testAnyOf() {
    try {
      assertThat("hello", anyOf(startsWith("H"), endsWith("!")));
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void testAssumeThat() {
    assumeThat("hello", allOf(startsWith("H"), endsWith("o")));
  }
}
