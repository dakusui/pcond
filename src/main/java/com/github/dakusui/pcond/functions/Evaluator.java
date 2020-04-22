package com.github.dakusui.pcond.functions;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;

public interface Evaluator {
  <T> void evaluate(T value, Evaluable.Conjunction<T> conjunction);

  <T> void evaluate(T value, Evaluable.Disjunction<T> disjunction);

  <T> void evaluate(T value, Evaluable.Negation<T> negation);

  <T> void evaluate(T value, Evaluable.LeafPred<T> leafPred);

  <T, R> void evaluate(T value, Evaluable.Transformation<T, R> transformation);

  <T> void evaluate(T value, Evaluable.Func<T> func);

  <E> void evaluate(Stream<E> value, Evaluable.StreamPred<E> streamPred);

  <T> T resultValue();

  List<Result.Record> resultRecords();

  static Evaluator create() {
    return new Impl();
  }

  static <T> Result evaluate(T value, Evaluable<T> evaluable) {
    Evaluator evaluator = create();
    Objects.requireNonNull(evaluable).accept(value, evaluator);
    return new Result(evaluator.resultValue(), evaluator.resultRecords());
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

    public Stream<Record> stream() {
      return this.records.stream();
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

      Record(int level, String name) {
        this.level = level;
        this.input = null;
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

      public abstract <T> Optional<T> output();
    }

    static class FinalizedRecord extends Record {
      final Object output;

      FinalizedRecord(int level, Object input, Object output, String name) {
        super(level, input, name);
        this.output = output;
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T> Optional<T> output() {
        return Optional.of((T) output);
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
      public <T> Optional<T> output() {
        return Optional.empty();
      }
    }
  }

  class Impl implements Evaluator {
    private static final Object NULL_VALUE = new Object();
    List<Result.OnGoingRecord> onGoingRecords = new LinkedList<>();
    List<Result.Record>        records        = new ArrayList<>();
    Object                     currentResult;

    /**
     * Records an operation of the name.
     *
     * @param name   The name of the operation
     * @param value  Input to the operation
     * @param result The output of the operation
     */
    void record(String name, Object value, Object result) {
      this.records.add(new Result.FinalizedRecord(onGoingRecords.size(), value, result, name));
      this.currentResult = result;
    }

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
      boolean result = leafPred.predicate().test(value);
      record(String.format("%s", leafPred), value, result);
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
      Object resultValue = func.head().apply(value);
      record(String.format("%s", func.head()), value, resultValue);
      func.tail().ifPresent(tailSide -> {
        ((Evaluable<Object>) tailSide).accept(resultValue, this);
      });
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
      leave(value.filter(valueChecker(streamPred))
          .map(v -> v != null ? v : NULL_VALUE)
          .findFirst()
          .map(each -> streamPred.valueOnCut())
          .orElse(ret));
    }

    private <E> Predicate<E> valueChecker(Evaluable.StreamPred<E> streamPred) {
      return e -> {
        Evaluator evaluator = new Impl();
        streamPred.cut().accept(e, evaluator);
        if (evaluator.resultValue()) {
          importResultRecords(evaluator.resultRecords());
          return true;
        }
        return false;
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

    public void importResultRecords(List<Result.Record> resultRecords) {
      resultRecords.stream()
          .map(this::createRecordForImport)
          .forEach(each -> this.records.add(each));
    }

    private Result.FinalizedRecord createRecordForImport(Result.Record each) {
      return new Result.FinalizedRecord(this.onGoingRecords.size() + each.level, each.input, each.output().orElseThrow(IllegalStateException::new), each.name);
    }
  }
}
