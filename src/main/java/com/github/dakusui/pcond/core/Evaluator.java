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
   * @param <T>         The type of the `value`.
   * @param evaluableIo An evaluation context.
   * @param conjunction A conjunction predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Conjunction
   */
  <T> void evaluate(EvaluableIo<T, Evaluable.Conjunction<T>, Boolean> evaluableIo, Evaluable.Conjunction<T> conjunction);

  /**
   * Evaluates `value` with a `disjunction` predicate ("or").
   *
   * @param <T>         The type of the `value`.
   * @param evaluableIo An evaluation context.
   * @param disjunction A disjunction predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Disjunction
   */
  <T> void evaluate(EvaluableIo<T, Evaluable.Disjunction<T>, Boolean> evaluableIo, Evaluable.Disjunction<T> disjunction);

  /**
   * Evaluates `value` with a `negation` predicate ("not").
   *
   * @param <T>         The type of the `value`.
   * @param evaluableIo An evaluation context.
   * @param negation    A negation predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Negation
   */
  <T> void evaluate(EvaluableIo<T, Evaluable.Negation<T>, Boolean> evaluableIo, Evaluable.Negation<T> negation);

  /**
   * Evaluates `value` with a leaf predicate.
   *
   * @param <T>         The type of the `value`.
   * @param evaluableIo An evaluation context.
   * @param leafPred    A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.LeafPred
   */
  <T> void evaluate(EvaluableIo<T, Evaluable.LeafPred<T>, Boolean> evaluableIo, Evaluable.LeafPred<T> leafPred);

  /**
   * Evaluates `value` with a context predicate.
   *
   * @param evaluableIo        An evaluation context.
   * @param variableBundlePred A predicate with which `value` is evaluated.
   * @see Evaluable.VariableBundlePred
   */
  void evaluate(EvaluableIo<VariableBundle, Evaluable.VariableBundlePred, Boolean> evaluableIo, Evaluable.VariableBundlePred variableBundlePred);

  /**
   * Evaluates `value` with a "transformatioin" predicate.
   *
   * @param evaluableIo    An evaluation context.
   * @param transformation A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Transformation
   */
  <T, R> void evaluate(EvaluableIo<T, Evaluable.Transformation<T, R>, R> evaluableIo, Evaluable.Transformation<T, R> transformation);

  /**
   * Evaluates `value` with a "function" predicate.
   *
   * @param evaluableIo An evaluation context.
   * @param func        A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Func
   */
  <T, R> void evaluate(EvaluableIo<T, Evaluable.Func<T>, R> evaluableIo, Evaluable.Func<T> func);

  /**
   * Evaluates `value` with a predicate for a stream.
   *
   * @param evaluableIo An evaluation context.
   * @param streamPred  A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.StreamPred
   */
  <E> void evaluate(EvaluableIo<Stream<E>, Evaluable.StreamPred<E>, Boolean> evaluableIo, Evaluable.StreamPred<E> streamPred);

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
    public Impl() {
    }

    @Override
    public <T> void evaluate(EvaluableIo<T, Evaluable.Conjunction<T>, Boolean> evaluableIo, Evaluable.Conjunction<T> conjunction) {
    }

    @Override
    public <T> void evaluate(EvaluableIo<T, Evaluable.Disjunction<T>, Boolean> evaluableIo, Evaluable.Disjunction<T> disjunction) {
    }

    @Override
    public <T> void evaluate(EvaluableIo<T, Evaluable.Negation<T>, Boolean> evaluableIo, Evaluable.Negation<T> negation) {
    }

    @Override
    public <T> void evaluate(EvaluableIo<T, Evaluable.LeafPred<T>, Boolean> evaluableIo, Evaluable.LeafPred<T> leafPred) {
    }

    @Override
    public <T, R> void evaluate(EvaluableIo<T, Evaluable.Func<T>, R> evaluableIo, Evaluable.Func<T> func) {
    }

    @Override
    public void evaluate(EvaluableIo<VariableBundle, Evaluable.VariableBundlePred, Boolean> evaluableIo, Evaluable.VariableBundlePred variableBundlePred) {
    }

    @Override
    public <T, R> void evaluate(EvaluableIo<T, Evaluable.Transformation<T, R>, R> evaluableIo, Evaluable.Transformation<T, R> transformation) {
    }

    @Override
    public <E> void evaluate(EvaluableIo<Stream<E>, Evaluable.StreamPred<E>, Boolean> evaluableIo, Evaluable.StreamPred<E> streamPred) {
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