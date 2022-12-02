package com.github.dakusui.pcond.core;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

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
    this.evaluable = evaluable;
  }

  public void visit(Evaluator evaluator) {
    EvaluationResultHolder<T> evaluationResultHolder = enter(evaluator);
    try {
      evaluable.accept(evaluationResultHolder, evaluator);
    } finally {
      leave(evaluator, evaluable, evaluationResultHolder);
    }
  }


  private EvaluationResultHolder<T> enter(Evaluator evaluator) {
    this.visitorLineage.add(this);
    return createEvaluationResultHolder(this);
  }


  private void leave(Evaluator evaluator, Evaluable<T> evaluable, EvaluationResultHolder<T> evaluationResultHolder) {
    this.evaluationEntries.add(createEvaluationEntry(this, evaluationResultHolder));
    EvaluationContext<?> removed = this.visitorLineage.remove(this.visitorLineage.size() - 1);
    assert removed == this;
  }

  private static <T> EvaluationResultHolder<T> createEvaluationResultHolder(EvaluationContext<T> evaluationContext) {
    return null;
  }

  private static <T> EvaluationEntry.Finalized createEvaluationEntry(
      EvaluationContext<T> evaluationContext,
      EvaluationResultHolder<T> evaluationResultHolder) {
    Evaluable<T> evaluable = evaluationContext.evaluable;
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

  EvaluationContext createChild() {
    return new EvaluationContext(evaluable);
  }

  public enum State {
    VALUE_RETURNED {
      <V> V value(EvaluationResultHolder<V> vContextVariable) {
        return vContextVariable.value;
      }

      <V> Throwable exception(EvaluationResultHolder<V> vContextVariable) {
        throw new UnsupportedOperationException();
      }
    },
    EXCEPTION_THROWN {
      <V> V value(EvaluationResultHolder<V> vContextVariable) {
        throw new UnsupportedOperationException();
      }

      <V> Throwable exception(EvaluationResultHolder<V> vContextVariable) {
        return vContextVariable.exception;
      }
    };

    abstract <V> V value(EvaluationResultHolder<V> vContextVariable);

    abstract <V> Throwable exception(EvaluationResultHolder<V> vContextVariable);
  }
}
