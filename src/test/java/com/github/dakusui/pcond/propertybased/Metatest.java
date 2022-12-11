package com.github.dakusui.pcond.propertybased;

import com.github.dakusui.pcond.forms.Predicates;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.io.IOException;

import static com.github.dakusui.pcond.propertybased.TestCaseUtils.exerciseTestCase;

public class Metatest {
  @Test
  public void whenExpectedValueIsReturned_thenPasses() throws Throwable {
    exerciseTestCase(new TestCase.Builder.ForReturnedValue<>(null, Predicates.isNull(), Object.class).build());
  }

  @Test(expected = ComparisonFailure.class)
  public void whenUnexpectedValueIsReturned_thenComparisonFailureIsThrown() throws Throwable {
    exerciseTestCase(new TestCase.Builder.ForReturnedValue<>("notNull", Predicates.isNull(), Object.class).build());
  }


  @Test(expected = AssertionError.class)
  public void whenUnexpectedExceptionIsThrown_thenAssertionErrorIsThrown() throws Throwable {
    exerciseTestCase(new TestCase.Builder.ForThrownException<>("", Predicates.isNull(), IOException.class).build());
  }

  @Test
  public void whenExpectedExceptionIsThrown_thenPasses() throws Throwable {
    exerciseTestCase(new TestCase.Builder.ForThrownException<>("", Predicates.isNull(), ComparisonFailure.class).build());
  }
}
