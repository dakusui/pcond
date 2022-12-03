package com.github.dakusui.pcond.ut;

import com.github.dakusui.shared.utils.ut.TestBase;
import org.junit.Test;

public class EvaluatorTest extends TestBase {
  /*
  @SuppressWarnings("unchecked")
  @Test
  public void testFinalizedEntry() {
    Evaluator evaluator = new Evaluator.Impl();
    try {
      evaluator.evaluate(
          , (Evaluable.Conjunction<String>) and(isNotNull(), errorThrowingPredicate()), EvaluationResultHolder.forValue("hello"),
          );
    } catch (EvaluationFailure e) {
      assertFalse(evaluator.resultEntries().get(0).evaluationFinished());
    }
  }

  @Test
  public void testDisjShortcut_withTrue() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<Object> evaluable = (Evaluable<Object>) or(isNull(), alwaysTrue());
    EvaluationResultHolder<Object> evaluationResultHolder = EvaluationResultHolder.forValue(null);
    evaluable.accept(evaluationResultHolder, evaluator);
    assertTrue(evaluator.resultValueAsBoolean(evaluationResultHolder));
  }

  @Test
  public void testDisjShortcut_withFalse() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<Object> evaluable = (Evaluable<Object>) or(isNull(), alwaysTrue().negate());
    EvaluationResultHolder<Object> evaluationResultHolder = EvaluationResultHolder.forValue(null);
    evaluable.accept(evaluationResultHolder, evaluator);
    assertTrue(evaluator.resultValueAsBoolean((evaluationResultHolder)));
  }

  @Test
  public void testDisjNonShortcut() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<Object> evaluable = (Evaluable<Object>) or(alwaysTrue().negate(), isNull());
    EvaluationResultHolder<Object> evaluationResultHolder = EvaluationResultHolder.forValue(null);
    evaluable.accept(evaluationResultHolder, evaluator);
    assertTrue(evaluator.resultValueAsBoolean((evaluationResultHolder)));
  }

  @Test
  public void testConjShortcut_withTrue() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<Object> evaluable = (Evaluable<Object>) and(isNotNull(), alwaysTrue());
    EvaluationResultHolder<Object> evaluationResultHolder = EvaluationResultHolder.forValue(null);
    evaluable.accept(evaluationResultHolder, evaluator);
    assertFalse(evaluator.resultValueAsBoolean((evaluationResultHolder)));
  }

  @Test
  public void testConjShortcut_withFalse() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<Object> evaluable = (Evaluable<Object>) and(isNotNull(), alwaysTrue().negate());
    EvaluationResultHolder<Object> evaluationResultHolder = EvaluationResultHolder.forValue(null);
    evaluable.accept(evaluationResultHolder, evaluator);
    assertFalse(evaluator.resultValueAsBoolean(evaluationResultHolder));
  }

  @Test
  public void testConjNonShortcut() {
    Evaluator evaluator = new Evaluator.Impl();
    @SuppressWarnings("unchecked") Evaluable<Object> evaluable = (Evaluable<Object>) and(alwaysTrue(), isNotNull());
    EvaluationResultHolder<Object> evaluationResultHolder = EvaluationResultHolder.forValue(null);
    evaluable.accept(evaluationResultHolder, evaluator);
    assertFalse(evaluator.resultValueAsBoolean(evaluationResultHolder));
  }
  public Predicate<Object> errorThrowingPredicate() {
    return Printables.predicate("errorThrowing", v -> {
      throw new EvaluationFailure();
    });
  }

  public static class EvaluationFailure extends RuntimeException {
  }

   */


  @Test
  public void uncommentTests() {
    throw new RuntimeException("Uncomment tests in this class and fix them.");
  }
}
