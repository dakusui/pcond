package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.core.context.VariableBundle;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.EvaluationContext.formNameOf;
import static com.github.dakusui.pcond.core.EvaluationContext.resolveEvaluationEntryType;
import static com.github.dakusui.pcond.core.EvaluationEntry.composeDetailOutputActualValueFromInputAndThrowable;
import static com.github.dakusui.pcond.core.ValueHolder.State.EXCEPTION_THROWN;
import static com.github.dakusui.pcond.core.ValueHolder.State.VALUE_RETURNED;
import static com.github.dakusui.pcond.internals.InternalUtils.explainValue;
import static com.github.dakusui.pcond.internals.InternalUtils.isDummyFunction;
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
   * Evaluates `value` with a "function" predicate.
   *
   * @param evaluableIo       An object to hold an evaluable and its input and output.
   * @param evaluationContext An evaluation context.
   * @see com.github.dakusui.pcond.core.Evaluable.Func
   */
  <T, R> void evaluateFunction(EvaluableIo<T, Evaluable.Func<T>, R> evaluableIo, EvaluationContext<T> evaluationContext);

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
    public static final Object EVALUATION_SKIPPED = new Object() {
      @Override
      public String toString() {
        return "(not evaluated)";
      }
    };

    private static final Object NULL_VALUE = new Object() {
      public String toString() {
        return "null";
      }
    };

    public static final Object UNKNOWN = new Snapshottable() {
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
          (Evaluable.Conjunction<T> evaluable, ValueHolder<T> input) -> {
            ValueHolder<Boolean> ret = ValueHolder.create();
            boolean result = true;
            for (Evaluable<T> each : evaluable.children()) {
              EvaluableIo<T, Evaluable<T>, Boolean> child = createChildEvaluableIoOf(each, input);
              each.accept(child, evaluationContext, this);
              ValueHolder<Boolean> outputFromEach = child.output();
              if (outputFromEach.isValueReturned()) {
                result &= outputFromEach.returnedValue();
                ret = ValueHolder.forValue(result);
              } else if (child.output().isExceptionThrown())
                ret = ValueHolder.<Boolean>create().evaluationSkipped();
              else if (child.output().isEvaluationSkipped())
                ret = ValueHolder.<Boolean>create().evaluationSkipped();
              else
                assert false;
              if (evaluable.shortcut() && (ret.isEvaluationSkipped() || !result))
                break;
            }
            return ret;
          });
    }

    @Override
    public <T> void evaluateDisjunction(EvaluableIo<T, Evaluable.Disjunction<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
      evaluationContext.evaluate(
          evaluableIo,
          (Evaluable.Disjunction<T> evaluable, ValueHolder<T> input) -> {
            ValueHolder<Boolean> ret = ValueHolder.create();
            boolean result = false;
            for (Evaluable<T> each : evaluable.children()) {
              EvaluableIo<T, Evaluable<T>, Boolean> child = createChildEvaluableIoOf(each, input);
              each.accept(child, evaluationContext, this);
              ValueHolder<Boolean> outputFromEach = child.output();
              if (outputFromEach.isValueReturned()) {
                result |= outputFromEach.returnedValue();
                ret = ValueHolder.forValue(result);
              } else if (outputFromEach.isExceptionThrown())
                ret = ValueHolder.<Boolean>create().evaluationSkipped();
              else if (outputFromEach.isEvaluationSkipped())
                ret = ValueHolder.<Boolean>create().evaluationSkipped();
              else
                assert false;
              if (evaluable.shortcut() && (ret.isEvaluationSkipped() || result))
                break;
            }
            return ret;
          });
    }

    @Override
    public <T> void evaluateNegation(EvaluableIo<T, Evaluable.Negation<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
      evaluationContext.evaluate(
          evaluableIo,
          (Evaluable.Negation<T> evaluable, ValueHolder<T> input) -> {
            evaluationContext.flipExpectation();
            try {
              EvaluableIo<T, Evaluable<T>, Boolean> childIo = createChildEvaluableIoOf(evaluable.target(), input);
              evaluable.target().accept(childIo, evaluationContext, this);
              return childIo.output().isValueReturned() ?
                  ValueHolder.forValue(evaluationContext.isExpectationFlipped() ^ childIo.output().returnedValue()) :
                  childIo.output();
            } finally {
              evaluationContext.flipExpectation();
            }
          }
      );
    }

    @Override
    public <T> void evaluateLeaf(EvaluableIo<T, Evaluable.LeafPred<T>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
      evaluationContext.evaluate(
          evaluableIo,
          (evaluable, input) -> {
            ValueHolder<Boolean> ret = ValueHolder.create();
            if (input.isValueReturned()) {
              T value = input.returnedValue();
              Predicate<? super T> predicate = requireNonNull(evaluable.predicate());
              try {
                return ret.valueReturned(predicate.test(value));
              } catch (Throwable t) {
                return ret.exceptionThrown(t);
              }
            } else
              return ret.evaluationSkipped();
          });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, R> void evaluateFunction(EvaluableIo<T, Evaluable.Func<T>, R> evaluableIo, EvaluationContext<T> evaluationContext) {
      evaluationContext.evaluate(
          evaluableIo,
          (Evaluable.Func<T> evaluable, ValueHolder<T> input) -> {
            ValueHolder<R> ret;
            {
              System.out.printf("BEGIN: func:%s(%s)%n", evaluable, input);
              System.out.printf("BEGIN: head:%s(%s)%n", evaluable, input);
              EvaluationContext<T> childContext = new EvaluationContext<>();
              EvaluableIo<T, Evaluable<T>, Object> ioForHead = createChildEvaluableIoOf(evaluable, input);
              childContext.evaluate(ioForHead, io -> {
                ValueHolder<Object> tmp = ValueHolder.create();
                if (input.isValueReturned())
                  tmp = applyFunction(tmp, io.input().returnedValue(), ((Evaluable.Func<T>) io.evaluable()).head());
                else
                  tmp = tmp.evaluationSkipped();
                return tmp;
              });
              ret = (ValueHolder<R>) ioForHead.output();
              evaluationContext.importEntries(childContext, 0);
              System.out.printf("END:   head:%s(%s)%n", evaluable, ret);
            }
            ValueHolder<Object> finalRet = (ValueHolder<Object>) ret;
            return evaluable.tail().map((Evaluable<Object> e) -> {
              System.out.printf("BEGIN: tail:%s(%s)%n", e, finalRet);
              EvaluationContext<Object> childContext = new EvaluationContext<>();
              EvaluableIo<Object, Evaluable<Object>, R> ioForTail = createChildEvaluableIoOf(e, finalRet);
              e.accept(ioForTail, childContext, this);
              System.out.printf("END:   tail:%s(%s)=%s%n", e, finalRet, ioForTail.output());
              return ioForTail.output();
            }).orElse(ret);
          });
    }

    @SuppressWarnings("unchecked")
    private static <T, R> ValueHolder<R> applyFunction(ValueHolder<R> ret, T in, Function<? super T, Object> function) {
      System.out.println("-->function:" + function);
      try {
        R returnedValue;
        returnedValue = (R) function.apply(in);
        return ret.valueReturned(returnedValue);
      } catch (Throwable t) {
        return ret.exceptionThrown(t);
      }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T, R> void evaluateTransformation(EvaluableIo<T, Evaluable.Transformation<T, R>, Boolean> evaluableIo, EvaluationContext<T> evaluationContext) {
      Evaluable<T> mapper = evaluableIo.evaluable().mapper();
      Evaluable<R> checker = evaluableIo.evaluable().checker();
      if (isDummyFunction((Function<?, ?>) mapper)) {
        checker.accept((EvaluableIo<R, Evaluable<R>, Boolean>) (Evaluable) evaluableIo, (EvaluationContext<R>) evaluationContext, this);
        return;
      }
      evaluationContext.evaluate(
          evaluableIo,
          (Evaluable.Transformation<T, R> evaluable, ValueHolder<T> input) -> {
            EvaluableIo<T, Evaluable<T>, R> mapperIo = evaluateMapper(mapper, input, evaluationContext);
            EvaluableIo<R, Evaluable<R>, Boolean> checkerIo = evaluateChecker(checker, mapperIo, evaluationContext);
            return checkerIo.output();
          }
      );
    }

    private <T, R> EvaluableIo<R, Evaluable<R>, Boolean> evaluateChecker(Evaluable<R> checker, EvaluableIo<T, Evaluable<T>, R> mapperIo, EvaluationContext<T> evaluationContext) {
      EvaluableIo<R, Evaluable<R>, Boolean> ret = createChildEvaluableIoOf(checker, mapperIo.output());
      {
        EvaluationContext<R> childContext = new EvaluationContext<>();
        checker.accept(ret, childContext, this);
        evaluationContext.importEntries(childContext);
      }
      return ret;
    }

    private <T, R> EvaluableIo<T, Evaluable<T>, R> evaluateMapper(Evaluable<T> mapper, ValueHolder<T> input, EvaluationContext<T> evaluationContext) {
      EvaluableIo<T, Evaluable<T>, R> ioForMapper = createChildEvaluableIoOf(mapper, input);
      {
        EvaluationContext<T> childContext = new EvaluationContext<>();
        mapper.accept(ioForMapper, childContext, this);
        evaluationContext.importEntries(childContext);
      }
      return ioForMapper;
    }

    @Override
    public <E> void evaluateStreamPredicate(EvaluableIo<Stream<E>, Evaluable.StreamPred<E>, Boolean> evaluableIo, EvaluationContext<Stream<E>> evaluationContext) {
      evaluationContext.evaluate(
          evaluableIo,
          (Evaluable.StreamPred<E> evaluable, ValueHolder<Stream<E>> input) -> {
            System.out.printf("BEGIN: StreamPredicate: %s(%s)%n", evaluable, input);
            ValueHolder<Boolean> ret = input.returnedValue()
                .map((E e) -> {
                  EvaluationContext<E> childContext = new EvaluationContext<>();
                  EvaluableIo<E, Evaluable<E>, Boolean> ioForCutPredicate = createChildEvaluableIoOf(evaluable.cut(), ValueHolder.forValue(e));
                  evaluable.cut().accept(ioForCutPredicate, childContext, this);
                  evaluationContext.importEntries(childContext);
                  return ioForCutPredicate.output();
                })
                .filter(eachResult -> {
                  if (!eachResult.isValueReturned())
                    return true;
                  return eachResult.returnedValue() == evaluable.valueToCut();
                })
                .findFirst()
                .orElseGet(() -> ValueHolder.forValue(evaluable.defaultValue()));
            System.out.printf("END:  StreamPredicate: %s(%s)=%s%n", evaluable, input, ret);
            return ret;
          });
    }

    @Override
    public void evaluateVariableBundlePredicate(EvaluableIo<VariableBundle, Evaluable.VariableBundlePred, Boolean> evaluableIo, EvaluationContext<VariableBundle> evaluationContext) {
      evaluationContext.evaluate(evaluableIo, (Evaluable.VariableBundlePred evaluable, ValueHolder<VariableBundle> input) -> {
        System.out.printf("VariableBundlePredicate:BEGIN: %s(%s)%n", evaluable, input);
        EvaluableIo<Object, Evaluable<Object>, Boolean> io = createChildEvaluableIoOf(evaluable.enclosed(), ValueHolder.forValue(input.returnedValue().valueAt(evaluable.argIndex())));
        EvaluationContext<Object> childContext = new EvaluationContext<>();
        evaluable.enclosed().accept(io, childContext, this);
        evaluationContext.importEntries(childContext);
        System.out.printf("VariableBundlePredicate:END: %s(%s)=%s%n", evaluable, input, io.output());
        return io.output();
      });
    }

    private static <T, E extends Evaluable<T>, O> EvaluableIo<T, Evaluable<T>, O> createChildEvaluableIoOf(E evaluable, ValueHolder<T> input) {
      return new EvaluableIo<>(input, resolveEvaluationEntryType(evaluable), evaluable);
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
      if (evaluable instanceof Evaluable)
        return formNameOf((Evaluable<?>) evaluable);
      return null;
    }

    static Object explainInputActualValue(Object evaluable, Object actualValue) {
      if (evaluable instanceof Explainable)
        return explainValue(((Explainable) evaluable).explainActual(actualValue));
      return null;
    }

    static <T, E extends Evaluable<T>> Object explainActual(EvaluableIo<T, E, ?> evaluableIo) {
      if (evaluableIo.output().state() == VALUE_RETURNED) {
        T ret = evaluableIo.input().returnedValue();
        return ret != null ? ret : Impl.NULL_VALUE;
      } else if (evaluableIo.output().state() == EXCEPTION_THROWN)
        return composeDetailOutputActualValueFromInputAndThrowable(evaluableIo.input().value(), evaluableIo.output().thrownException());
      else
        throw new AssertionError();
    }
  }
}