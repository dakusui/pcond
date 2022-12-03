package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.core.context.VariableBundle;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.explainValue;

/**
 * A visitor interface that defines a mechanism to "evaluate" printable predicates.
 */
public interface Evaluator {
  /**
   * Evaluates `value` with `conjunction` predicate ("and").
   *
   * @param <T>               The type of the `value`.
   * @param evaluableIo       An evaluation context.
   * @param evaluationContext
   * @see com.github.dakusui.pcond.core.Evaluable.Conjunction
   */
  <T> void evaluateConjunction(EvaluableIo<T, Evaluable.Conjunction<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a `disjunction` predicate ("or").
   *
   * @param <T>               The type of the `value`.
   * @param evaluableIo       An evaluation context.
   * @param evaluationContext
   * @see com.github.dakusui.pcond.core.Evaluable.Disjunction
   */
  <T> void evaluateDisjunction(EvaluableIo<T, Evaluable.Disjunction<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a `negation` predicate ("not").
   *
   * @param <T>               The type of the `value`.
   * @param evaluableIo       An evaluation context.
   * @param evaluationContext
   * @see com.github.dakusui.pcond.core.Evaluable.Negation
   */
  <T> void evaluateNegation(EvaluableIo<T, Evaluable.Negation<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a leaf predicate.
   *
   * @param <T>               The type of the `value`.
   * @param evaluableIo       An evaluation context.
   * @param evaluationContext
   * @see com.github.dakusui.pcond.core.Evaluable.LeafPred
   */
  <T> void evaluateLeaf(EvaluableIo<T, Evaluable.LeafPred<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a context predicate.
   *
   * @param evaluableIo       An evaluation context.
   * @param evaluationContext
   * @see Evaluable.VariableBundlePred
   */
  void evaluateVariableBundlePredicate(EvaluableIo<VariableBundle, Evaluable.VariableBundlePred, Boolean> evaluableIo, EvaluationContext<VariableBundle> evaluationContext);

  /**
   * Evaluates `value` with a "transformatioin" predicate.
   *
   * @param evaluableIo       An evaluation context.
   * @param evaluationContext
   * @see com.github.dakusui.pcond.core.Evaluable.Transformation
   */
  <T, R> void evaluateTransformation(EvaluableIo<T, Evaluable.Transformation<T, R>, R> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a "function" predicate.
   *
   * @param evaluableIo       An evaluation context.
   * @param evaluationContext
   * @see com.github.dakusui.pcond.core.Evaluable.Func
   */
  <T, R> void evaluateFunction(EvaluableIo<T, Evaluable.Func<T>, R> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a predicate for a stream.
   *
   * @param evaluableIo       An evaluation context.
   * @param evaluationContext
   * @see com.github.dakusui.pcond.core.Evaluable.StreamPred
   */
  <E> void evaluateStreamPredicate(EvaluableIo<Stream<E>, Evaluable.StreamPred<E>, Boolean> evaluableIo, EvaluationContext<Stream<E>> evaluationContext);

  boolean resultValueAsBoolean(EvaluationResultHolder<Object> evaluationResultHolder);

  /**
   * Returns a new instance of this interface.
   *
   * @return a new instance of this interface.
   */
  static Evaluator create() {
    return new Impl();
  }

  List<EvaluationEntry> resultEntries();

  class Impl implements Evaluator {
    public static final  Object NOT_EVALUATED = new Object() {
      @Override
      public String toString() {
        return "(not evaluated)";
      }
    };
    private static final Object NULL_VALUE    = new Object();
    public static final Object                    UNKNOWN       = new Snapshottable() {
      @Override
      public Object snapshot() {
        return this.toString();
      }

      @Override
      public String toString() {
        return "(unknown)";
      }
    };
    public Impl() {
    }

    @Override
    public <T> void evaluateConjunction(EvaluableIo<T, Evaluable.Conjunction<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {

    }

    @Override
    public <T> void evaluateDisjunction(EvaluableIo<T, Evaluable.Disjunction<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
    }

    @Override
    public <T> void evaluateNegation(EvaluableIo<T, Evaluable.Negation<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
    }

    @Override
    public <T> void evaluateLeaf(EvaluableIo<T, Evaluable.LeafPred<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
    }

    @Override
    public <T, R> void evaluateFunction(EvaluableIo<T, Evaluable.Func<T>, R> evaluableIo, EvaluationContext<T> evaluationContext) {
    }

    @Override
    public void evaluateVariableBundlePredicate(EvaluableIo<VariableBundle, Evaluable.VariableBundlePred, Boolean> evaluableIo, EvaluationContext<VariableBundle> evaluationContext) {
    }

    @Override
    public <T, R> void evaluateTransformation(EvaluableIo<T, Evaluable.Transformation<T, R>, R> evaluableIo, EvaluationContext<T> evaluationContext) {
    }

    @Override
    public <E> void evaluateStreamPredicate(EvaluableIo<Stream<E>, Evaluable.StreamPred<E>, Boolean> evaluableIo, EvaluationContext<Stream<E>> evaluationContext) {
    }

    @Override
    public boolean resultValueAsBoolean(EvaluationResultHolder<Object> evaluationResultHolder) {
      if (evaluationResultHolder.value() instanceof Boolean)
        return (boolean) evaluationResultHolder.value();
      return false;
    }

    @Override
    public List<EvaluationEntry> resultEntries() {
      return Collections.emptyList();
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