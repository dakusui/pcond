package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.core.context.VariableBundle;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.EvaluationContext.composeDetailOutputActualValueFromInputAndThrowable;
import static com.github.dakusui.pcond.core.EvaluationContext.resolveEvaluationEntryType;
import static com.github.dakusui.pcond.core.EvaluationEntry.Type.CHECK;
import static com.github.dakusui.pcond.core.EvaluationEntry.Type.TRANSFORM;
import static com.github.dakusui.pcond.core.ValueHolder.State.EXCEPTION_THROWN;
import static com.github.dakusui.pcond.core.ValueHolder.State.VALUE_RETURNED;
import static com.github.dakusui.pcond.internals.InternalUtils.*;
import static java.util.Objects.requireNonNull;

/**
 * A visitor interface that defines a mechanism to "evaluate" printable predicates.
 */
public interface Evaluator {
  /**
   * Evaluates `value` with `conjunction` predicate ("and").
   *
   * @param <T>               The type of the `value`.
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see com.github.dakusui.pcond.core.Evaluable.Conjunction
   */
  <T> void evaluateConjunction(EvaluableIo<T, Evaluable.Conjunction<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a `disjunction` predicate ("or").
   *
   * @param <T>               The type of the `value`.
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see com.github.dakusui.pcond.core.Evaluable.Disjunction
   */
  <T> void evaluateDisjunction(EvaluableIo<T, Evaluable.Disjunction<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a `negation` predicate ("not").
   *
   * @param <T>               The type of the `value`.
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see com.github.dakusui.pcond.core.Evaluable.Negation
   */
  <T> void evaluateNegation(EvaluableIo<T, Evaluable.Negation<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a leaf predicate.
   *
   * @param <T>               The type of the `value`.
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see com.github.dakusui.pcond.core.Evaluable.LeafPred
   */
  <T> void evaluateLeaf(EvaluableIo<T, Evaluable.LeafPred<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a context predicate.
   *
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see Evaluable.VariableBundlePred
   */
  void evaluateVariableBundlePredicate(EvaluableIo<VariableBundle, Evaluable.VariableBundlePred, Boolean> evaluableIo, EvaluationContext<VariableBundle> evaluationContext);

  /**
   * Evaluates `value` with a "transformatioin" predicate.
   *
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see com.github.dakusui.pcond.core.Evaluable.Transformation
   */
  <T, R> void evaluateTransformation(EvaluableIo<T, Evaluable.Transformation<T, R>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a "function" predicate.
   *
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see com.github.dakusui.pcond.core.Evaluable.Func
   */
  <T, R> void evaluateFunction(EvaluableIo<T, Evaluable.Func<T>, R> evaluableIo, EvaluationContext<T> evaluationContext);

  /**
   * Evaluates `value` with a predicate for a stream.
   *
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see com.github.dakusui.pcond.core.Evaluable.StreamPred
   */
  <E> void evaluateStreamPredicate(EvaluableIo<Stream<E>, Evaluable.StreamPred<E>, Boolean> evaluableIo, EvaluationContext<Stream<E>> evaluationContext);

  /**
   * Returns a new instance of this interface.
   *
   * @return a new instance of this interface.
   */
  static Evaluator create() {
    return new Impl();
  }

  class Impl implements Evaluator {
    public static final  Object EVALUATION_SKIPPED = new Object() {
      @Override
      public String toString() {
        return "(not evaluated)";
      }
    };
    private static final Object NULL_VALUE         = new Object();
    public static final  Object UNKNOWN            = new Snapshottable() {
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
      evaluationContext.evaluate(
          evaluableIo,
          (EvaluableIo<T, Evaluable.Conjunction<T>, Boolean> io) -> {
            boolean result = true;
            boolean skipped = false;
            for (Evaluable<T> each : io.evaluable().children()) {
              EvaluableIo<T, Evaluable<T>, Boolean> child = createChildEvaluableIoOf(io, each);
              each.accept(child, evaluationContext, this);
              ValueHolder<Boolean> outputFromEach = child.output();
              if (outputFromEach.isValueReturned())
                result &= outputFromEach.returnedValue();
              else if (child.output().isExceptionThrown())
                skipped = true;
              else if (child.output().isEvaluationSkipped())
                skipped = true;
              else
                assert false;
              if (io.evaluable().shortcut() && (skipped || !result))
                break;
            }
            if (skipped)
              io.evaluationSkipped();
            else
              io.valueReturned(result);
          });
    }

    @Override
    public <T> void evaluateDisjunction(EvaluableIo<T, Evaluable.Disjunction<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
      evaluationContext.evaluate(
          evaluableIo,
          (EvaluableIo<T, Evaluable.Disjunction<T>, Boolean> io) -> {
            boolean result = false;
            boolean skipped = false;
            for (Evaluable<T> each : io.evaluable().children()) {
              EvaluableIo<T, Evaluable<T>, Boolean> child = createChildEvaluableIoOf(io, each);
              each.accept(child, evaluationContext, this);
              ValueHolder<Boolean> outputFromEach = child.output();
              if (outputFromEach.isValueReturned())
                result |= outputFromEach.returnedValue();
              else if (outputFromEach.isExceptionThrown())
                skipped = true;
              else if (outputFromEach.isEvaluationSkipped())
                skipped = true;
              else
                assert false;
              if (io.evaluable().shortcut() && (skipped || result))
                break;
            }
            if (skipped)
              io.evaluationSkipped();
            else
              io.valueReturned(result);
          });
    }

    @Override
    public <T> void evaluateNegation(EvaluableIo<T, Evaluable.Negation<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
      evaluationContext.evaluate(
          evaluableIo,
          (EvaluableIo<T, Evaluable.Negation<T>, Boolean> io) -> {
            EvaluableIo<T, Evaluable<T>, Boolean> childIo = createChildEvaluableIoOf(io, io.evaluable().target());
            io.evaluable().target().accept(childIo, evaluationContext, this);
            updateOutputOfEvaluableIo(io, childIo, output -> evaluationContext.expectationFlipped ^ output.returnedValue());
          }
      );
    }

    @Override
    public <T> void evaluateLeaf(EvaluableIo<T, Evaluable.LeafPred<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
      evaluationContext.evaluate(
          evaluableIo,
          io -> {
            if (io.input().isValueReturned()) {
              T value = io.input().returnedValue();
              Predicate<? super T> predicate = requireNonNull(io.evaluable().predicate());
              try {
                boolean result = predicate.test(value);
                io.valueReturned(result);
              } catch (Throwable t) {
                io.exceptionThrown(t);
              }
            } else
              io.evaluationSkipped();
          });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, R> void evaluateFunction(EvaluableIo<T, Evaluable.Func<T>, R> evaluableIo, EvaluationContext<T> evaluationContext) {
      evaluationContext.evaluate(
          evaluableIo,
          (EvaluableIo<T, Evaluable.Func<T>, R> io) -> {
            if (io.input().isValueReturned()) {
              io.valueReturned((R) io.evaluable().head().apply(io.input().returnedValue()));
            } else
              io.evaluationSkipped();
            EvaluationContext<R> childContext = new EvaluationContext<>();
            evaluableIo.evaluable().<R>tail().ifPresent((Evaluable<R> tail) -> {
              tail.accept(new EvaluableIo<>(io.output(), resolveEvaluationEntryType(tail), tail), childContext, this);
              evaluationContext.importEntries(childContext);
            });
          });
    }

    @Override
    public void evaluateVariableBundlePredicate(EvaluableIo<VariableBundle, Evaluable.VariableBundlePred, Boolean> evaluableIo, EvaluationContext<VariableBundle> evaluationContext) {
      evaluationContext.evaluate(evaluableIo, (EvaluableIo<VariableBundle, Evaluable.VariableBundlePred, Boolean> io) -> {
      });
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T, R> void evaluateTransformation(EvaluableIo<T, Evaluable.Transformation<T, R>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
      if (isDummyFunction((Function<?, ?>) evaluableIo.evaluable().mapper())) {
        evaluableIo.evaluable().checker().accept((EvaluableIo<R, Evaluable<R>, Boolean>) (Evaluable) evaluableIo, (EvaluationContext<R>) evaluationContext, this);
        return;
      }
      evaluationContext.evaluate(
          evaluableIo,
          (EvaluableIo<T, Evaluable.Transformation<T, R>, Boolean> io_) -> {
            EvaluableIo<T, Evaluable.Transformation<T, R>, R> ioForMapper = new EvaluableIo<>(io_.input(), TRANSFORM, evaluableIo.evaluable());
            evaluationContext.evaluate(
                ioForMapper,
                (EvaluableIo<T, Evaluable.Transformation<T, R>, R> childIo) -> {
                  childIo.evaluable().mapper().accept((EvaluableIo<T, Evaluable<T>, R>) (EvaluableIo) childIo, evaluationContext, this);
                  childIo.valueReturned(null);
                });
            EvaluableIo<R, Evaluable<R>, Boolean> ioForChecker = new EvaluableIo<>(ioForMapper.output(), CHECK, evaluableIo.evaluable().checker());
            ((EvaluationContext<R>) evaluationContext).evaluate(
                ioForChecker,
                (EvaluableIo<R, Evaluable<R>, Boolean> childIo) -> {
                  childIo.evaluable().accept((EvaluableIo) childIo, (EvaluationContext<R>) evaluationContext, this);
                  updateOutputOfEvaluableIo(io_, childIo, ValueHolder::returnedValue);
                });
          }
      );
    }

    @Override
    public <E> void evaluateStreamPredicate(EvaluableIo<Stream<E>, Evaluable.StreamPred<E>, Boolean> evaluableIo, EvaluationContext<Stream<E>> evaluationContext) {
      evaluationContext.evaluate(evaluableIo, (EvaluableIo<Stream<E>, Evaluable.StreamPred<E>, Boolean> io) -> {
        if (io.input().isValueReturned()) {
          boolean defaultValue = io.evaluable().defaultValue();
          boolean ret = io.input().returnedValue()
              .filter((E e) -> checkValue(e, evaluableIo.evaluable(), io, evaluationContext))
              .findFirst()
              .map(each -> !defaultValue)
              .orElse(defaultValue);
          updateOutputOfEvaluableIo(io, io, v -> true);
        }
      });
    }

    private <E> boolean checkValue(E value, Evaluable.StreamPred<E> evaluable, EvaluableIo<Stream<E>, Evaluable.StreamPred<E>, Boolean> io, EvaluationContext<Stream<E>> evaluationContext) {
      return false;
    }

    private <E> Predicate<E> createPredicateForStream(Evaluable.StreamPred<E> p, EvaluableIo<Stream<E>, Evaluable.StreamPred<E>, Boolean> io, EvaluationContext<Stream<E>> evaluationContext) {
      return e -> {
        boolean succeeded = false;
        boolean ret = false;
        Object throwable = NULL_VALUE;
        try {
          EvaluationContext<E> c = new EvaluationContext<>();
          EvaluableIo<E, Evaluable<E>, Boolean> childIo = new EvaluableIo(io.input(), resolveEvaluationEntryType(io.evaluable()), io.evaluable());
          p.cut().accept(childIo, c, this);
          succeeded = true;
        } catch (Throwable t) {
          throwable = t;
          throw wrapIfNecessary(t);
        } finally {
          /*
          if (!succeeded || evaluator.<Boolean>resultValue() == p.valueToCut()) {
            importResultEntries(evaluator.resultEntries(), throwable);
            ret = true;
          }

           */
        }
        return ret;
      };

    }


    private static <T, E extends Evaluable<T>, R, O> EvaluableIo<T, Evaluable<T>, O> createChildEvaluableIoOf(EvaluableIo<T, ? extends Evaluable<T>, R> evaluableIo, E evaluable) {
      return new EvaluableIo<>(evaluableIo.input(), resolveEvaluationEntryType(evaluable), evaluable);
    }

    /**
     * @param io                    The IO object to which childIo is imported.
     * @param childIo
     * @param returnedValueResolver A function that resolves returned value from
     * @param <T>
     * @param <R>
     * @param <E>
     * @param <F>
     */
    private static <T, R, E extends Evaluable<T>, F extends Evaluable<R>> void updateOutputOfEvaluableIo(
        EvaluableIo<T, E, Boolean> io,
        EvaluableIo<R, F, Boolean> childIo,
        Function<ValueHolder<Boolean>, Boolean> returnedValueResolver) {
      ValueHolder<Boolean> outputFromChildIo = childIo.output();
      if (outputFromChildIo.isValueReturned())
        io.valueReturned(returnedValueResolver.apply(outputFromChildIo));
      else if (outputFromChildIo.isExceptionThrown())
        io.exceptionThrown(outputFromChildIo.thrownException());
      else if (outputFromChildIo.isEvaluationSkipped())
        io.evaluationSkipped();
      else
        assert false;
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

    static <T, E extends Evaluable<T>> Object explainActual(EvaluableIo<T, E, ?> evaluableIo) {
      if (evaluableIo.output().state() == VALUE_RETURNED)
        return evaluableIo.input().returnedValue();
      else if (evaluableIo.output().state() == EXCEPTION_THROWN)
        return composeDetailOutputActualValueFromInputAndThrowable(evaluableIo.input().value(), evaluableIo.output().thrownException());
      else
        throw new AssertionError();
    }
  }
}