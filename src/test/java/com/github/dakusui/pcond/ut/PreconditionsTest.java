package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.Preconditions;
import com.github.dakusui.pcond.functions.Functions;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.provider.PreconditionViolationException;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static com.github.dakusui.pcond.functions.Predicates.isNotNull;
import static com.github.dakusui.pcond.utils.TestUtils.firstLineOf;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class PreconditionsTest extends TestBase.ForAssertionEnabledVM {
  @Test(expected = NullPointerException.class)
  public void testRequireNonNull() {
    try {
      Preconditions.requireNonNull(null);
    } catch (NullPointerException e) {
      e.printStackTrace();
      assertThat(firstLineOf(e.getMessage()),
          allOf(
              notNullValue(),
              is("value:null violated precondition:value isNotNull")));
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRequireArgument() {
    try {
      Preconditions.requireArgument(null, isNotNull());
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertThat(firstLineOf(e.getMessage()),
          allOf(
              notNullValue(),
              is("value:null violated precondition:value isNotNull")));
      throw e;
    }
  }

  @Test(expected = IllegalStateException.class)
  public void givenInvalidState$whenRequireState$thenIllegalStateExceptionThrown() {
    try {
      Preconditions.requireState(null, isNotNull());
    } catch (IllegalStateException e) {
      e.printStackTrace();
      assertThat(firstLineOf(e.getMessage()),
          allOf(
              notNullValue(),
              is("value:null violated precondition:value isNotNull")));
      throw e;
    }
  }

  @Test
  public void givenValidState$whenRequireState$thenPass() {
    String var = Preconditions.requireState("hello", isNotNull());
    assertNotNull(var);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRequireWithTransformingPredicate() {
    String value = "hello";
    Preconditions.requireArgument(
        value,
        Predicates.when(Functions.length()).then(Predicates.gt(100)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRequireWithCustomStringTransformingPredicate() {
    String value = "hello";
    try {
      Preconditions.requireArgument(
          value,
          Predicates.when("LENGTH", Functions.length())
              .then("GT[100]", Predicates.gt(100)));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertThat(
          firstLineOf(e.getMessage()),
          CoreMatchers.is("value:\"hello\" violated precondition:value LENGTH GT[100]"));
      throw e;
    }
  }

  @Test
  public void testRequireWithSatisfyingValue() {
    String value = "hello";
    assertThat(
        Preconditions.requireNonNull(value),
        is(value));
  }

  @Test
  public void testRequireWithTransformingPredicateAndSatisfyingValue() {
    String value = "hello";
    assertThat(
        Preconditions.requireArgument(
            value,
            Predicates.when(Functions.length()).then(Predicates.gt(0))),
        is(value));
  }

  @Test
  public void testRequireWithCustomStringTransformingPredicateAndSatisfyingValue() {
    String value = "hello";
    assertThat(
        Preconditions.requireArgument(
            value,
            Predicates.when(Functions.length()).then(Predicates.gt(0))),
        is(value));
  }

  @Test
  public void testRequire() {
    String message = Preconditions.require("hello", Predicates.isNotNull());
    assertNotNull(message);
  }

  @Test(expected = PreconditionViolationException.class)
  public void testRequire$thenError() {
    String value = null;
    String message = Preconditions.require(
        value,
        Predicates.isNotNull()
    );
    assertNotNull(message);
  }
}
