package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.core.context.Context;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.Evaluator.Explainable.explainActualInputIfPossibleOrNull;
import static com.github.dakusui.pcond.core.Evaluator.Explainable.explainExpectationIfPossibleOrNull;
import static com.github.dakusui.pcond.core.Evaluator.Snapshottable.toSnapshotIfPossible;
import static com.github.dakusui.pcond.internals.InternalUtils.wrapIfNecessary;
import static java.util.Collections.unmodifiableList;

/**
 * A visitor interface that defines a mechanism to "evaluate" printable predicates.
 */
public interface Evaluator {
  <T> void evaluate(T value, Evaluable.Conjunction<T> conjunction);

  <T> void evaluate(T value, Evaluable.Disjunction<T> disjunction);

  <T> void evaluate(T value, Evaluable.Negation<T> negation);

  <T> void evaluate(T value, Evaluable.LeafPred<T> leafPred);

  <T> void evaluate(T value, Evaluable.Messaged<T> messaged);

  void evaluate(Context value, Evaluable.ContextPred contextPred);

  <T, R> void evaluate(T value, Evaluable.Transformation<T, R> transformation);

  <T> void evaluate(T value, Evaluable.Func<T> func);

  <E> void evaluate(Stream<? extends E> value, Evaluable.StreamPred<E> streamPred);

  <T> T resultValue();

  List<Entry> resultEntries();


  Evaluator copyEvaluator();

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
      if (evaluable.requestExpectationFlip())
        this.flipCurrentlyExpectedBooleanValue();

