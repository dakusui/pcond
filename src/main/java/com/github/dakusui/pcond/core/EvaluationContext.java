package com.github.dakusui.pcond.core;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.github.dakusui.pcond.core.EvaluationEntry.Type.*;
import static com.github.dakusui.pcond.core.EvaluationResultHolder.State.EXCEPTION_THROWN;
import static com.github.dakusui.pcond.core.EvaluationResultHolder.State.VALUE_RETURNED;
import static com.github.dakusui.pcond.core.Evaluator.Explainable.explainInputActualValue;
import static com.github.dakusui.pcond.core.Evaluator.Explainable.explainOutputExpectation;
import static com.github.dakusui.pcond.core.Evaluator.Impl.composeDetailOutputActualValueFromInputAndThrowable;
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

  boolean expectationFlipped = false;

  public EvaluationContext() {
  }

  public void evaluate(EvaluationResultHolder<T> input, Evaluable<T> evaluable, BiConsumer<Evaluable<T>, EvaluableIo<T, Evaluable<T>, ?>> evaluatorCallback) {
    requireNonNull(evaluable);
    requireNonNull(input);
    EvaluableIo<T, Evaluable<T>, ?> evaluableIo = this.enter(input, evaluable);
    try {
      evaluatorCallback.accept(evaluable, evaluableIo);
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
    this.expectationFlipped = this.expectationFlipped ^ evaluable.requestExpectationFlip();
    this.visitorLineage.add(ret);
    return ret;
  }


  private void leave(EvaluableIo<T, Evaluable<T>, ?> evaluableIo) {
    this.evaluationEntries.add(createEvaluationEntry(this, evaluableIo));
    this.visitorLineage.remove(this.visitorLineage.size() - 1);
    this.expectationFlipped = this.expectationFlipped ^ evaluableIo.evaluable().requestExpectationFlip();
  }

  private static <T> EvaluableIo<T, Evaluable<T>, ?> createEvaluableIo(EvaluationResultHolder<T> input, Evaluable<T> evaluable) {
    return new EvaluableIo<>(input, resolveEvaluationEntryType(evaluable), evaluable);
  }

  private static <T> EvaluationEntry.Finalized createEvaluationEntry(
      EvaluationContext<T> evaluationContext,
      EvaluableIo<T, Evaluable<T>, ?> evaluableIo) {
    Evaluable<T> evaluable = evaluableIo.evaluable();
    EvaluationEntry.Type evaluationEntryType = evaluableIo.evaluableType();
    Object inputActualValue = evaluableIo.input().value();
    Object outputExpectation = computeOutputExpectation(evaluationContext, evaluableIo);
    Object detailOutputExpectation = explainOutputExpectation(evaluableIo.evaluable());
    Object detailInputActualValue = explainInputActualValue(evaluable, inputActualValue);
    Object outputActualValue1 = computeOutputActualValue(evaluableIo);
    Object detailOutputActualValue = explainOutputActualValue(evaluableIo);
    boolean squashable = evaluable.isSquashable();
    boolean explanationRequired = isExplanationRequired(evaluationEntryType, evaluationContext, evaluableIo);
    return createEvaluationEntry(
        evaluable,
        evaluationEntryType,
        evaluationContext.visitorLineage.size(),
        outputExpectation, detailOutputExpectation,
        inputActualValue, detailInputActualValue,
        outputActualValue1, detailOutputActualValue,
        squashable,
        explanationRequired
    );
  }

  private static <T> EvaluationEntry.Finalized createEvaluationEntry(Evaluable<T> evaluable, EvaluationEntry.Type evaluationEntryType, int indentLevel, Object outputExpectation, Object detailOutputExpectation, Object inputActualValue, Object detailInputActualValue, Object outputActualValue, Object detailOutputActualValue, boolean squashable, boolean explanationRequired) {
    return new EvaluationEntry.Finalized(
        String.format("%s", evaluable),
        evaluationEntryType,
        indentLevel,
        inputActualValue,
        detailInputActualValue,
        outputExpectation,
        detailOutputExpectation,
        inputActualValue,
        detailInputActualValue,
        outputActualValue,
        detailOutputActualValue,
        squashable,
        explanationRequired);
  }

  private static <T> Object computeOutputExpectation(EvaluationContext<T> evaluationContext, EvaluableIo<T, Evaluable<T>, ?> evaluableIo) {
    if (evaluableIo.output().state() == VALUE_RETURNED) {
      if (evaluableIo.evaluableType() == LEAF)
        return !evaluationContext.expectationFlipped;
      return evaluableIo.output().returnedValue();
    } else if (evaluableIo.output().state() == EXCEPTION_THROWN)
      return "(not available)";
    else
      throw new AssertionError();
  }

  private static <T> Object computeOutputActualValue(EvaluableIo<T, Evaluable<T>, ?> evaluableIo) {
    if (evaluableIo.output().state() == VALUE_RETURNED)
      return evaluableIo.output().returnedValue();
    if (evaluableIo.output().state() == EXCEPTION_THROWN)
      return evaluableIo.output().thrownException();
    else
      throw new AssertionError();
  }

  private static <T> Object explainOutputActualValue(EvaluableIo<T, Evaluable<T>, ?> evaluableIo) {
    if (evaluableIo.output().state() == VALUE_RETURNED)
      return evaluableIo.output().returnedValue();
    else if (evaluableIo.output().state() == EXCEPTION_THROWN)
      return composeDetailOutputActualValueFromInputAndThrowable(evaluableIo.input().value(), evaluableIo.output().thrownException());
    else
      throw new AssertionError();
  }

  private static <T> boolean isExplanationRequired(EvaluationEntry.Type evaluationEntryType, EvaluationContext<T> evaluationContext, EvaluableIo<T, Evaluable<T>, ?> evaluableIo) {
    return asList(FUNCTION, LEAF).contains(evaluationEntryType) && (
        evaluableIo.output().state() == EvaluationResultHolder.State.EXCEPTION_THROWN || (
            evaluableIo.evaluableType() == LEAF && (
                evaluationContext.expectationFlipped ^ (Boolean) evaluableIo.output().returnedValue())));
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
