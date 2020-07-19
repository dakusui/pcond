package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.core.context.Context;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

  void evaluate(Context value, Evaluable.ContextPred contextPred);

  <T, R> void evaluate(T value, Evaluable.Transformation<T, R> transformation);

  <T> void evaluate(T value, Evaluable.Func<T> func);

  <E> void evaluate(Stream<E> value, Evaluable.StreamPred<E> streamPred);

  <T> T resultValue();

  List<Entry> resultEntries();

  static Evaluator create() {
    return new Impl();
  }

  class Impl implements Evaluator {
    private static final Object NULL_VALUE = new Object();
    List<Entry.OnGoing> onGoingEntries = new LinkedList<>();
    List<Entry>         entries        = new ArrayList<>();
    Object              currentResult;

    void enter(String name, Object input) {
      Entry.OnGoing newEntry = new Entry.OnGoing(onGoingEntries.size(), entries.size(), name, input);
      onGoingEntries.add(newEntry);
      entries.add(newEntry);
    }

    void leave(Object result) {
      int positionInOngoingEntries = onGoingEntries.size() - 1;
      Entry.OnGoing current = onGoingEntries.get(positionInOngoingEntries);
      entries.set(current.positionInEntries, current.result(result));
      onGoingEntries.remove(positionInOngoingEntries);
      this.currentResult = result;
    }

    @Override
    public <T> void evaluate(T value, Evaluable.Conjunction<T> conjunction) {
      enter("&&", value);
      conjunction.a().accept(value, this);
      if (!this.<Boolean>resultValue()) {
        leave(false);
        return;
      }
      conjunction.b().accept(value, this);
      leave(this.resultValue());
    }

    @Override
    public <T> void evaluate(T value, Evaluable.Disjunction<T> disjunction) {
      enter("||", value);
      disjunction.a().accept(value, this);
      if (this.resultValue()) {
        leave(true);
        return;
      }
      disjunction.b().accept(value, this);
      leave(this.resultValue());
    }

    @Override
    public <T> void evaluate(T value, Evaluable.Negation<T> negation) {
      enter("!", value);
      negation.target().accept(value, this);
      leave(!this.<Boolean>resultValue());
    }

    @Override
    public <T> void evaluate(T value, Evaluable.LeafPred<T> leafPred) {
      enter(String.format("%s", leafPred), value);
      boolean result = leafPred.predicate().test(value);
      leave(result);
    }

    @Override
    public void evaluate(Context context, Evaluable.ContextPred contextPred) {
      enter(String.format("%s", contextPred), context);
      contextPred.enclosed().accept(context.valueAt(contextPred.argIndex()), this);
      leave(this.resultValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, R> void evaluate(T value, Evaluable.Transformation<T, R> transformation) {
      enter("=>", value);
      transformation.mapper().accept(value, this);
      transformation.checker().accept((R) this.currentResult, this);
      leave(this.resultValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void evaluate(T value, Evaluable.Func<T> func) {
      enter(String.format("%s", func.head()), value);
      Object resultValue = func.head().apply(value);
      leave(resultValue);
      func.tail().ifPresent(tailSide -> ((Evaluable<Object>) tailSide).accept(resultValue, this));
    }

    @Override
    public <E> void evaluate(Stream<E> value, Evaluable.StreamPred<E> streamPred) {
      boolean ret = streamPred.defaultValue();
      enter(String.format("%s", streamPred), value);
      // Use NULL_VALUE object instead of null. Otherwise, the operation will fail with NullPointerException
      // on 'findFirst()'.
      // Although NULL_VALUE is an ordinary Object, not a value of E, this works
      // because either way we will just return a boolean and during the execution,
      // type information is erased.
      leave(value
          .filter(valueChecker(streamPred))
          .map(v -> v != null ? v : NULL_VALUE)
          .findFirst()
          .map(each -> !ret)
          .orElse(ret));
    }

    private <E> Predicate<E> valueChecker(Evaluable.StreamPred<E> streamPred) {
      return e -> {
        Evaluator evaluator = Evaluator.create();
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

    public void importResultEntries(List<Entry> resultEntries, Object other) {
      resultEntries.stream()
          .map(each -> createEntryForImport(each, other))
          .forEach(each -> this.entries.add(each));
    }

    private Entry.Finalized createEntryForImport(Entry each, Object other) {
      return new Entry.Finalized(this.onGoingEntries.size() + each.level, each.input, each.hasOutput() ? each.output() : other, each.name);
    }
  }

  abstract class Entry {
    final int    level;
    final Object input;
    final String name;

    Entry(int level, Object input, String name) {
      this.level = level;
      this.input = input;
      this.name = name;
    }

    public int level() {
      return level;
    }

    @SuppressWarnings({ "unchecked" })
    public <T> T input() {
      return (T) this.input;
    }

    public String name() {
      return name;
    }

    public abstract boolean hasOutput();

    public abstract <T> T output();

    static class Finalized extends Entry {
      final Object output;

      Finalized(int level, Object input, Object output, String name) {
        super(level, input, name);
        this.output = output;
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
    }

    static class OnGoing extends Entry {
      final int positionInEntries;

      OnGoing(int level, int positionInEntries, String name, Object input) {
        super(level, input, name);
        this.positionInEntries = positionInEntries;
      }

      Finalized result(Object result) {
        return new Finalized(this.level, this.input, result, this.name);
      }

      @Override
      public boolean hasOutput() {
        return false;
      }

      @Override
      public <T> T output() {
        throw new UnsupportedOperationException();
      }
    }
  }
}
