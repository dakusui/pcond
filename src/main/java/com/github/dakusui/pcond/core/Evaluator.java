package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.core.context.Context;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.Evaluator.Entry.Type.*;
import static com.github.dakusui.pcond.core.Evaluator.Explainable.explainActualInputIfPossibleOrNull;
import static com.github.dakusui.pcond.core.Evaluator.Explainable.explainExpectationIfPossibleOrNull;
import static com.github.dakusui.pcond.core.Evaluator.Snapshottable.toSnapshotIfPossible;
import static com.github.dakusui.pcond.internals.InternalUtils.*;
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
  <T> void evaluate(ContextVariable<? extends T> value, Evaluable.Conjunction<T> conjunction);

  /**
   * Evaluates `value` with a `disjunction` predicate ("or").
   *
   * @param value       A value to be evaluated.
   * @param disjunction A disjunction predicate with which `value` is evaluated.
   * @param <T>         The type of the `value`.
   * @see com.github.dakusui.pcond.core.Evaluable.Disjunction
   */
  <T> void evaluate(ContextVariable<T> value, Evaluable.Disjunction<T> disjunction);

  /**
   * Evaluates `value` with a `negation` predicate ("not").
   *
   * @param value    A value to be evaluated.
   * @param negation A negation predicate with which `value` is evaluated.
   * @param <T>      The type of the `value`.
   * @see com.github.dakusui.pcond.core.Evaluable.Negation
   */
  <T> void evaluate(ContextVariable<T> value, Evaluable.Negation<T> negation);

  /**
   * Evaluates `value` with a leaf predicate.
   *
   * @param value    A value to be evaluated.
   * @param leafPred A predicate with which `value` is evaluated.
   * @param <T>      The type of the `value`.
   * @see com.github.dakusui.pcond.core.Evaluable.LeafPred
   */
  <T> void evaluate(ContextVariable<T> value, Evaluable.LeafPred<T> leafPred);

  /**
   * Evaluates `value` with a context predicate.
   *
   * @param value       A value to be evaluated.
   * @param contextPred A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.ContextPred
   */
  void evaluate(ContextVariable<Context> value, Evaluable.ContextPred contextPred);

  /**
   * Evaluates `value` with a "transformatioin" predicate.
   *
   * @param value          A value to be evaluated.
   * @param transformation A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Transformation
   */
  <T, R> void evaluate(ContextVariable<T> value, Evaluable.Transformation<T, R> transformation);

  /**
   * Evaluates `value` with a "function" predicate.
   *
   * @param value A value to be evaluated.
   * @param func  A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.Func
   */
  <T> void evaluate(ContextVariable<T> value, Evaluable.Func<T> func);

  /**
   * Evaluates `value` with a predicate for a stream.
   *
   * @param value      A value to be evaluated.
   * @param streamPred A predicate with which `value` is evaluated.
   * @see com.github.dakusui.pcond.core.Evaluable.StreamPred
   */
  <E> void evaluate(ContextVariable<Stream<E>> value, Evaluable.StreamPred<E> streamPred);

  /**
   * The last evaluated value by an `evaluate` method defined in this interface.
   * This method is expected to be called from inside an `evaluated` method, not
   * to be called by a user.
   *
   * @param <T> The type of the last value evaluated by this object.
   * @return The evaluated result value.
   */
  <T> T resultValue();

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
    List<Entry.OnGoing> onGoingEntries                = new LinkedList<>();
    List<Entry>         entries                       = new ArrayList<>();
    Object              currentResult;
    boolean             currentlyExpectedBooleanValue = true;

    public Impl() {
    }

    void enter(Entry.Type type, Evaluable<?> evaluable, String name, Object input) {
      Entry.OnGoing newEntry = new Entry.OnGoing(
          type,
          (int) onGoingEntries.stream().filter(each -> !each.isTrivial()).count(),
          entries.size(),
          name,
          toSnapshotIfPossible(input),
          this.currentlyExpectedBooleanValue,
          evaluable.isTrivial());
      onGoingEntries.add(newEntry);
      entries.add(newEntry);
      if (evaluable.requestExpectationFlip())
        this.flipCurrentlyExpectedBooleanValue();
    }

    void leave(Object result, Evaluable<?> evaluable, boolean unexpected) {
      int positionInOngoingEntries = onGoingEntries.size() - 1;
      Entry.OnGoing current = onGoingEntries.get(positionInOngoingEntries);
      entries.set(
          current.positionInEntries,
          current.result(
              toSnapshotIfPossible(result),
              unexpected ? explainExpectationIfPossibleOrNull(evaluable) : null,
              unexpected ? explainActualInputIfPossibleOrNull(evaluable, current.input()) : null));
      onGoingEntries.remove(positionInOngoingEntries);
      this.currentResult = result;
      if (evaluable.requestExpectationFlip())
        this.flipCurrentlyExpectedBooleanValue();
    }

    void flipCurrentlyExpectedBooleanValue() {
      this.currentlyExpectedBooleanValue = !this.currentlyExpectedBooleanValue;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public <T> void evaluate(ContextVariable<? extends T> value, Evaluable.Conjunction<T> conjunction) {
      int i = 0;
      boolean finalValue = true;
      boolean shortcut = conjunction.shortcut();
      for (Evaluable<? super T> each : conjunction.children()) {
        if (i == 0)
          this.enter(AND, conjunction, conjunction.shortcut() ? "and" : "allOf", value);
        each.accept(value, this);
        boolean cur = this.<Boolean>resultValue();
        if (!cur)
          finalValue = cur; // This is constant, but keeping it for readability
        if ((shortcut && !finalValue) || i == conjunction.children().size() - 1) {
          this.leave(finalValue, conjunction, false);
          return;
        }
        i++;
      }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public <T> void evaluate(ContextVariable<T> value, Evaluable.Disjunction<T> disjunction) {
      int i = 0;
      boolean finalValue = false;
      boolean shortcut = disjunction.shortcut();
      for (Evaluable<? super T> each : disjunction.children()) {
        if (i == 0)
          this.enter(OR, disjunction, disjunction.shortcut() ? "or" : "anyOf", value);
        each.accept(value, this);
        boolean cur = this.<Boolean>resultValue();
        if (cur)
          finalValue = cur; // This is constant, but keeping it for readability
        if ((shortcut && finalValue) || i == disjunction.children().size() - 1) {
          this.leave(finalValue, disjunction, false);
          return;
        }
        i++;
      }
    }

    @Override
    public <T> void evaluate(ContextVariable<T> value, Evaluable.Negation<T> negation) {
      this.enter(Entry.Type.NOT, negation, "not", value);
      negation.target().accept(value, this);
      this.leave(!this.<Boolean>resultValue(), negation, false);
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
          predicate.type(),
          negate.level(),
          negate.input(),
          negate.output(),
          String.format("not(%s)", predicate.formName()),
          negate.expectedBooleanValue(),
          negate.expectationDetail(),
          negate.actualInputDetail(),
          false
      );
    }

    @Override
    public <T> void evaluate(ContextVariable<T> value, Evaluable.LeafPred<T> leafPred) {
      this.enter(LEAF, leafPred, String.format("%s", leafPred), value);
      // TODO: Issue-#59: Need exception handling
      boolean result = leafPred.predicate().test(value.value());
      this.leave(result, leafPred, this.currentlyExpectedBooleanValue != result);
    }

    @Override
    public void evaluate(ContextVariable<Context> context, Evaluable.ContextPred contextPred) {
      this.enter(LEAF, contextPred, String.format("%s", contextPred), context);
      // TODO: Issue-#59: Need exception handling
      contextPred.enclosed().accept(context.value().valueAt(contextPred.argIndex()), this);
      this.leave(this.resultValue(), contextPred, false);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public <T, R> void evaluate(ContextVariable<T> value, Evaluable.Transformation<T, R> transformation) {
      if (isDummyFunction((Function<?, ?>) transformation.mapper())) {
        transformation.checker().accept(ContextVariable.forValue((R) value), this);
        return;
      }
      this.enter(TRANSFORM,
          transformation.mapper(),
          transformation.mapperName()
              .orElse("transform"), value);
      transformation.mapper().accept(value, this);
      this.leave(this.resultValue(), transformation.checker(), false);
      this.enter(CHECK,
          transformation.checker(),
          transformation.checkerName()
              .orElse("check"),
          this.resultValue());

      transformation.checker().accept(ContextVariable.forValue((R) this.currentResult), this);
      this.leave(this.resultValue(), transformation.mapper(), false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void evaluate(ContextVariable<T> value, Evaluable.Func<T> func) {
      this.enter(FUNCTION, func, String.format("%s", func.head()), value);
      Object resultValue = func.head().apply(value.value());
      this.leave(resultValue, func, false);
      func.tail().ifPresent(tailSide -> ((Evaluable<Object>) tailSide).accept(ContextVariable.forValue(resultValue), this));
    }

    @Override
    public <E> void evaluate(ContextVariable<Stream<E>> value, Evaluable.StreamPred<E> streamPred) {
      boolean ret = streamPred.defaultValue();
      this.enter(LEAF, streamPred, String.format("%s", streamPred), value);
      // Use NULL_VALUE object instead of null. Otherwise, the operation will fail with NullPointerException
      // on 'findFirst()'.
      // Although NULL_VALUE is an ordinary Object, not a value of E, this works
      // because either way we will just return a boolean and during the execution,
      // type information is erased.
      // TODO: Issue-#59: Need exception handling
      this.leave(value.value()
          .filter(valueChecker(streamPred))
          .map(v -> v != null ? v : NULL_VALUE)
          .findFirst()
          .map(each -> !ret)
          .orElse(ret), streamPred, false);
    }

    private <E> Predicate<E> valueChecker(Evaluable.StreamPred<E> streamPred) {
      return e -> {
        Evaluator evaluator = this.copyEvaluator();

        boolean succeeded = false;
        boolean ret = false;
        Object throwable = "<<OUTPUT MISSING>>";
        try {
          streamPred.cut().accept(ContextVariable.forValue(e), evaluator);
          succeeded = true;
        } catch (Error error) {
          throw error;
        } catch (Throwable t) {
          throwable = t;
          throw wrapIfNecessary(t);
        } finally {
          if (!succeeded || evaluator.<Boolean>resultValue() == streamPred.valueToCut()) {
            importResultEntries(evaluator.resultEntries(), throwable);
            ret = true;
          }
        }
        return ret;
      };
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T resultValue() {
      return (T) currentResult;
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
          each.type,
          this.onGoingEntries.size() + each.level(),
          each.input(),
          each.hasOutput() ? each.output() : other, each.formName(),
          each.expectedBooleanValue,
          each.hasExpectationDetail() ? each.expectationDetail() : null,
          each.hasActualInputDetail() ? each.actualInputDetail() : null,
          each.trivial);
    }
  }

  /**
   * A class to hold an entry of an execution history of the {@link Evaluator}.
   */
  abstract class Entry {
    private final Type    type;
    private final int     level;
    private final Object  input;
    private final String  name;
    private final boolean expectedBooleanValue;

    final boolean trivial;

    Entry(Type type, int level, Object input, String name, boolean expectedBooleanValue, boolean trivial) {
      this.type = type;
      this.level = level;
      this.input = input;
      this.name = name;
      this.expectedBooleanValue = expectedBooleanValue;
      this.trivial = trivial;
    }

    public int level() {
      return level;
    }

    @SuppressWarnings({ "unchecked" })
    public <T> T input() {
      return (T) this.input;
    }

    public String formName() {
      return name;
    }

    public Type type() {
      return this.type;
    }

    public boolean expectedBooleanValue() {
      return this.expectedBooleanValue;
    }

    public abstract boolean hasOutput();

    public abstract <T> T output();

    public abstract boolean hasExpectationDetail();

    public abstract Object expectationDetail();

    public abstract boolean hasActualInputDetail();

    public abstract Object actualInputDetail();

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
    }

    static class Finalized extends Entry {
      final         Object output;
      final         Object expectationDetail;
      private final Object actualInputDetail;

      Finalized(
          Type type,
          int level,
          Object input,
          Object output,
          String name,
          boolean expectedBooleanValue,
          Object expectationDetail,
          Object actualInputDetail,
          boolean trivial) {
        super(type, level, input, name, expectedBooleanValue, trivial);
        this.output = output;
        this.expectationDetail = expectationDetail;
        this.actualInputDetail = actualInputDetail;
      }

      @Override
      public boolean hasOutput() {
        return true;
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T> T output() {
        return (T) output;
      }

      @Override
      public boolean hasExpectationDetail() {
        return this.expectationDetail != null;
      }

      @Override
      public Object expectationDetail() {
        return this.expectationDetail;
      }

      @Override
      public boolean hasActualInputDetail() {
        return this.actualInputDetail != null;
      }

      @Override
      public Object actualInputDetail() {
        return this.actualInputDetail;
      }
    }

    static class OnGoing extends Entry {
      final int positionInEntries;

      OnGoing(Type type, int level, int positionInEntries, String name, Object input, boolean expectedBooleanValue, boolean trivial) {
        super(type, level, input, name, expectedBooleanValue, trivial);
        this.positionInEntries = positionInEntries;
      }

      Finalized result(Object result, Object expectationDetail, Object actualInputDetail) {
        return new Finalized(this.type(), this.level(), this.input(), result, this.formName(), this.expectedBooleanValue(), expectationDetail, actualInputDetail, this.trivial);
      }

      @Override
      public boolean hasOutput() {
        return false;
      }

      @Override
      public <T> T output() {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean hasExpectationDetail() {
        return false;
      }

      @Override
      public Object expectationDetail() {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean hasActualInputDetail() {
        return false;
      }

      @Override
      public Object actualInputDetail() {
        throw new UnsupportedOperationException();
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

    Object explainActualInput(Object actualInputValue);

    static Object explainExpectationIfPossibleOrNull(Object value) {
      if (value instanceof Explainable)
        return explainExpectation((Explainable) value);
      return null;
    }

    static String explainExpectation(Explainable value) {
      return explainValue(value.explainExpectation());
    }

    static Object explainActualInputIfPossibleOrNull(Object value, Object actualInputValue) {
      if (value instanceof Explainable)
        return explainActualValue((Explainable) value, actualInputValue);
      return null;
    }

    static String explainActualValue(Explainable value, Object actualInputValue) {
      return explainValue(value.explainActualInput(actualInputValue));
    }
  }
}