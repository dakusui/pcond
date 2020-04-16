package com.github.dakusui.pcond.functions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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

    static class Record {
      final int    level;
      final Object value;
      final String message;

      Record(int level, Object value, String message) {
        this.level = level;
        this.value = value;
        this.message = message;
      }
    }
  }

  class Impl implements Evaluator {
    int                 level   = 0;
    List<Result.Record> records = new LinkedList<>();

    void newRecord(Object value, String message) {
      this.records.add(new Result.Record(level, value, message));
    }

    void enter() {
      this.level++;
    }

    void leave() {
      this.level--;
    }

    @Override
    public <T> boolean evaluate(T value, Evaluable.Conjunction<T> conjunction) {
      newRecord(value, "and");
      enter();
      try {
        if (!conjunction.a().accept(value, this))
          return false;
        return conjunction.b().accept(value, this);
      } finally {
        leave();
      }
    }

    @Override
    public <T> boolean evaluate(T value, Evaluable.Disjunction<T> disjunction) {
      newRecord(value, "or");
      enter();
      try {
        if (disjunction.a().accept(value, this))
          return true;
        return disjunction.b().accept(value, this);
      } finally {
        leave();
      }
    }

    @Override
    public <T> boolean evaluate(T value, Evaluable.Negation<T> negation) {
      newRecord(value, "or");
      enter();
      try {
        return !negation.body().accept(value, this);
      } finally {
        leave();
      }
    }

    @Override
    public <T> boolean evaluate(T value, Evaluable.Leaf<T> leaf) {
      newRecord(value, String.format("leaf:%s", leaf.predicate()));
      return leaf.predicate().test(value);
    }

    @Override
    public <T, R> boolean evaluate(T value, Evaluable.Transformation<T, R> transformation) {
      newRecord(value, String.format("transform:%s", transformation.mapper()));
      enter();
      try {
        return transformation.checker().accept(transformation.mapper().apply(value), this);
      } finally {
        leave();
      }
    }

    @Override
    public List<Result.Record> resultRecords() {
      return Collections.unmodifiableList(this.records);
    }
  }
}
