package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.core.context.VariableBundle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.explainValue;
import static com.github.dakusui.pcond.internals.InternalUtils.wrapIfNecessary;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;

/**
 * A visitor interface that defines a mechanism to "evaluate" printable predicates.
 */
public interface Evaluator {
  /**
   * Evaluates `value` with `conjunction` predicate ("and").
   *
   * @param <T>               The type of the `value`.
   * @param evaluationContext
   * @param conjunction       A conjunction predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Conjunction
   */
  <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Conjunction<T> conjunction);

  /**
   * Evaluates `value` with a `disjunction` predicate ("or").
   *
   * @param <T>               The type of the `value`.
   * @param evaluationContext
   * @param disjunction       A disjunction predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Disjunction
   */
  <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Disjunction<T> disjunction);

  /**
   * Evaluates `value` with a `negation` predicate ("not").
   *
   * @param <T>               The type of the `value`.
   * @param evaluationContext
   * @param negation          A negation predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Negation
   */
  <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Negation<T> negation);

  /**
   * Evaluates `value` with a leaf predicate.
   *
   * @param <T>               The type of the `value`.
   * @param evaluationContext
   * @param leafPred          A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.LeafPred
   */
  <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.LeafPred<T> leafPred);

  /**
   * Evaluates `value` with a context predicate.
   *
   * @param evaluationContext
   * @param variableBundlePred A predicate with which `value` is evaluated.
   * @see Evaluable.VariableBundlePred
   */
  void evaluate(EvaluationContext<VariableBundle> evaluationContext, Evaluable.VariableBundlePred variableBundlePred);

  /**
   * Evaluates `value` with a "transformatioin" predicate.
   *
   * @param evaluationContext
   * @param transformation    A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Transformation
   */
  <T, R> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Transformation<T, R> transformation);

  /**
   * Evaluates `value` with a "function" predicate.
   *
   * @param evaluationContext
   * @param func              A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Func
   */
  <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Func<T> func);

  /**
   * Evaluates `value` with a predicate for a stream.
   *
   * @param evaluationContext
   * @param streamPred        A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.StreamPred
   */
  <E> void evaluate(EvaluationContext<Stream<E>> evaluationContext, Evaluable.StreamPred<E> streamPred);

  boolean resultValueAsBoolean(EvaluationResultHolder<Object> evaluationResultHolder);

  /**
   * Returns a list of result entries.
   *
   * @return A list of result entries.
   * @see EvaluationEntry
   */
  List<EvaluationEntry> resultEntries();


  /**
   * Returns a new instance of this interface.
   *
   * @return a new instance of this interface.
   */
  static Evaluator create() {
    return new Impl();
  }

  class Impl implements Evaluator {
    public static final  Object NOT_EVALUATED = new Object() {
      @Override
      public String toString() {
        return "(not evaluated)";
      }
    };
    private static final Object NULL_VALUE    = new Object();
    public static final  Object UNKNOWN       = new Snapshottable() {
      @Override
      public Object snapshot() {
        return this.toString();
      }

      @Override
      public String toString() {
        return "(unknown)";
      }
    };
    List<EvaluationEntry.OnGoing> onGoingEntries                = new LinkedList<>();
    List<EvaluationEntry>         entries                       = new ArrayList<>();
    boolean                       currentlyExpectedBooleanValue = true;

    public Impl() {
    }

    @Override
    public <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Conjunction<T> conjunction) {
    }

    @Override
    public <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Disjunction<T> disjunction) {
    }

    @Override
    public <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Negation<T> negation) {
    }

    @Override
    public <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.LeafPred<T> leafPred) {
    }

    @Override
    public <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Func<T> func) {
    }

    @Override
    public void evaluate(EvaluationContext<VariableBundle> evaluationContext, Evaluable.VariableBundlePred variableBundlePred) {
    }

    @Override
    public <T, R> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Transformation<T, R> transformation) {
    }

    @Override
    public <E> void evaluate(EvaluationContext<Stream<E>> evaluationContext, Evaluable.StreamPred<E> streamPred) {
    }

    @Override
    public boolean resultValueAsBoolean(EvaluationResultHolder<Object> evaluationResultHolder) {
      if (evaluationResultHolder.value() instanceof Boolean)
        return (boolean) evaluationResultHolder.value();
      return false;
    }

    @Override
    public List<EvaluationEntry> resultEntries() {
      return unmodifiableList(this.entries);
    }


    private <E> Predicate<E> createValueCheckingPredicateForStream(Evaluable.StreamPred<E> streamPredicate) {
      return e -> {
        Evaluator evaluator = this.copyEvaluator();

        boolean succeeded = false;
        boolean ret = false;
        Object throwable = "<<OUTPUT MISSING>>";
        EvaluationResultHolder<E> evaluationResultHolder = EvaluationResultHolder.forValue(e);
        try {
          streamPredicate.cut().accept(null, evaluator);
          succeeded = true;
        } catch (Error error) {
          throw error;
        } catch (Throwable t) {
          throwable = t;
          throw wrapIfNecessary(t);
        } finally {
          if (!succeeded || evaluator.resultValueAsBoolean((EvaluationResultHolder<Object>) evaluationResultHolder) == streamPredicate.valueToCut()) {
            importEvaluationEntries(evaluator.resultEntries(), throwable);
            ret = true;
          }
        }
        return ret;
      };
    }

    private Evaluator copyEvaluator() {
      Impl impl = new Impl();
      impl.currentlyExpectedBooleanValue = this.currentlyExpectedBooleanValue;
      return impl;
    }

    public void importEvaluationEntries
        (List<EvaluationEntry> resultEntries, Object other) {
      resultEntries.stream()
          .map(each -> createEvaluationEntryForImport(each, other))
          .forEach(each -> this.entries.add(each));
    }

    private EvaluationEntry.Finalized createEvaluationEntryForImport
        (EvaluationEntry entry, Object other) {
      assert entry instanceof EvaluationEntry.Finalized;
      return new EvaluationEntry.Finalized(
          entry.formName(),
          entry.type(),
          this.onGoingEntries.size() + entry.level(),
          entry.inputExpectation(), entry.detailInputExpectation(),
          entry.outputExpectation(), entry.detailOutputExpectation(),
          entry.inputActualValue(),
          entry.detailInputActualValue(),
          entry.evaluationFinished() ? entry.outputActualValue() : other,
          entry.detailOutputActualValue(),
          entry.isSquashable(),
          entry.requiresExplanation());
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
  }

  /**
   * If an input or an output value object of a form implements this interface,
   * The value returned by `snapshot` method is stored in a {@link EvaluationEntry}
   * record, instead of the value itself.
   */
  interface Snapshottable {

    Object NULL = new Object() {
      @Override
      public String toString() {
        return "null";
      }
    };

    Object snapshot();

    static Object toSnapshotIfPossible(Object value) {
      if (value instanceof Snapshottable)
        return ((Snapshottable) value).snapshot();
      if (value == null)
        return NULL;
      else
        return value;
    }
  }

  /**
   * An interface to define methods that make a predicate "explainable" to humans.
   */
  interface Explainable {
    Object explainOutputExpectation();

    Object explainActual(Object actualValue);

    static Object explainOutputExpectation(Object evaluable) {
      if (evaluable instanceof Explainable)
        return explainValue(((Explainable) evaluable).explainOutputExpectation());
      return null;
    }

    static Object explainInputActualValue(Object evaluable, Object actualValue) {
      if (evaluable instanceof Explainable)
        return explainValue(((Explainable) evaluable).explainActual(actualValue));
      return null;
    }
  }
}