package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.core.ExecutionFailure;
import com.github.dakusui.crest.utils.TestBase;
import org.junit.Test;

import static com.github.dakusui.crest.Crest.asString;
import static com.github.dakusui.crest.Crest.assertThat;

public class Issue43Test extends TestBase {
  @Test(expected = ExpectedException.class)
  public void givenNoMatchers$whenAssert$thenExpectedExceptionThrown() {
    try {
      assertThat(
          "",
          asString("noSuchMethod").matcher()
      );
    } catch (ExecutionFailure e) {
      Throwable cause = e.getCause();
      cause.printStackTrace();
      if (cause instanceof RuntimeException && cause.getMessage().contains("noSuchMethod"))
        throw new ExpectedException();
    }
  }

  private static class ExpectedException extends RuntimeException {

  }
}
