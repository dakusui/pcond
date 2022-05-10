package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestSkippedException;

import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.forms.Predicates.*;

public class TestAssertionsTest extends TestBase {
  @Test(expected = AssertionFailedError.class)
  public void testAllOf() {
    try {
      assertThat("hello", allOf(startsWith("H"), endsWith("o")));
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test(expected = AssertionFailedError.class)
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

    throw new TestSkippedException();
    //assumeThat("hello", allOf(startsWith("H"), endsWith("o")));
  }
}
