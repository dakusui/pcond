package com.github.dakusui.pcond.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import static com.github.dakusui.pcond.core.EvaluationEntry.Type.*;
import static com.github.dakusui.pcond.core.Evaluator.Impl.EVALUATION_SKIPPED;
import static com.github.dakusui.pcond.core.ValueHolder.State.EXCEPTION_THROWN;
import static com.github.dakusui.pcond.core.ValueHolder.State.VALUE_RETURNED;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * The new design:
 *
 * Evaluator: Concentrates on "evaluate" an individual evaluable (form). No aware of how to compose evaluation entries.
 */

public class EvaluationContext<T> {
  final List<EvaluationEntry>                 evaluationEntries = new LinkedList<>();
  final List<EvaluableIo<T, Evaluable<T>, ?>> visitorLineage    = new LinkedList<>();

  boolean expectationFlipped = false;

  public EvaluationContext() {
  }

  public boolean isExpectationFlipped() {
    return this.expectationFlipped;
  }

  public void flipExpectation() {
    this.expectationFlipped = !expectationFlipped;
  }

  static String composeDetailOutputActualValueFromInputAndThrowable(Object input, Throwable throwable) {
    StringBuilder b = new StringBuilder();
    b.append("Input: '").append(input).append("'").append(format("%n"));
    b.append("Input Type: ").append(input == null ? "(null)" : input.getClass().getName()).append(format("%n"));
    b.append("Thrown Exception: '").append(throwable.getClass().getName()).append("'").append(format("%n"));
    b.append("Exception Message: ").append(throwable.getMessage()).append(format("%n"));
    for (StackTraceElement each : throwable.getStackTrace()) {
      b.append("\t");
      b.append(each);
      b.append(format("%n"));
    }
    return b.toString();
  }

  /**
   * @param evaluableIo       An object to hold a form and its I/O.
   * @param evaluatorCallback A callback that executes a logic specific to the {@code evaluable}.
   */
  public <E extends Evaluable<T>, O> void evaluate(EvaluableIo<T, E, O> evaluableIo, BiFunction<E, ValueHolder<T>, ValueHolder<O>> evaluatorCallback) {
    requireNonNull(evaluableIo);
    EvaluableIo<T, E, O> evaluableIoWork = this.enter(evaluableIo.input(), evaluableIo.evaluable());
    ValueHolder<O> out = evaluatorCallback.apply(evaluableIo.evaluable(), evaluableIo.input());
    this.leave(evaluableIoWork, out);
  }

  public static <T> EvaluationEntry.Type resolveEvaluationEntryType(Evaluable<T> evaluable) {
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
      return TRANSFORM_AND_CHECK;
    throw new IllegalArgumentException();
  }


  @SuppressWarnings("unchecked")
  private <E extends Evaluable<T>, O> EvaluableIo<T, E, O> enter(ValueHolder<T> input, E evaluable) {
    EvaluableIo<T, Evaluable<T>, O> ret = createEvaluableIo(input, evaluable);
    this.evaluationEntries.add(createEvaluationEntry(this, ret));
    this.expectationFlipped = this.expectationFlipped ^ evaluable.requestExpectationFlip();
    this.visitorLineage.add(ret);
    return (EvaluableIo<T, E, O>) ret;
  }


  private <E extends Evaluable<T>, O> void leave(EvaluableIo<T, E, O> evaluableIo, ValueHolder<O> output) {
    this.visitorLineage.remove(this.visitorLineage.size() - 1);
    this.expectationFlipped = this.expectationFlipped ^ evaluableIo.evaluable().requestExpectationFlip();
    if (output.isValueReturned())
      evaluableIo.valueReturned(output.returnedValue());
    else if (output.isExceptionThrown())
      evaluableIo.exceptionThrown(output.thrownException());
    else if (output.isEvaluationSkipped())
      evaluableIo.evaluationSkipped();
    else
      assert false : output;
  }

  private static <T, O> EvaluableIo<T, Evaluable<T>, O> createEvaluableIo(ValueHolder<T> input, Evaluable<T> evaluable) {
    return new EvaluableIo<>(input, resolveEvaluationEntryType(evaluable), evaluable);
  }

  private static <T, E extends Evaluable<T>> EvaluationEntry createEvaluationEntry(
      EvaluationContext<T> evaluationContext,
      EvaluableIo<T, E, ?> evaluableIo) {
    return new EvaluationEntry.Impl(evaluationContext, evaluableIo);
  }

  static <T, E extends Evaluable<T>> Object computeInputActualValue(EvaluableIo<T, E, ?> evaluableIo) {
    return evaluableIo.input().value();
  }

  static <T, E extends Evaluable<T>> Object computeOutputExpectation(EvaluationContext<T> evaluationContext, EvaluableIo<T, E, ?> evaluableIo) {
    if (evaluableIo.output().state() == VALUE_RETURNED) {
      if (evaluableIo.evaluableType() == LEAF)
        return !evaluationContext.expectationFlipped;
      return evaluableIo.output().returnedValue();
    } else if (evaluableIo.output().state() == EXCEPTION_THROWN)
      return EVALUATION_SKIPPED;
    else
      throw new AssertionError("output state=<" + evaluableIo.output().state() + ">");
  }

  static <T, E extends Evaluable<T>> Object computeOutputActualValue(EvaluableIo<T, E, ?> evaluableIo) {
    if (evaluableIo.output().state() == VALUE_RETURNED)
      return evaluableIo.output().returnedValue();
    if (evaluableIo.output().state() == EXCEPTION_THROWN)
      return evaluableIo.output().thrownException();
    else
      throw new AssertionError();
  }

  static <T, E extends Evaluable<T>> boolean isExplanationRequired(EvaluationEntry.Type evaluationEntryType, EvaluationContext<T> evaluationContext, EvaluableIo<T, E, ?> evaluableIo) {
    return asList(FUNCTION, LEAF).contains(evaluationEntryType) && (
        evaluableIo.output().state() == ValueHolder.State.EXCEPTION_THROWN || (
            evaluableIo.evaluableType() == LEAF && (
                evaluationContext.expectationFlipped ^ !(Boolean) evaluableIo.output().returnedValue())));
  }

  public List<EvaluationEntry> resultEntries() {
    return new ArrayList<>(this.evaluationEntries);
  }

  public <R> void importEntries(EvaluationContext<R> childContext) {
    childContext.resultEntries().forEach(each -> this.evaluationEntries.add((EvaluationEntry.Finalized) each));
  }
}