      Entry.OnGoing newEntry = new Entry.OnGoing(type, onGoingEntries.size(), entries.size(), name, toSnapshotIfPossible(input), this.currentlyExpectedBooleanValue);
      onGoingEntries.add(newEntry);
      entries.add(newEntry);
    }

    void leave(Object result, Evaluable<?> evaluable, boolean unexpected) {
      int positionInOngoingEntries = onGoingEntries.size() - 1;
      Entry.OnGoing current = onGoingEntries.get(positionInOngoingEntries);
      entries.set(
          current.positionInEntries,
          current.result(
              toSnapshotIfPossible(result),
              unexpected ? explainExpectationIfPossibleOrNull(evaluable) : null,
              unexpected ? explainActualInputIfPossibleOrNull(evaluable) : null));
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
    public <T> void evaluate(T value, Evaluable.Conjunction<T> conjunction) {
      int i = 0;
      boolean finalValue = true;
      boolean shortcut = conjunction.shortcut();
      for (Evaluable<? super T> each : conjunction.children()) {
        if (i == 0)
          this.enter(Entry.Type.AND, conjunction, "&&", value);
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
    public <T> void evaluate(T value, Evaluable.Disjunction<T> disjunction) {
      int i = 0;
      boolean finalValue = false;
      boolean shortcut = disjunction.shortcut();
      for (Evaluable<? super T> each : disjunction.children()) {
        if (i == 0)
          this.enter(Entry.Type.OR, disjunction, "||", value);
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
    public <T> void evaluate(T value, Evaluable.Negation<T> negation) {
      this.enter(Entry.Type.NOT, negation, "!", value);
      negation.target().accept(value, this);
      this.leave(!this.<Boolean>resultValue(), negation, false);
    }

    @Override
    public <T> void evaluate(T value, Evaluable.LeafPred<T> leafPred) {
      this.enter(Entry.Type.LEAF, leafPred, String.format("%s", leafPred), value);
      boolean result = leafPred.predicate().test(value);
      this.leave(result, leafPred, this.currentlyExpectedBooleanValue != result);
    }

    @Override
    public <T> void evaluate(T value, Evaluable.Messaged<T> messaged) {
      this.enter(Entry.Type.MESSAGED, messaged, messaged.message(), value);
      messaged.target().accept(value, this);
      this.leave(this.<Boolean>resultValue(), messaged, false);
    }

    @Override
    public void evaluate(Context context, Evaluable.ContextPred contextPred) {
      this.enter(Entry.Type.LEAF, contextPred, String.format("%s", contextPred), context);
      contextPred.enclosed().accept(context.valueAt(contextPred.argIndex()), this);
      this.leave(this.resultValue(), contextPred, false);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public <T, R> void evaluate(T value, Evaluable.Transformation<T, R> transformation) {
      this.enter(Entry.Type.TRANSFORM,
          transformation.mapper(),
          transformation.mapperName()
              .orElse("transform"), value);
      transformation.mapper().accept(value, this);
      this.leave(this.resultValue(), transformation.checker(), false);
      this.enter(Entry.Type.CHECK,
          transformation.checker(),
          transformation.checkerName()
              .orElse("check"),
          this.resultValue());
      transformation.checker().accept((R) this.currentResult, this);
      this.leave(this.resultValue(), transformation.mapper(), false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void evaluate(T value, Evaluable.Func<T> func) {
      this.enter(Entry.Type.FUNCTION, func, String.format("%s", func.head()), value);
      Object resultValue = func.head().apply(value);
      this.leave(resultValue, func, false);
      func.tail().ifPresent(tailSide -> ((Evaluable<Object>) tailSide).accept(resultValue, this));
    }

    @Override
    public <E> void evaluate(Stream<? extends E> value, Evaluable.StreamPred<E> streamPred) {
      boolean ret = streamPred.defaultValue();
      this.enter(Entry.Type.LEAF, streamPred, String.format("%s", streamPred), value);
      // Use NULL_VALUE object instead of null. Otherwise, the operation will fail with NullPointerException
      // on 'findFirst()'.
      // Although NULL_VALUE is an ordinary Object, not a value of E, this works
      // because either way we will just return a boolean and during the execution,
      // type information is erased.
      this.leave(value
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
          streamPred.cut().accept(e, evaluator);
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

    @Override
    public Evaluator copyEvaluator() {

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
          each.hasActualInputDetail() ? each.actualInputDetail() : null);
    }
  }

  abstract class Entry {
    private final Type    type;
    private final int     level;
    private final Object  input;
    private final String  name;
    private final boolean expectedBooleanValue;

    Entry(Type type, int level, Object input, String name, boolean expectedBooleanValue) {
      this.type = type;
      this.level = level;
      this.input = input;
      this.name = name;
      this.expectedBooleanValue = expectedBooleanValue;
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

    public enum Type {
      TRANSFORM,
      CHECK,
      AND,
      OR,
      NOT,
      LEAF,
      FUNCTION,
      MESSAGED,
    }

    static class Finalized extends Entry {
      final         Object output;
      final         Object expectationDetail;
      private final Object actualInputDetail;

      Finalized(Type type, int level, Object input, Object output, String name, boolean expectedBooleanValue, Object expectationDetail, Object actualInputDetail) {
        super(type, level, input, name, expectedBooleanValue);
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

      OnGoing(Type type, int level, int positionInEntries, String name, Object input, boolean expectedBooleanValue) {
        super(type, level, input, name, expectedBooleanValue);
        this.positionInEntries = positionInEntries;
      }

      Finalized result(Object result, Object expectationDetail, Object actualInputDetail) {
        return new Finalized(this.type(), this.level(), this.input(), result, this.formName(), this.expectedBooleanValue(), expectationDetail, actualInputDetail);
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
   * The value returned by {@link this#snapshot()} method is stored in a {@link Evaluator.Entry}
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

  interface Explainable {
    Object explainExpectation();

    Object explainActualInput();

    static Object explainExpectationIfPossibleOrNull(Object value) {
      if (value instanceof Explainable)
        return ((Explainable) value).explainExpectation();
      return null;
    }

    static Object explainActualInputIfPossibleOrNull(Object value) {
      if (value instanceof Explainable)
        return ((Explainable) value).explainActualInput();
      return null;
    }
  }
}