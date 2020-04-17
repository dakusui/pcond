package com.github.dakusui.pcond.functions;

import java.util.*;

public interface Evaluator {
  <T> boolean evaluate(T value, Evaluable.Conjunction<T> conjunction);

  <T> boolean evaluate(T value, Evaluable.Disjunction<T> disjunction);

  <T> boolean evaluate(T value, Evaluable.Negation<T> negation);

  <T> boolean evaluate(T value, Evaluable.Leaf<T> leaf);

  <T, R> boolean evaluate(T value, Evaluable.Transformation<T, R> transformation);

  static Evaluator create() {
    return new Impl();
  }

  static <T> Result evaluate(T value, Evaluable<T> evaluable) {
    Evaluator evaluator = create();
    return new Result(Objects.requireNonNull(evaluable).accept(value, evaluator), evaluator.resultRecords());
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

    public void formatTo(StringBuilder buffer) {
      records.forEach(r -> buffer.append(r.toString()).append(String.format("%n")));
    }

    static abstract class Record {
      final int    level;
      final Object value;
      final String name;

      Record(int level, Object value, String name) {
        this.level = level;
        this.value = value;
        this.name = name;
      }
    }

    static class FinalizedRecord extends Record {
      final boolean result;

      FinalizedRecord(int level, Object value, boolean result, String name) {
        super(level, value, name);
        this.result = result;
      }

      public String toString() {
        return String.format("%d:%s:%s:%s", level, name, value, result);
      }
    }

    static class OnGoingRecord extends Record {
      final int positionInRecords;

      OnGoingRecord(int level, int positionInRecords, Object value, String name) {
        super(level, value, name);
        this.positionInRecords = positionInRecords;
      }

      FinalizedRecord result(boolean result) {
        return new FinalizedRecord(this.level, this.value, result, this.name);
      }
    }
  }

  class Impl implements Evaluator {
    List<Result.OnGoingRecord> onGoingRecords = new LinkedList<>();
    List<Result.Record>        records        = new ArrayList<>();

    void record(boolean result, Object value, String message) {
      this.records.add(new Result.FinalizedRecord(onGoingRecords.size(), value, result, message));
    }

    Evaluator.Impl enter(String name, Object value) {
      Result.OnGoingRecord newRecord = new Result.OnGoingRecord(onGoingRecords.size(), records.size(), value, name);
      onGoingRecords.add(newRecord);
      records.add(newRecord);
      return this;
    }

    boolean leave(boolean result) {
      int positionInOngoingRecords = onGoingRecords.size() - 1;
      Result.OnGoingRecord current = onGoingRecords.get(positionInOngoingRecords);
      records.set(current.positionInRecords, current.result(result));
      onGoingRecords.remove(positionInOngoingRecords);
      return result;
    }

    @Override
    public <T> boolean evaluate(T value, Evaluable.Conjunction<T> conjunction) {
      enter("and", value);
      if (!conjunction.a().accept(value, this))
        return leave(false);
      return leave(conjunction.b().accept(value, this));
    }

    @Override
    public <T> boolean evaluate(T value, Evaluable.Disjunction<T> disjunction) {
      enter("or", value);
      if (disjunction.a().accept(value, this))
        return leave(true);
      return leave(disjunction.b().accept(value, this));
    }

    @Override
    public <T> boolean evaluate(T value, Evaluable.Negation<T> negation) {
      enter("negate", value);
      return leave(!negation.body().accept(value, this));
    }

    @Override
    public <T> boolean evaluate(T value, Evaluable.Leaf<T> leaf) {
      boolean result = leaf.predicate().test(value);
      record(result, value, String.format("leaf:%s", leaf));
      return result;
    }

    @Override
    public <T, R> boolean evaluate(T value, Evaluable.Transformation<T, R> transformation) {
      return enter(String.format("transform:%s", transformation.mapper()), value).leave(transformation.checker().accept(transformation.mapper().apply(value), this));
    }

    @Override
    public List<Result.Record> resultRecords() {
      return Collections.unmodifiableList(this.records);
    }
  }
}
