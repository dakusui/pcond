package com.github.dakusui.pcond.compilability;

import org.junit.Test;

import static com.github.dakusui.pcond.Preconditions.requireArgument;
import static com.github.dakusui.pcond.functions.Predicates.*;

public class BooleanTest {
  @Test(expected = IllegalArgumentException.class)
  public void testIsTrue() {
    boolean var = false;
    requireArgument(var, isTrue());
    requireArgument(var, and(isFalse(), isTrue()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrapperIsTrue() {
    Boolean var = false;
    requireArgument(var, isTrue());
    requireArgument(var, and(isFalse(), isTrue()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIsFalse() {
    boolean var = true;
    requireArgument(var, isFalse());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrapperIsFalse() {
    Boolean var = true;
    requireArgument(var, isFalse());
  }
}
