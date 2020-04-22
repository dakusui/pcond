package com.github.dakusui.pcond.functions;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;

public interface Evaluator {
  <T> void evaluate(T value, Evaluable.Conjunction<T> conjunction);

  <T> void evaluate(T value, Evaluable.Disjunction<T> disjunction);

  <T> void evaluate(T value, Evaluable.Negation<T> negation);

  <T> void evaluate(T value, Evaluable.Leaf<T> leaf);

  <T, R> void evaluate(T value, Evaluable.Transformation<T, R> transformation);

  <T> void evaluate(T value, Evaluable.Func<T> func);

  <T> T resultValue();

  static Evaluator create() {
    return new Impl();
  }

  static <T> Result evaluate(T value, Evaluable<T> evaluable) {
    Evaluator evaluator = create();
    Objects.requireNonNull(evaluable).accept(value, evaluator);
    return new Result(evaluator.resultValue(), evaluator.resultRecords());
  }


  List<Result.Record> resultRecords();

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
    public <T> void evaluate(T value, Evaluable.Leaf<T> leaf) {
      boolean result = leaf.predicate().test(value);
      record(String.format("%s", leaf), value, result);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, R> void evaluate(T value, Evaluable.Transformation<T, R> transformation) {
      enter("check", value);
      transformation.mapper().accept(value, this);
      //record(String.format("%s", transformation.mapper()), value, transformedValue);
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T resultValue() {
      return (T) currentResult;
    }

    @Override
    public List<Result.Record> resultRecords() {
      return unmodifiableList(this.records);
    }
  }
}
