package com.github.dakusui.pcond.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

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
    evaluate(evaluableIo.evaluableType(), evaluableIo, evaluatorCallback);
  }

  public <E extends Evaluable<T>, O> void evaluate(EvaluationEntry.Type type, EvaluableIo<T, E, O> evaluableIo, BiFunction<E, ValueHolder<T>, ValueHolder<O>> evaluatorCallback) {
    evaluate(type, evaluableIo, io -> evaluatorCallback.apply(io.evaluable(), io.input()));
  }

  public <E extends Evaluable<T>, O> void evaluate(EvaluationEntry.Type type, EvaluableIo<T, E, O> evaluableIo, Function<EvaluableIo<T, E, O>, ValueHolder<O>> function) {
    requireNonNull(evaluableIo);
    EvaluableIo<T, E, O> evaluableIoWork = this.enter(type, evaluableIo.input(), evaluableIo.evaluable());
    this.leave(evaluableIoWork, function.apply(evaluableIoWork));
    DebuggingUtils.printTo(this, System.err, 1);
    updateEvaluableIo(evaluableIo, evaluableIoWork);
  }

  private static <T, E extends Evaluable<T>, O> void updateEvaluableIo(EvaluableIo<T, E, O> evaluableIo, EvaluableIo<T, E, O> evaluableIoWork) {
    evaluableIo.output(evaluableIoWork.output());
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
  private <E extends Evaluable<T>, O> EvaluableIo<T, E, O> enter(EvaluationEntry.Type type, ValueHolder<T> input, E evaluable) {
    EvaluableIo<T, Evaluable<T>, O> ret = createEvaluableIo(input, evaluable, type);
    this.evaluationEntries.add(createEvaluationEntry(this, ret));
    this.visitorLineage.add(evaluationEntries.get(evaluationEntries.size() - 1));
    return (EvaluableIo<T, E, O>) ret;
  }

  private <E extends Evaluable<T>, O> void leave(EvaluableIo<T, E, O> evaluableIo, ValueHolder<O> output) {
    EvaluationEntry.Impl currentEvaluationEntry = (EvaluationEntry.Impl) this.visitorLineage.remove(this.visitorLineage.size() - 1);
    evaluableIo.output(output);
    currentEvaluationEntry.finalizeValues();
  }

  private static <T, O> EvaluableIo<T, Evaluable<T>, O> createEvaluableIo(ValueHolder<T> input, Evaluable<T> evaluable, EvaluationEntry.Type type) {
    return new EvaluableIo<>(input, type, evaluable);
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

  public <R> void importEntries(EvaluationContext<R> childContext, int indentLevelGap) {
    childContext.evaluationEntries.forEach(each -> each.level += indentLevelGap);
    this.evaluationEntries.addAll(childContext.resultEntries());
  }

  public int currentIndentLevel() {
    return this.visitorLineage.size();
  }
}
