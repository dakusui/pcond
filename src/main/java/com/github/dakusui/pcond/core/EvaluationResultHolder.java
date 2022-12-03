package com.github.dakusui.pcond.core;

public class EvaluationResultHolder<V> implements Cloneable {

  private State state;
  V         value;
  Throwable exception;

  private EvaluationResultHolder(State state, V value, Throwable exception) {
    this.state = state;
    this.value = value;
    this.exception = exception;
  }

  @SuppressWarnings("unchecked")
  public EvaluationResultHolder<V> clone() {
    try {
      return (EvaluationResultHolder<V>) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
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

  public EvaluationResultHolder<V> valueReturned(V returnedValue) {
    this.state = State.VALUE_RETURNED;
    this.value = returnedValue;
    this.exception = null;
    return this;
  }

  public EvaluationResultHolder<V> exceptionThrown(Throwable thrownException) {
    this.state = State.EXCEPTION_THROWN;
    this.exception = thrownException;
    this.value = null;
    return this;
  }

  public Object value() {
    if (this.state() == State.VALUE_RETURNED)
      return this.returnedValue();
    return this.thrownException();
  }

  public static <V> EvaluationResultHolder<V> forValue(V value) {
    return new EvaluationResultHolder<>(State.VALUE_RETURNED, value, null);
  }

  public String toString() {
    return String.format("state:%s, value:%s, exception:%s", state, value, exception);
  }

  public enum State {
    NOT_YET_EVALUATED {
      @Override
      <V> V value(EvaluationResultHolder<V> vContextVariable) {
        throw new IllegalStateException();
      }

      @Override
      <V> Throwable exception(EvaluationResultHolder<V> vContextVariable) {
        throw new IllegalStateException();
      }
    },
    VALUE_RETURNED {
      <V> V value(EvaluationResultHolder<V> vContextVariable) {
        return vContextVariable.value;
      }

      <V> Throwable exception(EvaluationResultHolder<V> vContextVariable) {
        throw new UnsupportedOperationException();
      }
    },
    EXCEPTION_THROWN {
      <V> V value(EvaluationResultHolder<V> vContextVariable) {
        throw new UnsupportedOperationException();
      }

      <V> Throwable exception(EvaluationResultHolder<V> vContextVariable) {
        return vContextVariable.exception;
      }
    };

    abstract <V> V value(EvaluationResultHolder<V> vContextVariable);

    abstract <V> Throwable exception(EvaluationResultHolder<V> vContextVariable);
  }
}
