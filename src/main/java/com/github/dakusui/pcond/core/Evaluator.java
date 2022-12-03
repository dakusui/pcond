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
  <T> void evaluate(EvaluationResultHolder<? extends T> value, Evaluable.Conjunction<T> conjunction);

  /**
   * Evaluates `value` with a `disjunction` predicate ("or").
   *
   * @param value       A value to be evaluated.
   * @param disjunction A disjunction predicate with which `value` is evaluated.
   * @param <T>         The type of the `value`.
   * @see com.github.dakusui.pcond.core.Evaluable.Disjunction
   */
  <T> void evaluate(EvaluationResultHolder<T> value, Evaluable.Disjunction<T> disjunction);

  /**
   * Evaluates `value` with a `negation` predicate ("not").
   *
   * @param value    A value to be evaluated.
   * @param negation A negation predicate with which `value` is evaluated.
   * @param <T>      The type of the `value`.
   * @see com.github.dakusui.pcond.core.Evaluable.Negation
   */
  <T> void evaluate(EvaluationResultHolder<T> value, Evaluable.Negation<T> negation);

  /**
   * Evaluates `value` with a leaf predicate.
   *
   * @param value    A value to be evaluated.
   * @param leafPred A predicate with which `value` is evaluated.
   * @param <T>      The type of the `value`.
   * @see com.github.dakusui.pcond.core.Evaluable.LeafPred
   */
  <T> void evaluate(EvaluationResultHolder<T> value, Evaluable.LeafPred<T> leafPred);

  /**
   * Evaluates `value` with a context predicate.
   *
   * @param value              A value to be evaluated.
   * @param variableBundlePred A predicate with which `value` is evaluated.
   * @see Evaluable.VariableBundlePred
   */
  void evaluate(EvaluationResultHolder<VariableBundle> value, Evaluable.VariableBundlePred variableBundlePred);

  /**
   * Evaluates `value` with a "transformatioin" predicate.
   *
   * @param value          A value to be evaluated.
   * @param transformation A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Transformation
   */
  <T, R> void evaluate(EvaluationResultHolder<T> value, Evaluable.Transformation<T, R> transformation);

  /**
   * Evaluates `value` with a "function" predicate.
   *
   * @param value A value to be evaluated.
   * @param func  A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Func
   */
  <T> void evaluate(EvaluationResultHolder<T> value, Evaluable.Func<T> func);

  /**
   * Evaluates `value` with a predicate for a stream.
   *
   * @param value      A value to be evaluated.
   * @param streamPred A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.StreamPred
   */
  <E> void evaluate(EvaluationResultHolder<Stream<E>> value, Evaluable.StreamPred<E> streamPred);

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

    void enter(EvaluableDesc evaluableDesc, EvaluationResultHolder<?> evaluationResultHolder) {
      EvaluationEntry.OnGoing newEntry = new EvaluationEntry.OnGoing(
          evaluableDesc.formName, evaluableDesc.type(), (int) onGoingEntries.stream().filter(each -> !each.isSquashable()).count(),
          evaluationResultHolder.value(), toSnapshotIfPossible(evaluationResultHolder.value()),
          this.currentlyExpectedBooleanValue, toSnapshotIfPossible(this.currentlyExpectedBooleanValue),
          evaluationResultHolder, toSnapshotIfPossible(evaluationResultHolder.value()),
          evaluableDesc.isSquashable(), entries.size()
      );
      this.onGoingEntries.add(newEntry);
      this.entries.add(newEntry);
      if (evaluableDesc.requestExpectationFlip())
        this.flipCurrentlyExpectedBooleanValue();
    }

    <T> void leave(
        Evaluable<T> evaluable,
        EvaluableIo io,
        EvaluationResultHolder<Object> evaluationResultHolder) {
      int positionInOngoingEntries = onGoingEntries.size() - 1;
      EvaluationEntry.OnGoing current = onGoingEntries.get(positionInOngoingEntries);
      this.entries.set(
          current.positionInEntries,
          io.type.finalizeEvaluationEntry(current, io, (Evaluable<Object>) evaluable));
      io.type.finishEvaluationContext(evaluationResultHolder, io.getOutputActualValue());
      this.onGoingEntries.remove(positionInOngoingEntries);
      if (evaluable.requestExpectationFlip())
        this.flipCurrentlyExpectedBooleanValue();
    }

    void flipCurrentlyExpectedBooleanValue() {
      this.currentlyExpectedBooleanValue = !this.currentlyExpectedBooleanValue;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public <T> void evaluate(EvaluationResultHolder<? extends T> evaluationResultHolder, Evaluable.Conjunction<T> conjunction) {
      int i = 0;
      boolean outputValue = true;
      boolean shortcut = conjunction.shortcut();
      this.enter(EvaluableDesc.fromEvaluable(conjunction), evaluationResultHolder);
      Object inputActualValue = evaluationResultHolder.value();
      for (Evaluable<? super T> each : conjunction.children()) {
        @SuppressWarnings("unchecked") EvaluationResultHolder<Object> clonedContext = (EvaluationResultHolder<Object>) evaluationResultHolder.clone();
        each.accept((EvaluationResultHolder) clonedContext, this);
        boolean cur = this.resultValueAsBooleanIfBooleanOtherwise(clonedContext, !this.currentlyExpectedBooleanValue);
        if (!cur)
          outputValue = cur; // This is constant, but keeping it for readability
        if ((shortcut && !outputValue) || i == conjunction.children().size() - 1) {
          boolean outputExpectation = outputExpectationFor(conjunction);
          leave(
              (Evaluable<Object>) conjunction,
              ioEntryForNonLeafWhenEvaluationFinished(outputExpectation, inputActualValue, outputValue), (EvaluationResultHolder<Object>) evaluationResultHolder);
          return;
        }
        i++;
      }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public <T> void evaluate(EvaluationResultHolder<T> evaluationResultHolder, Evaluable.Disjunction<T> disjunction) {
      int i = 0;
      boolean outputValue = false;
      boolean shortcut = disjunction.shortcut();
      this.enter(EvaluableDesc.fromEvaluable(disjunction), evaluationResultHolder);
      Object inputActualValue = evaluationResultHolder.value();
      for (Evaluable<? super T> each : disjunction.children()) {
        @SuppressWarnings("unchecked") EvaluationResultHolder<Object> clonedContext = (EvaluationResultHolder<Object>) evaluationResultHolder.clone();
        each.accept((EvaluationResultHolder) clonedContext, this);
        boolean cur = this.resultValueAsBooleanIfBooleanOtherwise(clonedContext, !this.currentlyExpectedBooleanValue);
        if (cur)
          outputValue = cur; // This is constant, but keeping it for readability
        if ((shortcut && outputValue) || i == disjunction.children().size() - 1) {
          boolean outputExpectation = outputExpectationFor(disjunction);
          leave(
              (Evaluable<Object>) disjunction,
              ioEntryForNonLeafWhenEvaluationFinished(outputExpectation, inputActualValue, outputValue), (EvaluationResultHolder<Object>) evaluationResultHolder);
          return;
        }
        i++;
      }
    }

    @Override
    public <T> void evaluate(EvaluationResultHolder<T> evaluationResultHolder, Evaluable.Negation<T> negation) {
      this.enter(EvaluableDesc.fromEvaluable(negation), evaluationResultHolder);
      Object inputActualValue = evaluationResultHolder.value();
      negation.target().accept(evaluationResultHolder, this);
      if (isValueReturned((EvaluationResultHolder<Object>) evaluationResultHolder)) {
        boolean outputActualValue = !this.resultValueAsBoolean((EvaluationResultHolder<Object>) evaluationResultHolder);
        this.leave(
            (Evaluable<Object>) negation,
            ioEntryForNegationWhenValueReturned(this.outputExpectationFor(negation), inputActualValue, outputActualValue), (EvaluationResultHolder<Object>) evaluationResultHolder);
      } else {
        leave(
            negation,
            ioEntryWhenExceptionThrown(outputExpectationFor(negation), inputActualValue, evaluationResultHolder.thrownException()), (EvaluationResultHolder<Object>) evaluationResultHolder);
      }
    }

    @Override
    public <T> void evaluate(EvaluationResultHolder<T> evaluationResultHolder, Evaluable.LeafPred<T> leafPred) {
      this.enter(EvaluableDesc.fromEvaluable(leafPred), evaluationResultHolder);
      EvaluableIo io = null;
      try {
        if (isValueReturned(evaluationResultHolder)) {
          T inputActualValue = evaluationResultHolder.returnedValue();
          boolean outputActualValue = leafPred.predicate().test(inputActualValue);
          io = ioEntryForLeafWhenValueReturned(outputExpectationFor(leafPred), inputActualValue, outputActualValue);
        } else if (isExceptionThrown(evaluationResultHolder)) {
          Throwable inputActualValue = evaluationResultHolder.thrownException();
          io = ioEntryWhenSkipped(leafPred, inputActualValue);
        } else
          assert false;
      } catch (Error e) {
        throw e;
      } catch (Throwable e) {
        io = ioEntryWhenExceptionThrown(outputExpectationFor(leafPred), evaluationResultHolder.value(), e);
      } finally {
        leave((Evaluable<Object>) leafPred, io, (EvaluationResultHolder<Object>) evaluationResultHolder);
      }
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> void evaluate(EvaluationResultHolder<T> evaluationResultHolder, Evaluable.Func<T> func) {
      this.enter(EvaluableDesc.fromEvaluable(func), evaluationResultHolder);
      try {
        Object outputActualValue;
        if (isValueReturned(evaluationResultHolder)) {
          T inputActualValue = evaluationResultHolder.returnedValue();
          outputActualValue = func.head().apply(inputActualValue);
          EvaluableIo io = ioEntryForFuncWhenValueReturned(inputActualValue, outputActualValue);
          leave((Evaluable<Object>) func, io, (EvaluationResultHolder<Object>) evaluationResultHolder);
          func.tail().ifPresent(tailSide -> ((Evaluable<Object>) tailSide).accept(evaluationResultHolder.valueReturned((T) outputActualValue), this));
        } else if (isExceptionThrown(evaluationResultHolder)) {
          Throwable inputActualValue = evaluationResultHolder.thrownException();
          outputActualValue = inputActualValue;
          EvaluableIo io = ioEntryWhenSkipped(func, inputActualValue);
          leave((Evaluable<Object>) func, io, (EvaluationResultHolder<Object>) evaluationResultHolder);
          func.tail().ifPresent(tailSide -> ((Evaluable<Object>) tailSide).accept(evaluationResultHolder.exceptionThrown((Throwable) outputActualValue), this));
        } else
          assert false;
      } catch (Error e) {
        throw e;
      } catch (Throwable e) {
        EvaluableIo io = ioEntryWhenExceptionThrown(UNKNOWN, evaluationResultHolder.returnedValue(), e);
        leave(func, io, (EvaluationResultHolder<Object>) evaluationResultHolder);
        func.tail().ifPresent(tailSide -> {
          ((Evaluable<Object>) tailSide).accept(((EvaluationResultHolder<Object>) evaluationResultHolder).exceptionThrown(e), this);
        });
      }
    }

    @Override
    public void evaluate(EvaluationResultHolder<VariableBundle> evaluationResultHolder, Evaluable.VariableBundlePred variableBundlePred) {
      this.enter(EvaluableDesc.fromEvaluable(variableBundlePred), evaluationResultHolder);
      VariableBundle inputActualValue = evaluationResultHolder.returnedValue();
      EvaluationResultHolder<? super Object> evaluationResultHolderForEnclosedPredicate = EvaluationResultHolder.forValue(inputActualValue.valueAt(variableBundlePred.argIndex()));
      variableBundlePred.enclosed().accept(evaluationResultHolderForEnclosedPredicate, this);
      leave(
          (Evaluable<Object>) (Evaluable) variableBundlePred,
          ioEntryForNonLeafWhenEvaluationFinished(
              this.outputExpectationFor(variableBundlePred),
              inputActualValue,
              evaluationResultHolderForEnclosedPredicate.value()),
          (EvaluationResultHolder<Object>) (EvaluationResultHolder) evaluationResultHolder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, R> void evaluate(EvaluationResultHolder<T> evaluationResultHolder, Evaluable.Transformation<T, R> transformation) {
      this.enter(EvaluableDesc.forMapperFromEvaluable(transformation), evaluationResultHolder);
      {
        Object inputActualValue = evaluationResultHolder.returnedValue();
        Evaluable<T> mapperEvaluable = (Evaluable<T>) transformation.mapper();
        mapperEvaluable.accept(evaluationResultHolder, this);
        Object outputActualValueFromMapper = evaluationResultHolder.value();
        if (isValueReturned((EvaluationResultHolder<Object>) evaluationResultHolder)) {
          leave((Evaluable<Object>) mapperEvaluable, ioEntryWhenValueReturned(evaluationResultHolder.returnedValue(), inputActualValue, evaluationResultHolder.returnedValue()), (EvaluationResultHolder<Object>) evaluationResultHolder);
        } else if (isExceptionThrown((EvaluationResultHolder<Object>) evaluationResultHolder))
          leave(mapperEvaluable, ioEntryWhenSkipped(inputActualValue, evaluationResultHolder.thrownException()), (EvaluationResultHolder<Object>) evaluationResultHolder);
        else
          assert false;
        this.enter(EvaluableDesc.forCheckerFromEvaluable(transformation), evaluationResultHolder);
        {
          Object inputActualValueForChecker = outputActualValueFromMapper;
          Evaluable<? super R> checkerEvaluable = transformation.checker();
          checkerEvaluable.accept((EvaluationResultHolder<R>) evaluationResultHolder, this);
          if (isValueReturned(evaluationResultHolder)) {
            leave(
                (Evaluable<Object>) checkerEvaluable,
                ioEntryForCheckerPredicateWhenValueReturned(inputActualValueForChecker, evaluationResultHolder.returnedValue(), outputExpectationFor(checkerEvaluable)),
                (EvaluationResultHolder<Object>) evaluationResultHolder);
          } else if (isExceptionThrown(evaluationResultHolder)) {
            leave((Evaluable<Object>) checkerEvaluable, ioEntryWhenSkipped(inputActualValueForChecker, evaluationResultHolder.thrownException()), (EvaluationResultHolder<Object>) evaluationResultHolder);
          } else
            assert false;
        }
      }
    }

    @Override
    public <E> void evaluate(EvaluationResultHolder<Stream<E>> evaluationResultHolder, Evaluable.StreamPred<E> streamPred) {
      Stream<E> inputActualValue = evaluationResultHolder.returnedValue();
      boolean ret = streamPred.defaultValue();
      this.enter(EvaluableDesc.fromEvaluable(streamPred), evaluationResultHolder);
      // Use NULL_VALUE object instead of null. Otherwise, the operation will fail with NullPointerException
      // on 'findFirst()'.
      // Although NULL_VALUE is an ordinary Object, not an evaluationContext of E, this works
      // because either way we will just return a boolean and during the execution,
      // type information is erased.
      Boolean outputActualValue = inputActualValue
          .filter(createValueCheckingPredicateForStream(streamPred))
          .map(v -> v != null ? v : NULL_VALUE)
          .findFirst()
          .map(each -> !ret)
          .orElse(ret);
      leave(
          (Evaluable) streamPred,
          ioEntryWhenValueReturned(outputExpectationFor(streamPred), inputActualValue, outputActualValue),
          (EvaluationResultHolder) evaluationResultHolder);
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

    public boolean resultValueAsBooleanIfBooleanOtherwise
        (EvaluationResultHolder<Object> evaluationResultHolder, boolean otherwiseValue) {
      return evaluationResultHolder.value() instanceof Boolean ? resultValueAsBoolean(evaluationResultHolder) : otherwiseValue;
    }

    private <T> EvaluableIo ioEntryWhenSkipped(Evaluable<T> evaluable, Throwable inputActualValue) {
      return EvaluableIo.valueReturned(
          inputActualValue,
          outputExpectationFor(evaluable),
          inputActualValue,
          NOT_EVALUATED,
          false);
    }


    private static EvaluableIo ioEntryForNonLeafWhenEvaluationFinished(boolean outputExpectation, Object inputActualValue, Object outputActualValue) {
      return new EvaluableIo(inputActualValue, outputExpectation, inputActualValue, outputActualValue, false);
    }

    private <T> EvaluableIo ioEntryForFuncWhenValueReturned(T inputActualValue, Object outputActualValue) {
      return new EvaluableIo(inputActualValue, outputActualValue, inputActualValue, outputActualValue, false);
    }

    private <T> EvaluableIo ioEntryForLeafWhenValueReturned(boolean outputExpectation, T inputActualValue, boolean outputActualValue) {
      return EvaluableIo.valueReturned(inputActualValue, outputExpectation, inputActualValue, outputActualValue, this.currentlyExpectedBooleanValue != outputActualValue);
    }

    private <T> EvaluableIo ioEntryForNegationWhenValueReturned(boolean outputExpectation, Object inputActualValue, boolean outputActualValue) {
      return new EvaluableIo(inputActualValue, outputExpectation, inputActualValue, outputActualValue, outputExpectation != outputActualValue);
    }

    private <T> EvaluableIo ioEntryWhenExceptionThrown(Object outputExpectation, Object inputActualValue, Throwable outputActualValue) {
      return EvaluableIo.exceptionThrown(inputActualValue, outputExpectation, inputActualValue, outputActualValue, true);
    }

    private <T> boolean outputExpectationFor(Evaluable<T> predicateEvaluable) {
      return predicateEvaluable.requestExpectationFlip() ^ this.currentlyExpectedBooleanValue;
    }

    private static <T> boolean isValueReturned
        (EvaluationResultHolder<T> evaluationResultHolder) {
      return evaluationResultHolder.state() == EvaluationResultHolder.State.VALUE_RETURNED;
    }

    private static <T> boolean isExceptionThrown
        (EvaluationResultHolder<T> evaluationResultHolder) {
      return evaluationResultHolder.state() == EvaluationResultHolder.State.EXCEPTION_THROWN;
    }

    private <T, R> EvaluableIo
    ioEntryForCheckerPredicateWhenValueReturned(Object inputActualValue, Object outputActualValue, boolean outputExpectation) {
      return EvaluableIo.valueReturned(inputActualValue, outputExpectation, inputActualValue, outputActualValue, false);
    }

    private static EvaluableIo ioEntryWhenValueReturned(Object outputExpectation, Object inputActualValue, Object outputActualValue) {
      return EvaluableIo.valueReturned(inputActualValue, outputExpectation, inputActualValue, outputActualValue, false);
    }

    private EvaluableIo ioEntryWhenSkipped(Object inputActualValue, Throwable outputActualValue) {
      return EvaluableIo.exceptionThrown(inputActualValue, UNKNOWN, inputActualValue, outputActualValue, false);
    }

    private <E> Predicate<E> createValueCheckingPredicateForStream(Evaluable.StreamPred<E> streamPredicate) {
      return e -> {
        Evaluator evaluator = this.copyEvaluator();

        boolean succeeded = false;
        boolean ret = false;
        Object throwable = "<<OUTPUT MISSING>>";
        EvaluationResultHolder<E> evaluationResultHolder = EvaluationResultHolder.forValue(e);
        try {
          streamPredicate.cut().accept(evaluationResultHolder, evaluator);
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
        void finishEvaluationContext(EvaluationResultHolder<Object> evaluationResultHolder, Object outputActualValue) {
          evaluationResultHolder.exceptionThrown((Throwable) outputActualValue);
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
        void finishEvaluationContext(EvaluationResultHolder<Object> evaluationResultHolder, Object outputActualValue) {
          evaluationResultHolder.valueReturned(outputActualValue);
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

      abstract void finishEvaluationContext(EvaluationResultHolder<Object> evaluationResultHolder, Object outputActualValue);

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