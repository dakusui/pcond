package com.github.dakusui.pcond.core;

public class EvaluationResultHolder<V> implements Cloneable {

  private EvaluationContext.State state;
  V                       value;
  Throwable exception;

  private EvaluationResultHolder(EvaluationContext.State state, V value, Throwable exception) {
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

  public EvaluationContext.State state() {
    return this.state;
  }

  public V returnedValue() {
    return this.state.value(this);
  }

  public Throwable thrownException() {
    return this.state.exception(this);
  }

  public EvaluationResultHolder<V> valueReturned(V returnedValue) {
    this.state = EvaluationContext.State.VALUE_RETURNED;
    this.value = returnedValue;
    this.exception = null;
    return this;
  }

  public EvaluationResultHolder<V> exceptionThrown(Throwable thrownException) {
    this.state = EvaluationContext.State.EXCEPTION_THROWN;
    this.exception = thrownException;
    this.value = null;
    return this;
  }

  public Object value() {
    if (this.state() == EvaluationContext.State.VALUE_RETURNED)
      return this.returnedValue();
    return this.thrownException();
  }

  public static <V> EvaluationResultHolder<V> forValue(V value) {
    return new EvaluationResultHolder<>(EvaluationContext.State.VALUE_RETURNED, value, null);
  }

  public String toString() {
    return String.format("state:%s, value:%s, exception:%s", state, value, exception);
  }
}
