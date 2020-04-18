package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.functions.Evaluable;
import com.github.dakusui.pcond.functions.Evaluator;
import com.github.dakusui.pcond.functions.Printables;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.function.Predicate;

import static com.github.dakusui.pcond.functions.Predicates.*;
import static org.junit.Assert.*;

public class EvaluatorTest extends TestBase {
  @SuppressWarnings("unchecked")
  @Test
  public void testFinalizedRecord() {
    Evaluator evaluator = new Evaluator.Impl();
    try {
      evaluator.evaluate(
          "hello",
          (Evaluable.Conjunction<String>) and(isNotNull(), errorThrowingPredicate()));
    } catch (EvaluationFailure e) {
      assertFalse(evaluator.resultRecords().get(0).output().isPresent());
    }
  }

  @Test(expected = IllegalStateException.class)
  public void testOngoingRecord_givenOngoingRecord$whenOutput$thenIllegalStateThrown() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable.Disjunction<String> evaluable = (Evaluable.Disjunction<String>) or(isNull(), alwaysTrue());
    evaluator.evaluate("hello", evaluable);
    try {
      evaluator.resultRecords().get(0).input();
    } catch (IllegalStateException e) {
      e.printStackTrace();
      assertThat(e.getMessage(), CoreMatchers.containsString("This object does not have an input"));
      throw e;
    }
  }

  @Test
  public void testDisjShortcut_withTrue() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<String> evaluable = (Evaluable<String>) or(isNull(), alwaysTrue());
    assertTrue(evaluable.accept(null, evaluator));
  }

  @Test
  public void testDisjShortcut_withFalse() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<String> evaluable = (Evaluable<String>) or(isNull(), alwaysTrue().negate());
    assertTrue(evaluable.accept(null, evaluator));
  }

  @Test
  public void testDisjNonShortcut() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<String> evaluable = (Evaluable<String>) or(alwaysTrue().negate(), isNull());
    assertTrue(evaluable.accept(null, evaluator));
  }

  @Test
  public void testConjShortcut_withTrue() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<String> evaluable = (Evaluable<String>) and(isNotNull(), alwaysTrue());
    assertFalse(evaluable.accept(null, evaluator));
  }

  @Test
  public void testConjShortcut_withFalse() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<String> evaluable = (Evaluable<String>) and(isNotNull(), alwaysTrue().negate());
    assertFalse(evaluable.accept(null, evaluator));
  }

  @Test
  public void testConjNonShortcut() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<String> evaluable = (Evaluable<String>) and(alwaysTrue(), isNotNull());
    assertFalse(evaluable.accept(null, evaluator));
  }

  public Predicate<String> errorThrowingPredicate() {
    return Printables.predicate("errorThrowing", v -> {
      throw new EvaluationFailure();
    });
  }

  public static class EvaluationFailure extends RuntimeException {
  }
}
