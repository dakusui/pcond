package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.internals.Exceptions;
import com.github.dakusui.pcond.ut.testdata.IntentionalException;
import org.junit.Test;

public class ExceptionsTest {
  @Test(expected = IntentionalError.class)
  public void testWrapIfNecessary() {
    throw Exceptions.wrapIfNecessary(new IntentionalError());
  }
  @Test(expected = IntentionalException.class)
  public void testWrapIfNecessaryWithRuntimeException() {
    throw Exceptions.wrapIfNecessary(new IntentionalException("hi"));
  }

  @Test(expected = Exceptions.InternalException.class)
  public void testWrapIfNecessaryWithCheckedException() {
    throw Exceptions.wrapIfNecessary(new Exception("hi"));
  }
}
