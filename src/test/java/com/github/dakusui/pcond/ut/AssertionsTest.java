package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.Assertions;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class AssertionsTest {
  public static class Passing {
    @Test
    public void testAssertObject$thenPass() {
      String var = "hello";
      Assertions.assertValue(var, Predicates.equalsIgnoreCase("HELLo"));
    }

    @Test
    public void testAssertBoolean$thenPass() {
      boolean var = true;
      Assertions.assertBoolean(var, Predicates.isTrue());
    }

    @Test
    public void testAssertByte$thenPass() {
      byte var = 10;
      Assertions.assertByte(var, Predicates.ge((byte) 10).and(Predicates.lt((byte) 20)));
    }

    @Test
    public void testAssertChar$thenPass() {
      char var = 10;
      Assertions.assertChar(var, Predicates.ge((char) 10).and(Predicates.lt((char) 20)));
    }

    @Test
    public void testAssertShort$thenPass() {
      short var = 10;
      Assertions.assertShort(var, Predicates.ge((short) 10).and(Predicates.lt((short) 20)));
    }

    @Test
    public void testAssertInt$thenPass() {
      int var = 10;
      Assertions.assertInt(var, Predicates.ge(10).and(Predicates.lt(20)));
    }

    @Test
    public void testAssertLong$thenPass() {
      long var = 10;
      Assertions.assertLong(var, Predicates.ge(10L).and(Predicates.lt(20L)));
    }

    @Test
    public void testAssertFloat$thenPass() {
      float var = 10;
      Assertions.assertFloat(var, Predicates.ge(10f).and(Predicates.lt(20f)));
    }

    @Test
    public void testAssertDouble$thenPass() {
      double var = 10;
      Assertions.assertDouble(var, Predicates.ge(10d).and(Predicates.lt(20d)));
    }
  }

  public static class Failing extends TestBase.ForAssertionEnabledVM {

    @Test(expected = AssertionError.class)
    public void testAssertObject$thenFail() {
      String var = "hello!";
      Assertions.assertValue(var, Predicates.equalsIgnoreCase("HELLo"));
    }

    @Test(expected = AssertionError.class)
    public void testAssertBoolean$thenFail() {
      boolean var = false;
      Assertions.assertBoolean(var, Predicates.isTrue());
    }

    @Test(expected = AssertionError.class)
    public void testAssertByte$thenFail() {
      byte var = 20;
      Assertions.assertByte(var, Predicates.ge((byte) 10).and(Predicates.lt((byte) 20)));
    }

    @Test(expected = AssertionError.class)
    public void testAssertChar$thenFail() {
      char var = 20;
      Assertions.assertChar(var, Predicates.ge((char) 10).and(Predicates.lt((char) 20)));
    }

    @Test(expected = AssertionError.class)
    public void testAssertShort$thenFail() {
      short var = 20;
      Assertions.assertShort(var, Predicates.ge((short) 10).and(Predicates.lt((short) 20)));
    }

    @Test(expected = AssertionError.class)
    public void testAssertInt$thenFail() {
      int var = 20;
      Assertions.assertInt(var, Predicates.ge(10).and(Predicates.lt(20)));
    }

    @Test(expected = AssertionError.class)
    public void testAssertLong$thenFail() {
      long var = 20;
      Assertions.assertLong(var, Predicates.ge(10L).and(Predicates.lt(20L)));
    }

    @Test(expected = AssertionError.class)
    public void testAssertFloat$thenFail() {
      float var = 20;
      Assertions.assertFloat(var, Predicates.ge(10f).and(Predicates.lt(20f)));
    }
  }
}
