package com.github.dakusui.pcond.functions;

import java.util.*;

import static java.util.Collections.unmodifiableList;

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
      final Object result;

      FinalizedRecord(int level, Object value, Object result, String name) {
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

    /**
     * Records an operation of the name.
     *
     * @param name   The name of the operation
     * @param value  Input to the operation
     * @param result The output of the operation
     */
    void record(String name, Object value, Object result) {
      this.records.add(new Result.FinalizedRecord(onGoingRecords.size(), value, result, name));
    }

    void enter(String name, Object value) {
      Result.OnGoingRecord newRecord = new Result.OnGoingRecord(onGoingRecords.size(), records.size(), value, name);
      onGoingRecords.add(newRecord);
      records.add(newRecord);
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
      enter("not", value);
      return leave(!negation.target().accept(value, this));
    }

    @Override
    public <T> boolean evaluate(T value, Evaluable.Leaf<T> leaf) {
      boolean result = leaf.predicate().test(value);
      record(String.format("leaf:%s", leaf), value, result);
      return result;
    }

    @Override
    public <T, R> boolean evaluate(T value, Evaluable.Transformation<T, R> transformation) {
      R transformedValue = transformation.mapper().apply(value);
      record(String.format("transform:%s", transformation.mapper()), value, transformedValue);
      return transformation.checker().accept(transformedValue, this);
    }

    @Override
    public List<Result.Record> resultRecords() {
      return unmodifiableList(this.records);
    }
  }
}
