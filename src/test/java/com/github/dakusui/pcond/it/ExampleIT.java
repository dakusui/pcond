package com.github.dakusui.pcond.it;

import com.github.dakusui.pcond.valuechecker.ExceptionComposer;
import org.junit.Ignore;
import org.junit.Test;
import org.opentest4j.AssertionFailedError;

import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.forms.Predicates.isNull;

public class ExampleIT {
  @Test(expected = AssertionFailedError.class)
  public void useOpentest4j() {
    System.setProperty("com.github.dakusui.pcond.exceptionComposerForAssertThat", ExceptionComposer.ForTestAssertion.Opentest4J.class.getName());

    assertThat("hello", isNull());
  }
}
