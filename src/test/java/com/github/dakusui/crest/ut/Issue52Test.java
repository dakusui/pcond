package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.Crest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.opentest4j.AssertionFailedError;

public class Issue52Test {
  @Test
  public void givenPassingAssertion$whenAssertThrows$thenPass() {
    Assert.assertThat(
        Crest.assertThrows(IllegalArgumentException.class, () -> {
          throw new IllegalArgumentException();
        }),
        CoreMatchers.instanceOf(IllegalArgumentException.class));
  }

  @Test(expected = AssertionFailedError.class)
  public void givenFailingAssertion() {
    Crest.assertThrows(AssertionFailedError.class, () -> {
      throw new IllegalArgumentException();
    });
  }

  @Test(expected = AssertionFailedError.class)
  public void givenFailingAssertion2() {
    Crest.assertThrows(AssertionFailedError.class, () -> {
    });
  }
}
