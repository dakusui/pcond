package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.functions.Experimentals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.wrapIfNecessary;
import static java.util.Collections.unmodifiableList;

public interface Evaluator {
  <T> void evaluate(T value, Evaluable.Conjunction<T> conjunction);

  <T> void evaluate(T value, Evaluable.Disjunction<T> disjunction);

  <T> void evaluate(T value, Evaluable.Negation<T> negation);

  <T> void evaluate(T value, Evaluable.LeafPred<T> leafPred);

  void evaluate(Experimentals.Context value, Evaluable.ContextPred contextPred);

  <T, R> void evaluate(T value, Evaluable.Transformation<T, R> transformation);

  <T> void evaluate(T value, Evaluable.Func<T> func);

  <E> void evaluate(Stream<E> value, Evaluable.StreamPred<E> streamPred);

  <T> T resultValue();

  List<Result.Record> resultRecords();

  static Evaluator create() {
    return new Impl();
  }

  class Result {
    final boolean      result;
    final List<Record> records;

    public Result(boolean result, List<Record> records) {
      this.result = result;
      this.records = records;
    }

    public boolean result() {
      return this.result;
    }

    public static abstract class Record {
      final int    level;
      final Object input;
      final String name;

      Record(int level, Object input, String name) {
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
    }

    static class FinalizedRecord extends Record {
      final Object output;

      FinalizedRecord(int level, Object input, Object output, String name) {
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

    static class OnGoingRecord extends Record {
      final int positionInRecords;

      OnGoingRecord(int level, int positionInRecords, String name, Object input) {
        super(level, input, name);
        this.positionInRecords = positionInRecords;
      }

      FinalizedRecord result(Object result) {
        return new FinalizedRecord(this.level, this.input, result, this.name);
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

  class Impl implements Evaluator {
    private static final Object NULL_VALUE = new Object();
    List<Result.OnGoingRecord> onGoingRecords = new LinkedList<>();
    List<Result.Record>        records        = new ArrayList<>();
    Object                     currentResult;

    void enter(String name, Object input) {
      Result.OnGoingRecord newRecord = new Result.OnGoingRecord(onGoingRecords.size(), records.size(), name, input);
      onGoingRecords.add(newRecord);
      records.add(newRecord);
    }

    void leave(Object result) {
      int positionInOngoingRecords = onGoingRecords.size() - 1;
      Result.OnGoingRecord current = onGoingRecords.get(positionInOngoingRecords);
      records.set(current.positionInRecords, current.result(result));
      onGoingRecords.remove(positionInOngoingRecords);
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
    public void evaluate(Experimentals.Context context, Evaluable.ContextPred contextPred) {
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
            importResultRecords(evaluator.resultRecords(), throwable);
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
    public List<Result.Record> resultRecords() {
      return unmodifiableList(this.records);
    }

    public void importResultRecords(List<Result.Record> resultRecords, Object other) {
      resultRecords.stream()
          .map(each1 -> createRecordForImport(each1, other))
          .forEach(each -> this.records.add(each));
    }

    private Result.FinalizedRecord createRecordForImport(Result.Record each, Object other) {
      return new Result.FinalizedRecord(this.onGoingRecords.size() + each.level, each.input, each.hasOutput() ? each.output() : other, each.name);
    }
  }
}
