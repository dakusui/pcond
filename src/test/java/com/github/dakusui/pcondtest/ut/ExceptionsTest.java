package com.github.dakusui.pcondtest.ut;

import com.github.dakusui.pcond.internals.InternalException;
import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcondtest.ut.testdata.IntentionalException;
import org.junit.Test;

public class ExceptionsTest {
  @Test(expected = IntentionalError.class)
  public void testWrapIfNecessary() {
    throw InternalUtils.wrapIfNecessary(new IntentionalError());
  }
  @Test(expected = IntentionalException.class)
  public void testWrapIfNecessaryWithRuntimeException() {
    throw InternalUtils.wrapIfNecessary(new IntentionalException("hi"));
  }

  @Test(expected = InternalException.class)
  public void testWrapIfNecessaryWithCheckedException() {
    throw InternalUtils.wrapIfNecessary(new Exception("hi"));
  }
}
