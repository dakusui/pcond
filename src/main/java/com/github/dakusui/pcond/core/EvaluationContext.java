package com.github.dakusui.pcond.core;

import java.util.LinkedList;
import java.util.List;

import static com.github.dakusui.pcond.internals.InternalUtils.wrapIfNecessary;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * The new design:
 *
 * Evaluator: Concentrates on "evaluate" an individual evaluable (form). No aware of how to compose evaluation entries.
 */

public class EvaluationContext<T> {
  final List<EvaluationEntry.Finalized> evaluationEntries = new LinkedList<>();
  final List<EvaluationContext<T>>      visitorLineage    = new LinkedList<>();
  final Evaluable<T>                    evaluable;

  public EvaluationContext(Evaluable<T> evaluable) {
    this.evaluable = requireNonNull(evaluable);
  }

  public void visit(Evaluator evaluator) {
    EvaluationResultHolder<T> evaluationResultHolder = this.enter();
    try {
      this.evaluable.accept(evaluationResultHolder, evaluator);
    } catch (Throwable t) {
      // Whatever the exception here is, it will be an internal error (a bug in pcond).
      // Because `evaluable.accept()` should catch it if an exception is thrown from a leaf
      // predicate or function.
      // The exception should be stored in the evaluationResultHolder
      throw wrapIfNecessary(t);
    } finally {
      this.leave(evaluationResultHolder);
    }
  }


  private EvaluationResultHolder<T> enter() {
    this.visitorLineage.add(this);
    return createEvaluationResultHolder(this);
  }


  private void leave(EvaluationResultHolder<T> evaluationResultHolder) {
    this.evaluationEntries.add(createEvaluationEntry(this.evaluable, this, evaluationResultHolder));
    EvaluationContext<?> removed = this.visitorLineage.remove(this.visitorLineage.size() - 1);
    assert removed == this;
  }

  private static <T> EvaluationResultHolder<T> createEvaluationResultHolder(EvaluationContext<T> evaluationContext) {
    return null;
  }

  private static <T> EvaluationEntry.Finalized createEvaluationEntry(
      Evaluable<T> evaluable, EvaluationContext<T> evaluationContext,
      EvaluationResultHolder<T> evaluationResultHolder) {
    EvaluationEntry.Type evaluationEntryType = figureOutEvaluationEntryType(evaluable);
    return new EvaluationEntry.Finalized(
        String.format("%s", evaluable),
        evaluationEntryType,
        evaluationContext.visitorLineage.size(),
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        evaluable.isSquashable(),
        asList(EvaluationEntry.Type.FUNCTION, EvaluationEntry.Type.LEAF).contains(evaluationEntryType));
  }

  private static <T> EvaluationEntry.Type figureOutEvaluationEntryType(Evaluable<T> evaluable) {
    return null;
  }
}
