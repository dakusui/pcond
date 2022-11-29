package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.core.context.VariableBundle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.EvaluationEntry.Type.*;
import static com.github.dakusui.pcond.core.Evaluator.Explainable.explainInputActualValue;
import static com.github.dakusui.pcond.core.Evaluator.Explainable.explainOutputExpectation;
import static com.github.dakusui.pcond.core.Evaluator.Impl.composeActualValueFromInputAndThrowable;
import static com.github.dakusui.pcond.core.Evaluator.Snapshottable.toSnapshotIfPossible;
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
   * @param value       A value to be evaluated.
   * @param conjunction A conjunction predicate with which `value` is evaluated.
   * @param <T>         The type of the `value`.
   * @see com.github.dakusui.pcond.core.Evaluable.Conjunction
   */
  <T> void evaluate(EvaluationContext<? extends T> value, Evaluable.Conjunction<T> conjunction);

  /**
   * Evaluates `value` with a `disjunction` predicate ("or").
   *
   * @param value       A value to be evaluated.
   * @param disjunction A disjunction predicate with which `value` is evaluated.
   * @param <T>         The type of the `value`.
   * @see com.github.dakusui.pcond.core.Evaluable.Disjunction
   */
  <T> void evaluate(EvaluationContext<T> value, Evaluable.Disjunction<T> disjunction);

  /**
   * Evaluates `value` with a `negation` predicate ("not").
   *
   * @param value    A value to be evaluated.
   * @param negation A negation predicate with which `value` is evaluated.
   * @param <T>      The type of the `value`.
   * @see com.github.dakusui.pcond.core.Evaluable.Negation
   */
  <T> void evaluate(EvaluationContext<T> value, Evaluable.Negation<T> negation);

  /**
   * Evaluates `value` with a leaf predicate.
   *
   * @param value    A value to be evaluated.
   * @param leafPred A predicate with which `value` is evaluated.
   * @param <T>      The type of the `value`.
   * @see com.github.dakusui.pcond.core.Evaluable.LeafPred
   */
  <T> void evaluate(EvaluationContext<T> value, Evaluable.LeafPred<T> leafPred);

  /**
   * Evaluates `value` with a context predicate.
   *
   * @param value              A value to be evaluated.
   * @param variableBundlePred A predicate with which `value` is evaluated.
   * @see Evaluable.VariableBundlePred
   */
  void evaluate(EvaluationContext<VariableBundle> value, Evaluable.VariableBundlePred variableBundlePred);

  /**
   * Evaluates `value` with a "transformatioin" predicate.
   *
   * @param value          A value to be evaluated.
   * @param transformation A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Transformation
   */
  <T, R> void evaluate(EvaluationContext<T> value, Evaluable.Transformation<T, R> transformation);

  /**
   * Evaluates `value` with a "function" predicate.
   *
   * @param value A value to be evaluated.
   * @param func  A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Func
   */
  <T> void evaluate(EvaluationContext<T> value, Evaluable.Func<T> func);

  /**
   * Evaluates `value` with a predicate for a stream.
   *
   * @param value      A value to be evaluated.
   * @param streamPred A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.StreamPred
   */
  <E> void evaluate(EvaluationContext<Stream<E>> value, Evaluable.StreamPred<E> streamPred);

  boolean resultValueAsBoolean(EvaluationContext<Object> evaluationContext);

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

    void enter(EvaluableDesc evaluableDesc, EvaluationContext<?> evaluationContext) {
      EvaluationEntry.OnGoing newEntry = new EvaluationEntry.OnGoing(
          evaluableDesc.formName, evaluableDesc.type(), (int) onGoingEntries.stream().filter(each -> !each.isSquashable()).count(),
          evaluationContext.value(), toSnapshotIfPossible(evaluationContext.value()),
          this.currentlyExpectedBooleanValue, toSnapshotIfPossible(this.currentlyExpectedBooleanValue),
          evaluationContext, toSnapshotIfPossible(evaluationContext.value()),
          evaluableDesc.isSquashable(), entries.size()
      );
      this.onGoingEntries.add(newEntry);
      this.entries.add(newEntry);
      if (evaluableDesc.requestExpectationFlip())
        this.flipCurrentlyExpectedBooleanValue();
    }

    void leave(
        Evaluable<Object> evaluable,
        EvaluableIo io,
        EvaluationContext<Object> evaluationContext) {
      int positionInOngoingEntries = onGoingEntries.size() - 1;
      EvaluationEntry.OnGoing current = onGoingEntries.get(positionInOngoingEntries);
      this.entries.set(
          current.positionInEntries,
          io.type.finalizeEvaluationEntry(current, io, evaluable));
      io.type.finishEvaluationContext(evaluationContext, io.getOutputActualValue());
      evaluationContext.valueReturned(io.getOutputActualValue());
      this.onGoingEntries.remove(positionInOngoingEntries);
      if (evaluable.requestExpectationFlip())
        this.flipCurrentlyExpectedBooleanValue();
    }

    void leaveWithReturnedValue(
        Evaluable<Object> evaluable,
        EvaluableIo io,
        EvaluationContext<Object> evaluationContext) {
      int positionInOngoingEntries = onGoingEntries.size() - 1;
      EvaluationEntry.OnGoing current = onGoingEntries.get(positionInOngoingEntries);
      this.entries.set(
          current.positionInEntries,
          current.finalizeEntry(
              // evaluable
              io.getInputExpectation(), io.getInputExpectation(),
              io.getOutputExpectation(), explainOutputExpectation(evaluable),
              io.getInputActualValue(), explainInputActualValue(evaluable, io.getInputActualValue()),
              io.getOutputActualValue(),
              toSnapshotIfPossible(io.getInputActualValue()),
              io.requiresExplanation()));
      evaluationContext.valueReturned(io.getOutputActualValue());
      this.onGoingEntries.remove(positionInOngoingEntries);
      if (evaluable.requestExpectationFlip())
        this.flipCurrentlyExpectedBooleanValue();
    }

    void leaveWithThrownException(
        Evaluable<?> evaluable,
        EvaluableIo io,
        EvaluationContext<Object> evaluationContext) {
      int positionInOngoingEntries = onGoingEntries.size() - 1;
      EvaluationEntry.OnGoing current = onGoingEntries.get(positionInOngoingEntries);
      this.entries.set(
          current.positionInEntries,
          current.finalizeEntry(
              io.getInputExpectation(), (Snapshottable) io::getInputExpectation,
              io.getOutputExpectation(), explainOutputExpectation(evaluable),
              io.getInputActualValue(), explainInputActualValue(evaluable, io.getInputActualValue()),
              io.getOutputActualValue(),
              composeActualValueFromInputAndThrowable(io.getInputActualValue(), (Throwable) io.getOutputActualValue()),
              true));
      evaluationContext.exceptionThrown((Throwable) io.getOutputActualValue());
      this.onGoingEntries.remove(positionInOngoingEntries);
      if (evaluable.requestExpectationFlip())
        this.flipCurrentlyExpectedBooleanValue();
    }

    void flipCurrentlyExpectedBooleanValue() {
      this.currentlyExpectedBooleanValue = !this.currentlyExpectedBooleanValue;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public <T> void evaluate(EvaluationContext<? extends T> evaluationContext, Evaluable.Conjunction<T> conjunction) {
      int i = 0;
      boolean outputValue = true;
      boolean shortcut = conjunction.shortcut();
      this.enter(EvaluableDesc.fromEvaluable(conjunction), evaluationContext);
      Object inputActualValue = evaluationContext.value();
      for (Evaluable<? super T> each : conjunction.children()) {
        @SuppressWarnings("unchecked") EvaluationContext<Object> clonedContext = (EvaluationContext<Object>) evaluationContext.clone();
        each.accept((EvaluationContext) clonedContext, this);
        boolean cur = this.resultValueAsBooleanIfBooleanOtherwise(clonedContext, !this.currentlyExpectedBooleanValue);
        if (!cur)
          outputValue = cur; // This is constant, but keeping it for readability
        if ((shortcut && !outputValue) || i == conjunction.children().size() - 1) {
          boolean outputExpectation = outputExpectationFor(conjunction);
          leave(
              (Evaluable<Object>) conjunction,
              ioEntryForNonLeafWhenEvaluationFinished(outputExpectation, inputActualValue, outputValue), (EvaluationContext<Object>) evaluationContext);
          return;
        }
        i++;
      }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Disjunction<T> disjunction) {
      int i = 0;
      boolean outputValue = false;
      boolean shortcut = disjunction.shortcut();
      this.enter(EvaluableDesc.fromEvaluable(disjunction), evaluationContext);
      Object inputActualValue = evaluationContext.value();
      for (Evaluable<? super T> each : disjunction.children()) {
        @SuppressWarnings("unchecked") EvaluationContext<Object> clonedContext = (EvaluationContext<Object>) evaluationContext.clone();
        each.accept((EvaluationContext) clonedContext, this);
        boolean cur = this.resultValueAsBooleanIfBooleanOtherwise(clonedContext, !this.currentlyExpectedBooleanValue);
        if (cur)
          outputValue = cur; // This is constant, but keeping it for readability
        if ((shortcut && outputValue) || i == disjunction.children().size() - 1) {
          boolean outputExpectation = outputExpectationFor(disjunction);
          leaveWithReturnedValue(
              (Evaluable<Object>) disjunction,
              ioEntryForNonLeafWhenEvaluationFinished(outputExpectation, inputActualValue, outputValue), (EvaluationContext<Object>) evaluationContext);
          return;
        }
        i++;
      }
    }

    @Override
    public <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Negation<T> negation) {
      this.enter(EvaluableDesc.fromEvaluable(negation), evaluationContext);
      Object inputActualValue = evaluationContext.value();
      negation.target().accept(evaluationContext, this);
      if (isValueReturned((EvaluationContext<Object>) evaluationContext)) {
        boolean outputActualValue = !this.resultValueAsBoolean((EvaluationContext<Object>) evaluationContext);
        this.leaveWithReturnedValue(
            (Evaluable<Object>) negation,
            ioEntryForNegationWhenValueReturned(negation, inputActualValue, outputActualValue), (EvaluationContext<Object>) evaluationContext);
      } else {
        leaveWithThrownException(
            negation,
            ioEntryWhenExceptionThrown(outputExpectationFor(negation), inputActualValue, evaluationContext.thrownException()), (EvaluationContext<Object>) evaluationContext);
      }
    }

    @Override
    public <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.LeafPred<T> leafPred) {
      this.enter(EvaluableDesc.fromEvaluable(leafPred), evaluationContext);
      try {
        if (isValueReturned(evaluationContext)) {
          T inputActualValue = evaluationContext.returnedValue();
          boolean outputActualValue = leafPred.predicate().test(inputActualValue);
          leave((Evaluable<Object>) leafPred, ioEntryForLeafWhenValueReturned(leafPred, inputActualValue, outputActualValue), (EvaluationContext<Object>) evaluationContext);
        } else if (isExceptionThrown(evaluationContext)) {
          Throwable inputActualValue = evaluationContext.thrownException();
          leave((Evaluable<Object>) leafPred, ioEntryWhenSkipped(leafPred, inputActualValue), (EvaluationContext<Object>) evaluationContext);
        } else
          assert false;
      } catch (Error e) {
        throw e;
      } catch (Throwable e) {
        leaveWithThrownException(leafPred, ioEntryWhenExceptionThrown(outputExpectationFor(leafPred), evaluationContext.value(), e), (EvaluationContext<Object>) evaluationContext);
      }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Func<T> func) {
      this.enter(EvaluableDesc.fromEvaluable(func), evaluationContext);
      try {
        Object outputActualValue;
        if (isValueReturned(evaluationContext)) {
          T inputActualValue = evaluationContext.returnedValue();
          outputActualValue = func.head().apply(inputActualValue);
          leaveWithReturnedValue((Evaluable<Object>) (Evaluable) func, ioEntryForFuncWhenValueReturned(inputActualValue, outputActualValue), (EvaluationContext<Object>) evaluationContext);
          func.tail().ifPresent(tailSide -> ((Evaluable<Object>) tailSide).accept(evaluationContext.valueReturned((T) outputActualValue), this));
        } else if (isExceptionThrown(evaluationContext)) {
          Throwable inputActualValue = evaluationContext.thrownException();
          outputActualValue = inputActualValue;
          leaveWithReturnedValue((Evaluable<Object>) func, ioEntryWhenSkipped(func, inputActualValue), (EvaluationContext<Object>) evaluationContext);
          func.tail().ifPresent(tailSide -> tailSide.accept((EvaluationContext) evaluationContext.exceptionThrown((Throwable) outputActualValue), this));
        } else
          assert false;
      } catch (Error e) {
        throw e;
      } catch (Throwable e) {
        leaveWithThrownException(func, ioEntryWhenExceptionThrown(UNKNOWN, evaluationContext.returnedValue(), e), (EvaluationContext<Object>) evaluationContext);
        func.tail().ifPresent(tailSide -> tailSide.accept((EvaluationContext) ((EvaluationContext<Object>) evaluationContext).exceptionThrown(e), this));
      }
    }

    @Override
    public void evaluate(EvaluationContext<VariableBundle> evaluationContext, Evaluable.VariableBundlePred variableBundlePred) {
      this.enter(EvaluableDesc.fromEvaluable(variableBundlePred), evaluationContext);
      VariableBundle inputActualValue = evaluationContext.returnedValue();
      EvaluationContext<? super Object> evaluationContextForEnclosedPredicate = EvaluationContext.forValue(inputActualValue.valueAt(variableBundlePred.argIndex()));
      variableBundlePred.enclosed().accept(evaluationContextForEnclosedPredicate, this);
      leaveWithReturnedValue(
          (Evaluable<Object>) (Evaluable) variableBundlePred,
          ioEntryForNonLeafWhenEvaluationFinished(
              this.outputExpectationFor(variableBundlePred),
              inputActualValue,
              evaluationContextForEnclosedPredicate.value()),
          (EvaluationContext<Object>) (EvaluationContext) evaluationContext);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, R> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Transformation<T, R> transformation) {
      this.enter(EvaluableDesc.forMapperFromEvaluable(transformation), evaluationContext);
      {
        Object inputActualValue = evaluationContext.returnedValue();
        Evaluable<T> mapperEvaluable = (Evaluable<T>) transformation.mapper();
        mapperEvaluable.accept(evaluationContext, this);
        Object outputActualValueFromMapper = evaluationContext.value();
        if (isValueReturned((EvaluationContext<Object>) evaluationContext)) {
          leaveWithReturnedValue((Evaluable<Object>) mapperEvaluable, ioEntryForTransformingFunctionWhenValueReturned(inputActualValue, evaluationContext.returnedValue(), evaluationContext.returnedValue()), (EvaluationContext<Object>) evaluationContext);
        } else if (isExceptionThrown((EvaluationContext<Object>) evaluationContext))
          leaveWithThrownException(mapperEvaluable, ioEntryForTransformingFunctionWhenSkipped(inputActualValue, evaluationContext.thrownException()), (EvaluationContext<Object>) evaluationContext);
        else
          assert false;
        this.enter(EvaluableDesc.forCheckerFromEvaluable(transformation), evaluationContext);
        {
          Object inputActualValueForChecker = outputActualValueFromMapper;
          Evaluable<? super R> checkerEvaluable = transformation.checker();
          checkerEvaluable.accept((EvaluationContext<R>) evaluationContext, this);
          if (isValueReturned(evaluationContext)) {
            leaveWithReturnedValue(
                (Evaluable<Object>) checkerEvaluable,
                ioEntryForCheckerPredicateWhenValueReturned(checkerEvaluable, inputActualValueForChecker, evaluationContext.returnedValue()),
                (EvaluationContext<Object>) evaluationContext);
          } else if (isExceptionThrown(evaluationContext)) {
            leaveWithReturnedValue((Evaluable<Object>) checkerEvaluable, ioEntryForCheckerPredicateWhenSkipped(inputActualValueForChecker, evaluationContext.value()), (EvaluationContext<Object>) evaluationContext);
          } else
            assert false;
        }
      }
    }

    @Override
    public <E> void evaluate(EvaluationContext<Stream<E>> evaluationContext, Evaluable.StreamPred<E> streamPred) {
      Stream<E> inputActualValue = evaluationContext.returnedValue();
      boolean ret = streamPred.defaultValue();
      this.enter(EvaluableDesc.fromEvaluable(streamPred), evaluationContext);
      // Use NULL_VALUE object instead of null. Otherwise, the operation will fail with NullPointerException
      // on 'findFirst()'.
      // Although NULL_VALUE is an ordinary Object, not a evaluationContext of E, this works
      // because either way we will just return a boolean and during the execution,
      // type information is erased.
      Boolean outputActualValue = inputActualValue
          .filter(createValueCheckingPredicateForStream(streamPred))
          .map(v -> v != null ? v : NULL_VALUE)
          .findFirst()
          .map(each -> !ret)
          .orElse(ret);
      leaveWithReturnedValue(
          (Evaluable) streamPred,
          ioEntryForStreamPredicateWhenValueReturned(streamPred, outputActualValue, evaluationContext.value()),
          (EvaluationContext) evaluationContext);
    }

    @Override
    public boolean resultValueAsBoolean(EvaluationContext<Object> evaluationContext) {
      if (evaluationContext.value() instanceof Boolean)
        return (boolean) evaluationContext.value();
      return false;
    }

    @Override
    public List<EvaluationEntry> resultEntries() {
      return unmodifiableList(this.entries);
    }

    public boolean resultValueAsBooleanIfBooleanOtherwise
        (EvaluationContext<Object> evaluationContext, boolean otherwiseValue) {
      return evaluationContext.value() instanceof Boolean ? resultValueAsBoolean(evaluationContext) : otherwiseValue;
    }

    private <T> EvaluableIo
    ioEntryWhenSkipped(Evaluable<T> evaluable, Throwable inputActualValue) {
      return EvaluableIo.valueReturned(
          inputActualValue,
          outputExpectationFor(evaluable),
          inputActualValue,
          NOT_EVALUATED,
          false);
    }

    private <T> EvaluableIo
    ioEntryForLeafWhenValueReturned(Evaluable.LeafPred<T> leafPred, T inputActualValue,
        boolean outputActualValue) {
      return EvaluableIo.valueReturned(inputActualValue, outputExpectationFor(leafPred), inputActualValue, outputActualValue, this.currentlyExpectedBooleanValue != outputActualValue);
    }


    private static EvaluableIo ioEntryForNonLeafWhenEvaluationFinished(
        boolean outputExpectation, Object inputActualValue, Object
        outputActualValue) {
      return new EvaluableIo(inputActualValue, outputExpectation, inputActualValue, outputActualValue, false);
    }

    private <T> EvaluableIo
    ioEntryForFuncWhenValueReturned(T inputActualValue, Object outputActualValue) {
      return new EvaluableIo(inputActualValue, outputActualValue, inputActualValue, outputActualValue, false);
    }

    private <T> EvaluableIo
    ioEntryForNegationWhenValueReturned(Evaluable.Negation<T> negation, Object inputActualValue,
        boolean outputActualValue) {
      return new EvaluableIo(inputActualValue, outputExpectationFor(negation), inputActualValue, outputActualValue, this.outputExpectationFor(negation) != outputActualValue);
    }

    private <T> EvaluableIo
    ioEntryWhenExceptionThrown(Object outputExpectation, Object inputActualValue, Throwable outputActualValue) {
      return new EvaluableIo(inputActualValue, outputExpectation, inputActualValue, outputActualValue, true);
    }

    private <T> boolean outputExpectationFor
        (Evaluable<T> predicateEvaluable) {
      return predicateEvaluable.requestExpectationFlip() ^ this.currentlyExpectedBooleanValue;
    }

    private static <T> boolean isValueReturned
        (EvaluationContext<T> evaluationContext) {
      return evaluationContext.state() == EvaluationContext.State.VALUE_RETURNED;
    }

    private static <T> boolean isExceptionThrown
        (EvaluationContext<T> evaluationContext) {
      return evaluationContext.state() == EvaluationContext.State.EXCEPTION_THROWN;
    }

    private EvaluableIo ioEntryForCheckerPredicateWhenSkipped(Object
        inputActualValue, Object outputActualValue) {
      return new EvaluableIo(inputActualValue, UNKNOWN, inputActualValue, outputActualValue, false);
    }

    private <T, R> EvaluableIo
    ioEntryForCheckerPredicateWhenValueReturned(Evaluable<? super R> checker, Object inputActualValue, Object outputActualValue) {
      return new EvaluableIo(inputActualValue, outputExpectationFor(checker), inputActualValue, outputActualValue, false);
    }

    private static EvaluableIo ioEntryForTransformingFunctionWhenSkipped
        (Object inputActualValue, Object outputActualValue) {
      return new EvaluableIo(inputActualValue, UNKNOWN, inputActualValue, outputActualValue, false);
    }

    private static EvaluableIo ioEntryForTransformingFunctionWhenValueReturned
        (Object inputActualValue, Object outputActualValue, Object outputExpectation) {
      return new EvaluableIo(inputActualValue, outputExpectation, inputActualValue, outputActualValue, false);
    }

    private <E> EvaluableIo
    ioEntryForStreamPredicateWhenValueReturned(Evaluable.StreamPred<E> streamPred, Boolean outputActualValue, Object inputActualValue) {
      return new EvaluableIo(
          inputActualValue,
          outputExpectationFor(streamPred),
          inputActualValue,
          outputActualValue,
          false);
    }

    private <E> Predicate<E> createValueCheckingPredicateForStream(Evaluable.StreamPred<E> streamPredicate) {
      return e -> {
        Evaluator evaluator = this.copyEvaluator();

        boolean succeeded = false;
        boolean ret = false;
        Object throwable = "<<OUTPUT MISSING>>";
        EvaluationContext<E> evaluationContext = EvaluationContext.forValue(e);
        try {
          streamPredicate.cut().accept(evaluationContext, evaluator);
          succeeded = true;
        } catch (Error error) {
          throw error;
        } catch (Throwable t) {
          throwable = t;
          throw wrapIfNecessary(t);
        } finally {
          if (!succeeded || evaluator.resultValueAsBoolean((EvaluationContext<Object>) evaluationContext) == streamPredicate.valueToCut()) {
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

    static String composeActualValueFromInputAndThrowable(Object
        input, Throwable throwable) {
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

  class EvaluableDesc {
    final EvaluationEntry.Type type;
    final String               formName;
    final boolean              requestsExpectationFlip;
    final boolean              squashable;

    public EvaluableDesc(EvaluationEntry.Type type, String formName, boolean requestsExpectationFlip, boolean squashable) {
      this.type = type;
      this.formName = formName;
      this.requestsExpectationFlip = requestsExpectationFlip;
      this.squashable = squashable;
    }

    public EvaluationEntry.Type type() {
      return this.type;
    }

    public boolean isSquashable() {
      return this.squashable;
    }

    public boolean requestExpectationFlip() {
      return this.requestsExpectationFlip;
    }

    static <T> EvaluableDesc fromEvaluable(Evaluable.LeafPred<T> leafEvaluable) {
      return new EvaluableDesc(
          LEAF,
          String.format("%s", leafEvaluable),
          leafEvaluable.requestExpectationFlip(),
          leafEvaluable.isSquashable()
      );
    }

    static EvaluableDesc fromEvaluable(Evaluable.VariableBundlePred contextEvaluable) {
      return new EvaluableDesc(
          LEAF,
          String.format("%s", contextEvaluable),
          contextEvaluable.requestExpectationFlip(),
          contextEvaluable.isSquashable()
      );
    }

    static <T> EvaluableDesc fromEvaluable(Evaluable.StreamPred<T> streamEvaluable) {
      return new EvaluableDesc(
          LEAF,
          String.format("%s", streamEvaluable),
          streamEvaluable.requestExpectationFlip(),
          streamEvaluable.isSquashable()
      );
    }

    static <T> EvaluableDesc fromEvaluable(Evaluable.Func<T> funcEvaluable) {
      return new EvaluableDesc(
          FUNCTION,
          String.format("%s", funcEvaluable.head()),
          funcEvaluable.requestExpectationFlip(),
          funcEvaluable.isSquashable()
      );
    }

    static <T> EvaluableDesc fromEvaluable(Evaluable.Conjunction<T> conjunctionEvaluable) {
      return new EvaluableDesc(
          FUNCTION,
          String.format("%s", conjunctionEvaluable.shortcut() ? "and" : "allOf"),
          conjunctionEvaluable.requestExpectationFlip(),
          conjunctionEvaluable.isSquashable()
      );
    }

    static <T> EvaluableDesc fromEvaluable(Evaluable.Disjunction<T> disjunctionEvaluable) {
      return new EvaluableDesc(
          FUNCTION,
          String.format("%s", disjunctionEvaluable.shortcut() ? "or" : "anyOf"),
          disjunctionEvaluable.requestExpectationFlip(),
          disjunctionEvaluable.isSquashable()
      );
    }

    static <T> EvaluableDesc fromEvaluable(Evaluable.Negation<T> negationEvaluable) {
      return new EvaluableDesc(
          FUNCTION,
          "not",
          negationEvaluable.requestExpectationFlip(),
          true
      );
    }

    static <T, R> EvaluableDesc forMapperFromEvaluable(Evaluable.Transformation<T, R> transformationEvaluable) {
      return new EvaluableDesc(
          TRANSFORM,
          transformationEvaluable.mapperName().orElse("transform"),
          transformationEvaluable.mapper().requestExpectationFlip(),
          true
      );
    }

    static <T, R> EvaluableDesc forCheckerFromEvaluable(Evaluable.Transformation<T, R> transformationEvaluable) {
      return new EvaluableDesc(
          CHECK,
          transformationEvaluable.checkerName().orElse("check"),
          transformationEvaluable.checker().requestExpectationFlip(),
          true);
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

  class EvaluableIo {
    enum Type {
      EXCEPTION_THROWN {
        @Override
        void finishEvaluationContext(EvaluationContext<Object> evaluationContext, Object outputActualValue) {
          evaluationContext.exceptionThrown((Throwable) outputActualValue);
        }

        @Override
        EvaluationEntry.Finalized finalizeEvaluationEntry(EvaluationEntry.OnGoing evaluationEntry, EvaluableIo io, Evaluable<Object> evaluable) {
          return evaluationEntry.finalizeEntry(
              io.getInputExpectation(), (Snapshottable) io::getInputExpectation,
              io.getOutputExpectation(), explainOutputExpectation(evaluable),
              io.getInputActualValue(), explainInputActualValue(evaluable, io.getInputActualValue()),
              io.getOutputActualValue(),
              composeActualValueFromInputAndThrowable(io.getInputActualValue(), (Throwable) io.getOutputActualValue()),
              true);
        }
      },
      VALUE_RETURNED {
        @Override
        void finishEvaluationContext(EvaluationContext<Object> evaluationContext, Object outputActualValue) {
          evaluationContext.valueReturned(outputActualValue);
        }

        @Override
        EvaluationEntry.Finalized finalizeEvaluationEntry(EvaluationEntry.OnGoing evaluationEntry, EvaluableIo io, Evaluable<Object> evaluable) {
          return evaluationEntry.finalizeEntry(
              io.getInputExpectation(), io.getInputExpectation(),
              io.getOutputExpectation(), explainOutputExpectation(evaluable),
              io.getInputActualValue(), explainInputActualValue(evaluable, io.getInputActualValue()),
              io.getOutputActualValue(),
              toSnapshotIfPossible(io.getInputActualValue()),
              io.requiresExplanation());
        }
      };

      abstract void finishEvaluationContext(EvaluationContext<Object> evaluationContext, Object outputActualValue);

      abstract EvaluationEntry.Finalized finalizeEvaluationEntry(EvaluationEntry.OnGoing evaluationEntry, EvaluableIo io, Evaluable<Object> evaluable);
    }

    public final  Type    type;
    private final Object  inputExpectation;
    private final Object  outputExpectation;
    private final Object  inputActualValue;
    private final Object  outputActualValue;
    private final boolean requiresExplanation;

    public EvaluableIo(Type type, Object inputExpectation, Object outputExpectation, Object inputActualValue, Object outputActualValue, boolean requiresExplanation) {
      this.type = type;
      this.inputExpectation = inputExpectation;
      this.outputExpectation = outputExpectation;
      this.inputActualValue = inputActualValue;
      this.outputActualValue = outputActualValue;
      this.requiresExplanation = requiresExplanation;
    }

    public EvaluableIo(Object inputExpectation, Object outputExpectation, Object inputActualValue, Object outputActualValue, boolean requiresExplanation) {
      this(Type.VALUE_RETURNED, inputExpectation, outputExpectation, inputActualValue, outputActualValue, requiresExplanation);
    }

    public Object getInputExpectation() {
      return inputExpectation;
    }

    public Object getOutputExpectation() {
      return outputExpectation;
    }

    public Object getInputActualValue() {
      return inputActualValue;
    }

    public Object getOutputActualValue() {
      return outputActualValue;
    }

    public boolean requiresExplanation() {
      return requiresExplanation;
    }

    static EvaluableIo valueReturned(Object inputExpectation, Object outputExpectation, Object inputActualValue, Object outputActualValue, boolean requiresExplanation) {
      return new EvaluableIo(Type.VALUE_RETURNED, inputExpectation, outputExpectation, inputActualValue, outputActualValue, requiresExplanation);
    }

    static EvaluableIo exceptionThrown(Object inputExpectation, Object outputExpectation, Object inputActualValue, Object outputActualValue, boolean requiresExplanation) {
      return new EvaluableIo(Type.EXCEPTION_THROWN, inputExpectation, outputExpectation, inputActualValue, outputActualValue, requiresExplanation);
    }
  }
}