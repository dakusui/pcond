package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.core.context.VariableBundle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.EvaluationEntry.Type.*;
import static com.github.dakusui.pcond.core.Evaluator.Explainable.explainInputActualValue;
import static com.github.dakusui.pcond.core.Evaluator.Explainable.explainOutputExpectation;
import static com.github.dakusui.pcond.core.Evaluator.Snapshottable.toSnapshotIfPossible;
import static com.github.dakusui.pcond.internals.InternalUtils.*;
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
   * @param value       A value to be evaluated.
   * @param contextPred A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.ContextPred
   */
  void evaluate(EvaluationContext<VariableBundle> value, Evaluable.ContextPred contextPred);

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

  /**
   * The last evaluated value by an `evaluate` method defined in this interface.
   * This method is expected to be called from inside an `evaluated` method, not
   * to be called by a user.
   *
   * @return The evaluated result value.
   */
  Object resultValue();

  boolean resultValueAsBoolean();

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
    public static final  Object NOT_AVAILABLE = new Object() {
      @Override
      public String toString() {
        return "(not available)";
      }
    };
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
    List<EvaluationEntry.OnGoing> onGoingEntries = new LinkedList<>();
    List<EvaluationEntry>         entries        = new ArrayList<>();
    final EvaluationContext<Object> currentEvaluationContext;
    boolean currentlyExpectedBooleanValue = true;

    public Impl() {
      this.currentEvaluationContext = EvaluationContext.forValue(null);
    }

    void enter(EvaluableDesc evaluableDesc, EvaluationContext<?> input) {
      EvaluationEntry.OnGoing newEntry = new EvaluationEntry.OnGoing(
          evaluableDesc.formName, evaluableDesc.type(), (int) onGoingEntries.stream().filter(each -> !each.isSquashable()).count(),
          input.currentValue(), toSnapshotIfPossible(input.currentValue()),
          this.currentlyExpectedBooleanValue, toSnapshotIfPossible(this.currentlyExpectedBooleanValue),
          input, toSnapshotIfPossible(input.currentValue()),
          evaluableDesc.isSquashable(), entries.size()
      );
      onGoingEntries.add(newEntry);
      entries.add(newEntry);
      if (evaluableDesc.requestExpectationFlip())
        this.flipCurrentlyExpectedBooleanValue();
    }

    void leaveWithReturnedValue(
        Evaluable<?> evaluable,
        Object inputExpectation,
        Object outputExpectation,
        Object inputActualValue,
        Object outputActualValue,
        boolean unexpected) {
      int positionInOngoingEntries = onGoingEntries.size() - 1;
      EvaluationEntry.OnGoing current = onGoingEntries.get(positionInOngoingEntries);
      entries.set(
          current.positionInEntries,
          current.finalizeEntry(
              // evaluable
              inputExpectation, inputExpectation,
              outputExpectation, explainOutputExpectation(evaluable),
              inputActualValue, explainInputActualValue(evaluable, inputActualValue),
              outputActualValue,
              toSnapshotIfPossible(inputActualValue), false, unexpected));
      onGoingEntries.remove(positionInOngoingEntries);
      this.currentEvaluationContext.valueReturned(outputActualValue);
      if (evaluable.requestExpectationFlip())
        this.flipCurrentlyExpectedBooleanValue();
    }

    void leaveWithThrownException(
        Evaluable<?> evaluable,
        Object inputExpectation,
        Object outputExpectation,
        Object inputActualValue,
        Throwable thrownException) {
      int positionInOngoingEntries = onGoingEntries.size() - 1;
      EvaluationEntry.OnGoing current = onGoingEntries.get(positionInOngoingEntries);
      entries.set(
          current.positionInEntries,
          current.finalizeEntry(
              inputExpectation, (Snapshottable) () -> inputExpectation,
              outputExpectation, explainOutputExpectation(evaluable),
              inputActualValue, explainInputActualValue(evaluable, inputActualValue),
              thrownException,
              composeActualValueFromInputAndThrowable(inputActualValue, thrownException), true, true));
      onGoingEntries.remove(positionInOngoingEntries);
      this.currentEvaluationContext.exceptionThrown(thrownException);
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
      boolean actualOutputValue = true;
      Throwable thrownException = null;
      boolean shortcut = conjunction.shortcut();
      EvaluationContext<Object> clonedContext = (EvaluationContext<Object>) evaluationContext.clone();
      for (Evaluable<? super T> each : conjunction.children()) {
        this.currentEvaluationContext().resetTo(clonedContext);
        if (i == 0)
          this.enter(EvaluableDesc.fromEvaluable(conjunction), evaluationContext);
        each.accept(evaluationContext, this);
        boolean cur = this.resultValueAsBooleanIfBooleanOtherwise(!this.currentlyExpectedBooleanValue);
        if (clonedContext.state() == EvaluationContext.State.EXCEPTION_THROWN && thrownException == null) {
          thrownException = clonedContext.thrownException();
        }
        if (!cur)
          actualOutputValue = cur; // This is constant, but keeping it for readability
        if ((shortcut && !actualOutputValue) || i == conjunction.children().size() - 1) {
          boolean outputExpectation = conjunction.requestExpectationFlip() ^ this.currentlyExpectedBooleanValue;
          this.leaveWithReturnedValue(
              conjunction,
              clonedContext.value(),
              outputExpectation,
              clonedContext.value(),
              actualOutputValue,
              outputExpectation == actualOutputValue
          );
          return;
        }
        i++;
      }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Disjunction<T> disjunction) {
      int i = 0;
      boolean actualOutputValue = false;
      boolean shortcut = disjunction.shortcut();
      Object currentValue = this.currentEvaluationContext().currentValue();
      EvaluationContext<Object> clonedContext = (EvaluationContext<Object>) evaluationContext.clone();
      for (Evaluable<? super T> each : disjunction.children()) {
        this.currentEvaluationContext().valueReturned(currentValue);
        if (i == 0)
          this.enter(EvaluableDesc.fromEvaluable(disjunction), evaluationContext);
        Object inputActualValue = evaluationContext.value();
        each.accept(evaluationContext, this);
        boolean cur = this.resultValueAsBoolean();
        if (cur)
          actualOutputValue = cur; // This is constant, but keeping it for readability
        if ((shortcut && actualOutputValue) || i == disjunction.children().size() - 1) {
          boolean outputExpectation = disjunction.requestExpectationFlip() ^ this.currentlyExpectedBooleanValue;
          this.leaveWithReturnedValue(
              disjunction,
              clonedContext.value(),
              outputExpectation,
              clonedContext.value(),
              clonedContext.state() == EvaluationContext.State.EXCEPTION_THROWN ? NOT_AVAILABLE : actualOutputValue,
              outputExpectation == actualOutputValue
          );
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
      this.leaveWithReturnedValue(negation, inputActualValue, negation.requestExpectationFlip() ^ this.currentlyExpectedBooleanValue, inputActualValue, !this.resultValueAsBoolean(), false);
    }

    @Override
    public <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.LeafPred<T> leafPred) {
      this.enter(EvaluableDesc.fromEvaluable(leafPred), evaluationContext);
      try {
        System.out.println("LEAF:<" + evaluationContext + ">");
        if (evaluationContext.state() == EvaluationContext.State.VALUE_RETURNED) {
          T inputActualValue = evaluationContext.returnedValue();
          boolean result = leafPred.predicate().test(inputActualValue);
          this.leaveWithReturnedValue(leafPred, inputActualValue, leafPred.requestExpectationFlip() ^ this.currentlyExpectedBooleanValue, inputActualValue, result, this.currentlyExpectedBooleanValue != result);
        } else {
          Object inputActualValue = evaluationContext.thrownException();
          this.leaveWithReturnedValue(leafPred, inputActualValue, leafPred.requestExpectationFlip() ^ this.currentlyExpectedBooleanValue, inputActualValue, NOT_EVALUATED, false);
        }
      } catch (Error e) {
        throw e;
      } catch (Throwable e) {
        this.leaveWithThrownException(leafPred, evaluationContext.value(), leafPred.requestExpectationFlip() ^ this.currentlyExpectedBooleanValue, evaluationContext.value(), e);
      }
    }

    @Override
    public void evaluate(EvaluationContext<VariableBundle> evaluationContext, Evaluable.ContextPred contextPred) {
      this.enter(EvaluableDesc.fromEvaluable(contextPred), evaluationContext);
      // TODO: Issue-#59: Need exception handling
      VariableBundle inputActualValue = evaluationContext.returnedValue();
      contextPred.enclosed().accept(EvaluationContext.forValue(inputActualValue.valueAt(contextPred.argIndex())), this);
      this.leaveWithReturnedValue(contextPred, inputActualValue, contextPred.requestExpectationFlip() ^ this.currentlyExpectedBooleanValue, inputActualValue, this.resultValue(), false);
    }

    @Override
    public <T, R> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Transformation<T, R> transformation) {
      if (isDummyFunction((Function<?, ?>) transformation.mapper())) {
        transformation.checker().accept(currentEvaluationContext(), this);
        return;
      }
      this.enter(EvaluableDesc.forMapperFromEvaluable(transformation), evaluationContext);
      Object inputActualValue = evaluationContext.returnedValue();
      transformation.mapper().accept(evaluationContext, this);
      Evaluable<?> evaluable = transformation.checker();
      EvaluationContext<?> currentEvaluationContext = this.currentEvaluationContext().clone();
      if (currentEvaluationContext.state() == EvaluationContext.State.VALUE_RETURNED) {
        this.leaveWithReturnedValue(
            evaluable,
            inputActualValue,
            currentEvaluationContext.currentValue(),
            inputActualValue,
            currentEvaluationContext.value(),
            false);
      } else if (currentEvaluationContext.state() == EvaluationContext.State.EXCEPTION_THROWN)
        this.leaveWithThrownException(
            evaluable,
            inputActualValue,
            UNKNOWN,
            inputActualValue,
            currentEvaluationContext.thrownException());
      this.enter(EvaluableDesc.forCheckerFromEvaluable(transformation), currentEvaluationContext());
      transformation.checker().accept((EvaluationContext<R>) currentEvaluationContext, this);
      if (currentEvaluationContext.state() == EvaluationContext.State.VALUE_RETURNED) {
        this.leaveWithReturnedValue(
            transformation.checker(),
            currentEvaluationContext.value(),
            transformation.mapper().requestExpectationFlip() ^ this.currentlyExpectedBooleanValue,
            currentEvaluationContext.value(),
            this.currentEvaluationContext().value(), false);
      } else if (currentEvaluationContext.state() == EvaluationContext.State.EXCEPTION_THROWN) {
        this.leaveWithReturnedValue(
            transformation.checker(),
            currentEvaluationContext.value(),
            transformation.mapper().requestExpectationFlip() ^ this.currentlyExpectedBooleanValue,
            currentEvaluationContext.value(),
            this.currentEvaluationContext().value(), false);
      } else {
        assert false;
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.Func<T> func) {
      this.enter(EvaluableDesc.fromEvaluable(func), evaluationContext);
      try {
        Object resultValue = func.head().apply(evaluationContext.returnedValue());
        this.leaveWithReturnedValue(func, evaluationContext.returnedValue(), resultValue, evaluationContext.returnedValue(), resultValue, false);
        func.tail().ifPresent(tailSide -> ((Evaluable<Object>) tailSide).accept(EvaluationContext.forValue(resultValue), this));
      } catch (Error e) {
        throw e;
      } catch (Throwable e) {
        this.leaveWithThrownException(func, evaluationContext.returnedValue(), UNKNOWN, evaluationContext.returnedValue(), e);
        func.tail().ifPresent(tailSide -> tailSide.accept((EvaluationContext) this.currentEvaluationContext().exceptionThrown(e), this));
      }
    }

    @Override
    public <E> void evaluate(EvaluationContext<Stream<E>> evaluationContext, Evaluable.StreamPred<E> streamPred) {
      boolean ret = streamPred.defaultValue();
      this.enter(EvaluableDesc.fromEvaluable(streamPred), evaluationContext);
      // Use NULL_VALUE object instead of null. Otherwise, the operation will fail with NullPointerException
      // on 'findFirst()'.
      // Although NULL_VALUE is an ordinary Object, not a evaluationContext of E, this works
      // because either way we will just return a boolean and during the execution,
      // type information is erased.
      // TODO: Issue-#59: Need exception handling
      this.leaveWithReturnedValue(
          streamPred,
          evaluationContext.value(),
          streamPred.requestExpectationFlip() ^ this.currentlyExpectedBooleanValue, evaluationContext.value(),
          evaluationContext.returnedValue()
              .filter(createValueCheckingPredicateForStream(streamPred))
              .map(v -> v != null ? v : NULL_VALUE)
              .findFirst()
              .map(each -> !ret)
              .orElse(ret), false);
    }

    @Override
    public Object resultValue() {
      return currentEvaluationContext.value();
    }

    @Override
    public boolean resultValueAsBoolean() {
      if (this.resultValue() instanceof Boolean)
        return (boolean) resultValue();
      return false;
    }

    public boolean resultValueAsBooleanIfBooleanOtherwise(boolean otherwiseValue) {
      return resultValue() instanceof Boolean ? resultValueAsBoolean() : otherwiseValue;
    }

    @Override
    public List<EvaluationEntry> resultEntries() {
      return unmodifiableList(this.entries);
    }

    @SuppressWarnings("unchecked")
    private <T> EvaluationContext<T> currentEvaluationContext() {
      return (EvaluationContext<T>) this.currentEvaluationContext;
    }

    private <E> Predicate<E> createValueCheckingPredicateForStream(Evaluable.StreamPred<E> streamPredicate) {
      return e -> {
        Evaluator evaluator = this.copyEvaluator();

        boolean succeeded = false;
        boolean ret = false;
        Object throwable = "<<OUTPUT MISSING>>";
        try {
          streamPredicate.cut().accept(EvaluationContext.forValue(e), evaluator);
          succeeded = true;
        } catch (Error error) {
          throw error;
        } catch (Throwable t) {
          throwable = t;
          throw wrapIfNecessary(t);
        } finally {
          if (!succeeded || evaluator.resultValueAsBoolean() == streamPredicate.valueToCut()) {
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

    public void importEvaluationEntries(List<EvaluationEntry> resultEntries, Object other) {
      resultEntries.stream()
          .map(each -> createEvaluationEntryForImport(each, other))
          .forEach(each -> this.entries.add(each));
    }

    private EvaluationEntry.Finalized createEvaluationEntryForImport(EvaluationEntry entry, Object other) {
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
          entry.wasExceptionThrown(),
          entry.requiresExplanation());
    }

    private static String composeActualValueFromInputAndThrowable(Object input, Throwable throwable) {
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

    static EvaluableDesc fromEvaluable(Evaluable.ContextPred contextEvaluable) {
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
    Object snapshot();

    static Object toSnapshotIfPossible(Object value) {
      if (value instanceof Snapshottable)
        return ((Snapshottable) value).snapshot();
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