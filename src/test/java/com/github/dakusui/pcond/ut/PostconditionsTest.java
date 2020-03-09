package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.Postconditions;
import com.github.dakusui.pcond.functions.Predicates;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PostconditionsTest {
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
}
