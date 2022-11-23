package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.core.context.VariableBundle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.Evaluator.Entry.Type.*;
import static com.github.dakusui.pcond.core.Evaluator.Explainable.explainActual;
import static com.github.dakusui.pcond.core.Evaluator.Explainable.explainExpectation;
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
   * @see Entry
   */
  List<Entry> resultEntries();


  /**
   * Returns a new instance of this interface.
   *
   * @return a new instance of this interface.
   */
  static Evaluator create() {
    return new Impl();
  }

  class Impl implements Evaluator {
    private static final Object NULL_VALUE = new Object();
    List<Entry.OnGoing> onGoingEntries = new LinkedList<>();
    List<Entry>         entries        = new ArrayList<>();
    final EvaluationContext<Object> currentResult;
    boolean currentlyExpectedBooleanValue = true;

    public Impl() {
      this.currentResult = EvaluationContext.forValue(null);
    }

    void enter(EvaluableDesc evaluableDesc, EvaluationContext<?> input) {
      Entry.OnGoing newEntry = new Entry.OnGoing(
          input.toSnapshot(), evaluableDesc.type(),
          (int) onGoingEntries.stream().filter(each -> !each.isTrivial()).count(),
          evaluableDesc.name,
          this.currentlyExpectedBooleanValue,
          evaluableDesc.isTrivial(),
          entries.size()
      );
      onGoingEntries.add(newEntry);
      entries.add(newEntry);
      if (evaluableDesc.requestExpectationFlip())
        this.flipCurrentlyExpectedBooleanValue();
    }

    void leave(Evaluable<?> evaluable, EvaluationContext<Object> evaluationContext, boolean unexpected) {
      if (evaluationContext.state() == EvaluationContext.State.VALUE_RETURNED)
        leaveWithReturnedValue(evaluable, evaluationContext.returnedValue(), unexpected);
      else if (evaluationContext.state() == EvaluationContext.State.EXCEPTION_THROWN)
        leaveWithThrownException(evaluable, evaluationContext.thrownException());
    }

    void leaveWithReturnedValue(Evaluable<?> evaluable, Object returnedValue, boolean unexpected) {
      int positionInOngoingEntries = onGoingEntries.size() - 1;
      Entry.OnGoing current = onGoingEntries.get(positionInOngoingEntries);
      entries.set(
          current.positionInEntries,
          current.result(
              toSnapshotIfPossible(returnedValue),
              unexpected ? explainExpectation(evaluable) : null,
              unexpected ? explainActual(evaluable, composeActualValue(current.actualInput(), returnedValue)) : null));
      onGoingEntries.remove(positionInOngoingEntries);
      this.currentResult.valueReturned(returnedValue);
      if (evaluable.requestExpectationFlip())
        this.flipCurrentlyExpectedBooleanValue();
    }

    void leaveWithThrownException(Evaluable<?> evaluable, Throwable thrownException) {
      int positionInOngoingEntries = onGoingEntries.size() - 1;
      Entry.OnGoing current = onGoingEntries.get(positionInOngoingEntries);
      entries.set(
          current.positionInEntries,
          current.result(
              toSnapshotIfPossible(thrownException),
              explainExpectation(evaluable),
              explainActual(evaluable, composeActualValue(current.actualInput(), thrownException))));
      onGoingEntries.remove(positionInOngoingEntries);
      this.currentResult.exceptionThrown(thrownException);
      if (evaluable.requestExpectationFlip())
        this.flipCurrentlyExpectedBooleanValue();
    }

    private static Object composeActualValue(Object input, Object output) {
      if (output instanceof Throwable)
        return composeActualValueFromInputAndThrowable(input, (Throwable) output);
      return input;
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

    void flipCurrentlyExpectedBooleanValue() {
      this.currentlyExpectedBooleanValue = !this.currentlyExpectedBooleanValue;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public <T> void evaluate(EvaluationContext<? extends T> value, Evaluable.Conjunction<T> conjunction) {
      int i = 0;
      boolean finalValue = true;
      boolean shortcut = conjunction.shortcut();
      EvaluationContext<Object> clonedContext = (EvaluationContext<Object>) value.clone();
      for (Evaluable<? super T> each : conjunction.children()) {
        this.currentResult().resetTo(clonedContext);
        if (i == 0)
          this.enter(EvaluableDesc.fromEvaluable(conjunction), value);
        each.accept(value, this);
        boolean cur = this.resultValueAsBooleanIfBooleanOtherwise(!this.currentlyExpectedBooleanValue);
        if (!cur)
          finalValue = cur; // This is constant, but keeping it for readability
        if ((shortcut && !finalValue) || i == conjunction.children().size() - 1) {
          this.leaveWithReturnedValue(conjunction, finalValue, false); // Is this "false" ok? When the finalValue != expected, shouldn't it be true?
          return;
        }
        i++;
      }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public <T> void evaluate(EvaluationContext<T> value, Evaluable.Disjunction<T> disjunction) {
      int i = 0;
      boolean finalValue = false;
      boolean shortcut = disjunction.shortcut();
      Object currentValue = this.currentResult().currentValue();
      for (Evaluable<? super T> each : disjunction.children()) {
        this.currentResult().valueReturned(currentValue);
        if (i == 0)
          this.enter(EvaluableDesc.fromEvaluable(disjunction), value);
        each.accept(value, this);
        boolean cur = this.resultValueAsBoolean();
        if (cur)
          finalValue = cur; // This is constant, but keeping it for readability
        if ((shortcut && finalValue) || i == disjunction.children().size() - 1) {
          this.leaveWithReturnedValue(disjunction, finalValue, false); // Is this "false" ok? When the finalValue != expected, shouldn't it be true?
          return;
        }
        i++;
      }
    }

    @Override
    public <T> void evaluate(EvaluationContext<T> value, Evaluable.Negation<T> negation) {
      this.enter(EvaluableDesc.fromEvaluable(negation), value);
      negation.target().accept(value, this);
      this.leaveWithReturnedValue(negation, !this.resultValueAsBoolean(), false);
      if (Objects.equals(this.resultValue(), this.currentlyExpectedBooleanValue))
        mergeLastTwoEntriesIfPossible(this.entries);
    }

    private static void mergeLastTwoEntriesIfPossible(List<Entry> entries) {
      if (LEAF.equals(entries.get(entries.size() - 1).type())) {
        Entry entryForLeaf = entries.remove(entries.size() - 1);
        Entry entryForNegate = entries.remove(entries.size() - 1);
        entries.add(mergeNegateAndLeafEntries(entryForNegate, entryForLeaf));
      }
    }

    public static Entry.Finalized mergeNegateAndLeafEntries(Entry negate, Entry predicate) {
      return new Entry.Finalized(
          format("not(%s)", predicate.formName()), predicate.type(),
          negate.level(),
          negate.expectedBooleanValue(), negate.expectationDetail(), negate.actualInput(),
          negate.actualInputDetail(), negate.output(),
          "detailOutputActualValue",
          false
      );
    }

    @Override
    public <T> void evaluate(EvaluationContext<T> evaluationContext, Evaluable.LeafPred<T> leafPred) {
      this.enter(EvaluableDesc.fromEvaluable(leafPred), evaluationContext);
      // TODO: Issue-#59: Need exception handling
      try {
        System.out.println("LEAF:<" + evaluationContext + ">");
        if (evaluationContext.state() == EvaluationContext.State.VALUE_RETURNED) {
          boolean result = leafPred.predicate().test(evaluationContext.returnedValue());
          this.leaveWithReturnedValue(leafPred, result, this.currentlyExpectedBooleanValue != result);
        } else {
          this.leaveWithReturnedValue(leafPred, "(not evaluated)", false);
        }
      } catch (Error e) {
        throw e;
      } catch (Throwable e) {
        this.leaveWithThrownException(leafPred, e);
      }
    }

    @Override
    public void evaluate(EvaluationContext<VariableBundle> value, Evaluable.ContextPred contextPred) {
      this.enter(EvaluableDesc.fromEvaluable(contextPred), value);
      // TODO: Issue-#59: Need exception handling
      contextPred.enclosed().accept(EvaluationContext.forValue(value.returnedValue().valueAt(contextPred.argIndex())), this);
      this.leaveWithReturnedValue(contextPred, this.resultValue(), false);
    }

    @Override
    public <T, R> void evaluate(EvaluationContext<T> value, Evaluable.Transformation<T, R> transformation) {
      if (isDummyFunction((Function<?, ?>) transformation.mapper())) {
        transformation.checker().accept(currentResult(), this);
        return;
      }
      this.enter(EvaluableDesc.forMapperFromEvaluable(transformation), value);
      transformation.mapper().accept(value, this);
      this.leave(
          transformation.checker(),
          this.currentResult(),
          false);
      this.enter(EvaluableDesc.forCheckerFromEvaluable(transformation), currentResult());

      transformation.checker().accept(this.currentResult(), this);
      this.leaveWithReturnedValue(
          transformation.mapper(),
          this.currentResult().returnedValue(),
          false);
    }

    @SuppressWarnings("unchecked")
    private <T> EvaluationContext<T> currentResult() {
      return (EvaluationContext<T>) this.currentResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void evaluate(EvaluationContext<T> value, Evaluable.Func<T> func) {
      this.enter(EvaluableDesc.fromEvaluable(func), value);
      try {
        Object resultValue = func.head().apply(value.returnedValue());
        this.leaveWithReturnedValue(func, resultValue, false);
        func.tail().ifPresent(tailSide -> ((Evaluable<Object>) tailSide).accept(EvaluationContext.forValue(resultValue), this));
      } catch (Error e) {
        throw e;
      } catch (Throwable e) {
        this.leaveWithThrownException(func, e);
        func.tail().ifPresent(tailSide -> tailSide.accept((EvaluationContext) this.currentResult().exceptionThrown(e), this));
      }
    }

    @Override
    public <E> void evaluate(EvaluationContext<Stream<E>> value, Evaluable.StreamPred<E> streamPred) {
      boolean ret = streamPred.defaultValue();
      this.enter(EvaluableDesc.fromEvaluable(streamPred), value);
      // Use NULL_VALUE object instead of null. Otherwise, the operation will fail with NullPointerException
      // on 'findFirst()'.
      // Although NULL_VALUE is an ordinary Object, not a value of E, this works
      // because either way we will just return a boolean and during the execution,
      // type information is erased.
      // TODO: Issue-#59: Need exception handling
      this.leaveWithReturnedValue(
          streamPred,
          value.returnedValue()
              .filter(valueChecker(streamPred))
              .map(v -> v != null ? v : NULL_VALUE)
              .findFirst()
              .map(each -> !ret)
              .orElse(ret),
          false);
    }

    private <E> Predicate<E> valueChecker(Evaluable.StreamPred<E> streamPred) {
      return e -> {
        Evaluator evaluator = this.copyEvaluator();

        boolean succeeded = false;
        boolean ret = false;
        Object throwable = "<<OUTPUT MISSING>>";
        try {
          streamPred.cut().accept(EvaluationContext.forValue(e), evaluator);
          succeeded = true;
        } catch (Error error) {
          throw error;
        } catch (Throwable t) {
          throwable = t;
          throw wrapIfNecessary(t);
        } finally {
          if (!succeeded || evaluator.resultValueAsBoolean() == streamPred.valueToCut()) {
            importResultEntries(evaluator.resultEntries(), throwable);
            ret = true;
          }
        }
        return ret;
      };
    }

    @Override
    public Object resultValue() {
      return currentResult.value();
    }

    @Override
    public boolean resultValueAsBoolean() {
      return (boolean) resultValue();
    }

    public boolean resultValueAsBooleanIfBooleanOtherwise(boolean otherwiseValue) {
      return resultValue() instanceof Boolean ? resultValueAsBoolean() : otherwiseValue;
    }

    @Override
    public List<Entry> resultEntries() {
      return unmodifiableList(this.entries);
    }

    private Evaluator copyEvaluator() {
      Impl impl = new Impl();
      impl.currentlyExpectedBooleanValue = this.currentlyExpectedBooleanValue;
      return impl;
    }

    public void importResultEntries(List<Entry> resultEntries, Object other) {
      resultEntries.stream()
          .map(each -> createEntryForImport(each, other))
          .forEach(each -> this.entries.add(each));
    }

    private Entry.Finalized createEntryForImport(Entry each, Object other) {
      return new Entry.Finalized(
          each.formName(), each.type,
          this.onGoingEntries.size() + each.level(),
          each.outputExpectation, each.expectationDetail(), each.actualInput(),
          each.hasActualInputDetail() ? each.actualInputDetail() : null, each.evaluationFinished() ? each.output() : other,
          "detailOutputActualValue",
          each.trivial
      );
    }
  }

  class EvaluableDesc {
    final Entry.Type type;
    final String     name;
    final boolean    requestsExpectationFlip;
    final boolean    trivial;

    public EvaluableDesc(Entry.Type type, String name, boolean requestsExpectationFlip, boolean trivial) {
      this.type = type;
      this.name = name;
      this.requestsExpectationFlip = requestsExpectationFlip;
      this.trivial = trivial;
    }

    public Entry.Type type() {
      return this.type;
    }

    public boolean isTrivial() {
      return this.trivial;
    }

    public boolean requestExpectationFlip() {
      return this.requestsExpectationFlip;
    }

    static <T> EvaluableDesc fromEvaluable(Evaluable.LeafPred<T> leafEvaluable) {
      return new EvaluableDesc(
          LEAF,
          String.format("%s", leafEvaluable),
          leafEvaluable.requestExpectationFlip(),
          leafEvaluable.isTrivial()
      );
    }

    static EvaluableDesc fromEvaluable(Evaluable.ContextPred contextEvaluable) {
      return new EvaluableDesc(
          LEAF,
          String.format("%s", contextEvaluable),
          contextEvaluable.requestExpectationFlip(),
          contextEvaluable.isTrivial()
      );
    }

    static <T> EvaluableDesc fromEvaluable(Evaluable.StreamPred<T> streamEvaluable) {
      return new EvaluableDesc(
          LEAF,
          String.format("%s", streamEvaluable),
          streamEvaluable.requestExpectationFlip(),
          streamEvaluable.isTrivial()
      );
    }

    static <T> EvaluableDesc fromEvaluable(Evaluable.Func<T> funcEvaluable) {
      return new EvaluableDesc(
          FUNCTION,
          String.format("%s", funcEvaluable.head()),
          funcEvaluable.requestExpectationFlip(),
          funcEvaluable.isTrivial()
      );
    }

    static <T> EvaluableDesc fromEvaluable(Evaluable.Conjunction<T> conjunctionEvaluable) {
      return new EvaluableDesc(
          FUNCTION,
          String.format("%s", conjunctionEvaluable.shortcut() ? "and" : "allOf"),
          conjunctionEvaluable.requestExpectationFlip(),
          conjunctionEvaluable.isTrivial()
      );
    }

    static <T> EvaluableDesc fromEvaluable(Evaluable.Disjunction<T> disjunctionEvaluable) {
      return new EvaluableDesc(
          FUNCTION,
          String.format("%s", disjunctionEvaluable.shortcut() ? "or" : "anyOf"),
          disjunctionEvaluable.requestExpectationFlip(),
          disjunctionEvaluable.isTrivial()
      );
    }

    static <T> EvaluableDesc fromEvaluable(Evaluable.Negation<T> negationEvaluable) {
      return new EvaluableDesc(
          FUNCTION,
          "not",
          negationEvaluable.requestExpectationFlip(),
          negationEvaluable.isTrivial()
      );
    }

    static <T, R> EvaluableDesc forMapperFromEvaluable(Evaluable.Transformation<T, R> transformationEvaluable) {
      return new EvaluableDesc(
          TRANSFORM,
          transformationEvaluable.mapperName().orElse("transform"),
          transformationEvaluable.mapper().requestExpectationFlip(),
          transformationEvaluable.mapper().isTrivial()
      );
    }

    static <T, R> EvaluableDesc forCheckerFromEvaluable(Evaluable.Transformation<T, R> transformationEvaluable) {
      return new EvaluableDesc(
          CHECK,
          transformationEvaluable.checkerName().orElse("check"),
          transformationEvaluable.checker().requestExpectationFlip(),
          transformationEvaluable.checker().isTrivial()
      );
    }
  }

  /**
   * A class to hold an entry of execution history of the {@link Evaluator}.
   * When an evaluator enters into one {@link Evaluable} (actually a predicate or a function),
   * an {@link OnGoing} entry is created and held by the evaluator as a current
   * one.
   * Since one evaluate can have its children and only one child can be evaluated at once,
   * on-going entries are held as a list (stack).
   *
   * When the evaluator leaves the evaluable, the entry is "finalized".
   * From the data held by an entry, "expectation" and "actual behavior" reports are generated.
   *
   * .Evaluation Summary Format
   * ----
   * +----------------------------------------------------------------------------- Failure Detail Index
   * |  +-------------------------------------------------------------------------- Input
   * |  |                                            +----------------------------- Form (Function/Predicate)
   * |  |                                            |                           +- Output
   * |  |                                            |                           |
   * V  V                                            V                           V
   * Book:[title:<De Bello G...i appellantur.>]->check:allOf               ->false
   * transform:title       ->"De Bello Gallico"
   * "De Bello Gallico"                        ->    check:allOf           ->false
   * isNotNull         ->true
   * [0]                                                     transform:parseInt->NumberFormatException:"For input s...ico""
   * null                                      ->        check:allOf       ->false
   * >=[10]        ->true
   * <[40]         ->true
   * Book:[title:<De Bello G...i appellantur.>]->    transform:title       ->"Gallia est omnis divis...li appellantur."
   * "Gallia est omnis divis...li appellantur."->    check:allOf           ->false
   * isNotNull         ->true
   * transform:length  ->145
   * 145                                       ->        check:allOf       ->false
   * [1]                                                         >=[200]       ->true
   * <[400]        ->true
   * ----
   *
   * Failure Detail Index::
   * In the full format of a failure report, detailed descriptions of mismatching forms are provided if the form is {@link Explainable}.
   * This index points an item in the detail part of the full report.
   * Input::
   * Values given to forms are printed here.
   * If the previous line uses the same value, the value will not be printed.
   * Form (Function/Predicate)::
   * This part displays names of forms (predicates and functions).
   * If a form is marked trivial, the framework may merge the form with the next line.
   * Output::
   * For predicates, expected boolean value is printed.
   * For functions, if a function does not throw an exception during its evaluation, the result will be printed here both for expectation and actual behavior summary.
   * If it throws an exception, the exception will be printed here in actual behavior summary.
   */
  abstract class Entry {
    private final Type   type;
    /**
     * A name of a form (evaluable; function, predicate)
     */
    private final String formName;

    private final int level;


    /**
     * A field to store an input value to an {@code Evaluable}.
     * This may be
     */
    private final Object inputActualValue;
    Object detailInputActualValue;

    /**
     * A name of an evaluable.
     */
    private final boolean outputExpectation;

    Object detailOutputExpectation;

    /**
     * A flag to let the framework know this entry should be printed in a less outstanding form.
     */
    final boolean trivial;

    Entry(String formName, Type type, int level, boolean outputExpectation, Object detailOutputExpectation, Object inputActualValue, boolean trivial) {
      this.type = type;
      this.level = level;
      this.formName = formName;
      this.outputExpectation = outputExpectation;
      this.detailOutputExpectation = detailOutputExpectation;
      this.inputActualValue = inputActualValue;
      this.detailInputActualValue = "(Not available)";
      this.trivial = trivial;
    }

    Entry(Entry base) {
      this.type = base.type();
      this.level = base.level();
      this.formName = base.formName();
      this.inputActualValue = base.actualInput();
      this.detailInputActualValue = base.actualInputDetail();
      this.detailOutputExpectation = base.expectationDetail();
      this.outputExpectation = base.expectedBooleanValue();
      this.trivial = base.isTrivial();
    }

    public int level() {
      return level;
    }

    @SuppressWarnings({ "unchecked" })
    public <T> T actualInput() {
      return (T) this.inputActualValue;
    }

    public String formName() {
      return formName;
    }

    public Type type() {
      return this.type;
    }

    public boolean expectedBooleanValue() {
      return this.outputExpectation;
    }

    public abstract boolean evaluationFinished();

    public abstract <T> T output();

    public Object expectationDetail() {
      return this.detailOutputExpectation;
    }

    public abstract boolean hasActualInputDetail();

    final public Object actualInputDetail() {
      return this.detailInputActualValue;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isTrivial() {
      return this.trivial;
    }

    public enum Type {
      TRANSFORM,
      CHECK,
      AND,
      OR,
      NOT,
      LEAF,
      FUNCTION,
      ;
    }

    static class Finalized extends Entry {
      final Object outputActualValue;
      final Object detailOutputActualValue;

      Finalized(
          String formName, Type type,
          int level,
          boolean outputExpectation, Object detailOutputExpectation,
          Object inputActualValue, Object detailInputActualValue,
          Object outputActualValue, Object detailOutputActualValue,
          boolean trivial) {
        super(formName, type, level, outputExpectation, detailOutputExpectation, inputActualValue, trivial);
        this.outputActualValue = outputActualValue;
        this.detailOutputActualValue = detailOutputActualValue;
        this.detailInputActualValue = detailInputActualValue;
      }

      Finalized(OnGoing onGoing, Object outputActualValue, Object detailOutputExpectation, Object detailInputActualValue, Object detailOutputActualValue) {
        super(onGoing);
        this.outputActualValue = outputActualValue;
        this.detailOutputActualValue = detailOutputActualValue;
        this.detailOutputExpectation = detailOutputExpectation;
        this.detailInputActualValue = detailInputActualValue;
      }

      @Override
      public boolean evaluationFinished() {
        return true;
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T> T output() {
        return (T) outputActualValue;
      }

      @Override
      public boolean hasActualInputDetail() {
        return this.detailInputActualValue != null;
      }
    }

    static class OnGoing extends Entry {
      final int positionInEntries;

      OnGoing(Object input, Type type, int level, String formName, boolean outputExpectation, boolean trivial, int positionInEntries) {
        super(formName, type, level, outputExpectation, outputExpectation, input, trivial);
        this.positionInEntries = positionInEntries;
      }

      Finalized result(Object result, Object expectationDetail, Object actualInputDetail) {
        return new Finalized(this, result, expectationDetail, actualInputDetail, "detailOutputActualValue");
      }

      @Override
      public boolean evaluationFinished() {
        return false;
      }

      @Override
      public <T> T output() {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean hasActualInputDetail() {
        return false;
      }
    }
  }

  /**
   * If an input or an output value object of a form implements this interface,
   * The value returned by `snapshot` method is stored in a {@link Evaluator.Entry}
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
    Object explainExpectation();

    Object explainActual(Object actualValue);

    static Object explainExpectation(Object evaluable) {
      if (evaluable instanceof Explainable)
        return explainValue(((Explainable) evaluable).explainExpectation());
      return null;
    }

    static Object explainActual(Object evaluable, Object actualValue) {
      if (evaluable instanceof Explainable)
        return explainValue(((Explainable) evaluable).explainActual(actualValue));
      return null;
    }
  }
}