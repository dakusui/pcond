package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.utils.TestBase;
import org.junit.Test;

public class Issue42Test extends TestBase {
  @Test(expected = IllegalArgumentException.class)
  public void whenNoMatchersTo_Crest_allOf_$thenIllegalArgumentExceptionThrown() {
    try {
      Crest.allOf();
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void whenNoMatchersTo_Crest_anyOf_$thenIllegalArgumentExceptionThrown() {
    try {
      Crest.anyOf();
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }
}
