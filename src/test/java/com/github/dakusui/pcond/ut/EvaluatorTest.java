package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.core.ContextVariable;
import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.shared.utils.ut.TestBase;
import org.junit.Test;

import java.util.function.Predicate;

import static com.github.dakusui.pcond.forms.Predicates.*;
import static org.junit.Assert.*;

public class EvaluatorTest extends TestBase {
  @SuppressWarnings("unchecked")
  @Test
  public void testFinalizedEntry() {
    Evaluator evaluator = new Evaluator.Impl();
    try {
      evaluator.evaluate(
          ContextVariable.forValue("hello"),
          (Evaluable.Conjunction<String>) and(isNotNull(), errorThrowingPredicate()));
    } catch (EvaluationFailure e) {
      assertFalse(evaluator.resultEntries().get(0).hasOutput());
    }
  }

  @Test
  public void testDisjShortcut_withTrue() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<String> evaluable = (Evaluable<String>) or(isNull(), alwaysTrue());
    evaluable.accept(null, evaluator);
    assertTrue(evaluator.resultValue());
  }

  @Test
  public void testDisjShortcut_withFalse() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<String> evaluable = (Evaluable<String>) or(isNull(), alwaysTrue().negate());
    evaluable.accept(null, evaluator);
    assertTrue(evaluator.resultValue());
  }

  @Test
  public void testDisjNonShortcut() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<String> evaluable = (Evaluable<String>) or(alwaysTrue().negate(), isNull());
    evaluable.accept(null, evaluator);
    assertTrue(evaluator.resultValue());
  }

  @Test
  public void testConjShortcut_withTrue() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<String> evaluable = (Evaluable<String>) and(isNotNull(), alwaysTrue());
    evaluable.accept(null, evaluator);
    assertFalse(evaluator.resultValue());
  }

  @Test
  public void testConjShortcut_withFalse() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<String> evaluable = (Evaluable<String>) and(isNotNull(), alwaysTrue().negate());
    evaluable.accept(null, evaluator);
    assertFalse(evaluator.resultValue());
  }

  @Test
  public void testConjNonShortcut() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<String> evaluable = (Evaluable<String>) and(alwaysTrue(), isNotNull());
    evaluable.accept(null, evaluator);
    assertFalse(evaluator.resultValue());
  }

  public Predicate<String> errorThrowingPredicate() {
    return Printables.predicate("errorThrowing", v -> {
      throw new EvaluationFailure();
    });
  }

  public static class EvaluationFailure extends RuntimeException {
  }
}
