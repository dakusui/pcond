package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.Postconditions;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PostconditionsTest extends TestBase {
  public static class IntentionalException extends RuntimeException {
    public IntentionalException(String message) {
      super(message);
    }
  }

  @Test(expected = NullPointerException.class)
  public void testEnsureNonNull() {
    try {
      Object ret = Postconditions.ensureNonNull(null);
      System.out.println("<" + ret + ">");
    } catch (NullPointerException e) {
      assertEquals("value:null violated postcondition:value isNotNull", e.getMessage());
      throw e;
    }
  }

  @Test
  public void givenNonNull$whenEnsureNonNull$thenPasses() {
    Object ret = Postconditions.ensureNonNull("hello");
    System.out.println("<" + ret + ">");
    assertNotNull(ret);
  }

  @Test(expected = IllegalStateException.class)
  public void testEnsureState() {
    try {
      Object ret = Postconditions.ensureState(null, Predicates.isNotNull());
      System.out.println("<" + ret + ">");
    } catch (NullPointerException e) {
      assertEquals("value:null violated postcondition:value isNotNull", e.getMessage());
      throw e;
    }
  }

  @Test
  public void givenValidState$whenEnsureState$thenPasses() {
    Object ret = Postconditions.ensureState("hello", Predicates.isNotNull());
    System.out.println("<" + ret + ">");
    assertNotNull(ret);
  }

  @Test(expected = IntentionalException.class)
  public void testEnsure() {
    try {
      Object ret = Postconditions.ensure(
          null,
          Predicates.isNotNull(),
          (v, p) -> "Hello:" + v + ":" + p, IntentionalException::new);
      System.out.println("<" + ret + ">");
    } catch (IntentionalException e) {
      assertEquals("Hello:null:isNotNull", e.getMessage());
      throw e;
    }
  }

  @Test
  public void givenValidValue$whenEnsure$thenPasses() {
    Object ret = Postconditions.ensure(
        "hello",
        Predicates.isNotNull(),
        (v, p) -> "hello:" + v + ":" + p, IllegalArgumentException::new);
    System.out.println("<" + ret + ">");
    assertNotNull(ret);
  }

}
