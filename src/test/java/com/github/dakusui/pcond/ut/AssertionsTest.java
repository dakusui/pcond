package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.Assertions;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class AssertionsTest {
  public static class Passing {
    @Test
    public void testAssertThatValue$thenPass() {
      String var = "10";
      assert Assertions.that(var, Predicates.ge("10").and(Predicates.lt("20")));
    }
  }

  public static class Failing extends TestBase.ForAssertionEnabledVM {
    @Test(expected = AssertionError.class)
    public void testAssertThatValue$thenPass() {
      String var = "20";
      assert Assertions.that(var, Predicates.ge("10").and(Predicates.lt("20")));
    }
  }

  public static class MessageTest {
    @Test
    public void composeMessage$thenComposed() {
      assertEquals("Value: \"hello\" violated: isNull", Assertions.composeMessage("hello", Predicates.isNull()));
    }
  }
}
