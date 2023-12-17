package com.github.dakusui.ut.valid8j.compatibility;

import com.github.dakusui.pcond.forms.Predicates;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class ObjectTest {
  @Test
  public void givenNull() {
    assertFalse(Predicates.isInstanceOf(String.class).test(null));
  }
}
