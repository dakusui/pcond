package com.github.dakusui.pcond.core;

import static com.github.dakusui.pcond.core.Evaluator.Snapshottable.toSnapshotIfPossible;

public class EvaluationContext<V> {
  enum State {
    VALUE_RETURNED {
      <V> V value(EvaluationContext<V> vContextVariable) {
        return vContextVariable.value;
      }

      <V> Throwable exception(EvaluationContext<V> vContextVariable) {
        throw new UnsupportedOperationException();
      }
    },
    EXCEPTION_THROWN {
      <V> V value(EvaluationContext<V> vContextVariable) {
        throw new UnsupportedOperationException();
      }

      <V> Throwable exception(EvaluationContext<V> vContextVariable) {
        return vContextVariable.exception;
      }
    };

    abstract <V> V value(EvaluationContext<V> vContextVariable);

    abstract <V> Throwable exception(EvaluationContext<V> vContextVariable);
  }

  State state;
  V     value;
  Throwable exception;

  private EvaluationContext(State state, V value, Throwable exception) {
    this.state = state;
    this.value = value;
    this.exception = exception;
  }

  public State state() {
    return this.state;
  }

  public V returnedValue() {
    return this.state.value(this);
  }

  public Throwable thrownException() {
    return this.state.exception(this);
  }

  public EvaluationContext<V> valueReturned(V returnedValue) {
    this.state = State.VALUE_RETURNED;
    this.value = returnedValue;
    this.exception = null;
    return this;
  }

  public EvaluationContext<V> exceptionThrown(Throwable thrownException) {
    this.state = State.EXCEPTION_THROWN;
    this.exception = thrownException;
    //this.value = null;
    return this;
  }

  public Object value() {
    if (this.state() == State.VALUE_RETURNED)
      return this.returnedValue();
    return this.thrownException();
  }

  public V currentValue() {
    return this.value;
  }

  public Object toSnapshot() {
    return toSnapshotIfPossible(this.currentValue());
  }

  public static <V> EvaluationContext<V> forValue(V value) {
    return new EvaluationContext<>(State.VALUE_RETURNED, value, null);
  }

  public String toString() {
    return String.format("state:%s, value:%s, exception:%s", state, value, exception);
  }
}
