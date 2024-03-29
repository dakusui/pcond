package com.github.dakusui.ut.thincrest.it;

import com.github.dakusui.pcond.validator.ExceptionComposer;
import org.junit.Test;
import org.opentest4j.AssertionFailedError;

import static com.github.dakusui.thincrest.TestAssertions.assertThat;
import static com.github.dakusui.pcond.forms.Predicates.isNull;

public class ExampleIT {
  @Test(expected = AssertionFailedError.class)
  public void useOpentest4j() {
    System.setProperty("com.github.dakusui.pcond.exceptionComposerForTestFailures", ExceptionComposer.ForTestAssertion.Opentest4J.class.getName());

    assertThat("hello", isNull());
  }
}
