package com.github.dakusui.pcond.core;

import java.util.LinkedList;
import java.util.List;

import static com.github.dakusui.pcond.core.EvaluationEntry.Type.*;
import static com.github.dakusui.pcond.internals.InternalUtils.wrapIfNecessary;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * The new design:
 *
 * Evaluator: Concentrates on "evaluate" an individual evaluable (form). No aware of how to compose evaluation entries.
 */

public class EvaluationContext<T> {
  final List<EvaluationEntry.Finalized>       evaluationEntries = new LinkedList<>();
  final List<EvaluableIo<T, Evaluable<T>, ?>> visitorLineage    = new LinkedList<>();

  boolean flipExpectation = false;

  public EvaluationContext() {
  }

  public void visit(Evaluator evaluator, Evaluable<T> evaluable, EvaluationResultHolder<T> input) {
    requireNonNull(evaluator);
    requireNonNull(evaluable);
    requireNonNull(input);
    EvaluableIo<T, Evaluable<T>, ?> evaluableIo = this.enter(input, evaluable);
    try {
      evaluableIo.evaluable().accept(
          evaluableIo.input() /* Actually, we need to pass evaluableIo itself, not its input only */,
          evaluator);
    } catch (Throwable t) {
      // Whatever the exception here is, it will be an internal error (a bug in pcond).
      // Because `evaluable.accept()` should catch it if an exception is thrown from a leaf
      // predicate or function.
      // The exception should be stored in the evaluableIo
      throw wrapIfNecessary(t);
    } finally {
      this.leave(evaluableIo);
    }
  }


  private EvaluableIo<T, Evaluable<T>, ?> enter(EvaluationResultHolder<T> input, Evaluable<T> evaluable) {
    EvaluableIo<T, Evaluable<T>, ?> ret = createEvaluableIo(input, evaluable);
    this.flipExpectation = this.flipExpectation ^ evaluable.requestExpectationFlip();
    this.visitorLineage.add(ret);
    return ret;
  }


  private void leave(EvaluableIo<T, Evaluable<T>, ?> evaluableIo) {
    this.evaluationEntries.add(createEvaluationEntry(this, evaluableIo));
    this.visitorLineage.remove(this.visitorLineage.size() - 1);
  }

  private static <T> EvaluableIo<T, Evaluable<T>, ?> createEvaluableIo(EvaluationResultHolder<T> input, Evaluable<T> evaluable) {
    return new EvaluableIo<>(input, evaluable);
  }

  private static <T> EvaluationEntry.Finalized createEvaluationEntry(
      EvaluationContext<T> evaluationContext,
      EvaluableIo<T, Evaluable<T>, ?> evaluableIo) {
    Evaluable<T> evaluable = evaluableIo.evaluable();
    EvaluationEntry.Type evaluationEntryType = resolveEvaluationEntryType(evaluable);
    return new EvaluationEntry.Finalized(
        String.format("%s", evaluable),
        evaluationEntryType,
        evaluationContext.visitorLineage.size(),
        evaluableIo.input().value(),
        null,
        null,
        null,
        evaluableIo.input().value(),
        null,
        evaluableIo.output().value(),
        null,
        evaluable.isSquashable(),
        isExplanationRequired(evaluationEntryType, evaluationContext, evaluableIo));
  }

  private static <T> boolean isExplanationRequired(EvaluationEntry.Type evaluationEntryType, EvaluationContext<T> evaluationContext, EvaluableIo<T, Evaluable<T>, ?> evaluableIo) {
    return asList(FUNCTION, LEAF).contains(evaluationEntryType) && evaluationContext.flipExpectation ^ (Boolean)evaluableIo.output().returnedValue();
  }

  private static <T> EvaluationEntry.Type resolveEvaluationEntryType(Evaluable<T> evaluable) {
    if (evaluable instanceof Evaluable.LeafPred || evaluable instanceof Evaluable.VariableBundlePred || evaluable instanceof Evaluable.StreamPred)
      return LEAF;
    if (evaluable instanceof Evaluable.Func)
      return FUNCTION;
    if (evaluable instanceof Evaluable.Conjunction)
      return AND;
    if (evaluable instanceof Evaluable.Disjunction)
      return OR;
    if (evaluable instanceof Evaluable.Negation)
      return NOT;
    if (evaluable instanceof Evaluable.Transformation)
      return TRANSFORM; // How to model CHECK?
    throw new IllegalArgumentException();
  }
}
