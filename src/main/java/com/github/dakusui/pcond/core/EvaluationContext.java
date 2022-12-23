package com.github.dakusui.pcond.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import static com.github.dakusui.pcond.core.EvaluationEntry.Type.*;
import static java.util.Objects.requireNonNull;

/**
 * The new design:
 *
 * Evaluator: Concentrates on "evaluate" an individual evaluable (form). No aware of how to compose evaluation entries.
 */

public class EvaluationContext<T> {
  final List<EvaluationEntry> evaluationEntries = new LinkedList<>();
  final List<EvaluationEntry> visitorLineage    = new LinkedList<>();

  boolean expectationFlipped = false;

  public EvaluationContext() {
  }

  public static String formNameOf(Evaluable<?> evaluable) {
    return resolveEvaluationEntryType(evaluable).formName(evaluable);
  }

  public boolean isExpectationFlipped() {
    return this.expectationFlipped;
  }

  public void flipExpectation() {
    this.expectationFlipped = !expectationFlipped;
  }

  /**
   * @param evaluableIo       An object to hold a form and its I/O.
   * @param evaluatorCallback A callback that executes a logic specific to the {@code evaluable}.
   */
  public <E extends Evaluable<T>, O> void evaluate(EvaluableIo<T, E, O> evaluableIo, BiFunction<E, ValueHolder<T>, ValueHolder<O>> evaluatorCallback) {
    requireNonNull(evaluableIo);
    EvaluableIo<T, E, O> evaluableIoWork = this.enter(evaluableIo.input(), evaluableIo.evaluable());
    ValueHolder<O> out = evaluatorCallback.apply(evaluableIoWork.evaluable(), evaluableIoWork.input());
    this.leave(evaluableIoWork, out);
    updateEvaluableIo(evaluableIo, evaluableIoWork);
  }

  private static <T, E extends Evaluable<T>, O> void updateEvaluableIo(EvaluableIo<T, E, O> evaluableIo, EvaluableIo<T, E, O> evaluableIoWork) {
    if (evaluableIoWork.output().isValueReturned())
      evaluableIo.valueReturned(evaluableIoWork.output().returnedValue());
    else if (evaluableIoWork.output().isExceptionThrown())
      evaluableIo.exceptionThrown(evaluableIoWork.output().thrownException());
    else if (evaluableIoWork.output().isEvaluationSkipped())
      evaluableIo.evaluationSkipped();
    else
      assert false;
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
    this.visitorLineage.add(evaluationEntries.get(evaluationEntries.size() - 1));
    return (EvaluableIo<T, E, O>) ret;
  }

  private <E extends Evaluable<T>, O> void leave(EvaluableIo<T, E, O> evaluableIo, ValueHolder<O> output) {
    EvaluationEntry.Impl currentEvaluationEntry = (EvaluationEntry.Impl) this.visitorLineage.remove(this.visitorLineage.size() - 1);
    if (output.isValueReturned())
      evaluableIo.valueReturned(output.returnedValue());
    else if (output.isExceptionThrown())
      evaluableIo.exceptionThrown(output.thrownException());
    else if (output.isEvaluationSkipped())
      evaluableIo.evaluationSkipped();
    else
      assert false : output;
    currentEvaluationEntry.finalizeValues();
  }

  private static <T, O> EvaluableIo<T, Evaluable<T>, O> createEvaluableIo(ValueHolder<T> input, Evaluable<T> evaluable) {
    return new EvaluableIo<>(input, resolveEvaluationEntryType(evaluable), evaluable);
  }

  private static <T, E extends Evaluable<T>> EvaluationEntry createEvaluationEntry(
      EvaluationContext<T> evaluationContext,
      EvaluableIo<T, E, ?> evaluableIo) {
    return new EvaluationEntry.Impl(evaluationContext, evaluableIo);
  }

  public List<EvaluationEntry> resultEntries() {
    return new ArrayList<>(this.evaluationEntries);
  }

  public <R> void importEntries(EvaluationContext<R> childContext) {
    importEntries(childContext, currentIndentLevel());
  }

  private int currentIndentLevel() {
    return this.visitorLineage.size();
  }

  public <R> void importEntries(EvaluationContext<R> childContext, int indentLevelGap) {
    childContext.evaluationEntries.forEach(each -> each.level += indentLevelGap);
    this.evaluationEntries.addAll(childContext.resultEntries());
  }
}
