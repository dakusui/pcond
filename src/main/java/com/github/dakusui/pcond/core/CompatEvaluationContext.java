package com.github.dakusui.pcond.core;

public class CompatEvaluationContext<V> implements Cloneable {
  public void resetTo(CompatEvaluationContext<V> clonedContext) {
    this.state = clonedContext.state();
    this.value = clonedContext.value;
    this.exception = clonedContext.exception;
  }

  enum State {
    VALUE_RETURNED {
      <V> V value(CompatEvaluationContext<V> vContextVariable) {
        return vContextVariable.value;
      }

      <V> Throwable exception(CompatEvaluationContext<V> vContextVariable) {
        throw new UnsupportedOperationException();
      }
    },
    EXCEPTION_THROWN {
      <V> V value(CompatEvaluationContext<V> vContextVariable) {
        throw new UnsupportedOperationException();
      }

      <V> Throwable exception(CompatEvaluationContext<V> vContextVariable) {
        return vContextVariable.exception;
      }
    };

    abstract <V> V value(CompatEvaluationContext<V> vContextVariable);

    abstract <V> Throwable exception(CompatEvaluationContext<V> vContextVariable);
  }

  private State     state;
  private V         value;
  private Throwable exception;

  private CompatEvaluationContext(State state, V value, Throwable exception) {
    this.state = state;
    this.value = value;
    this.exception = exception;
  }

  @SuppressWarnings("unchecked")
  public CompatEvaluationContext<V> clone() {
    try {
      return (CompatEvaluationContext<V>) super.clone();
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

  public CompatEvaluationContext<V> valueReturned(V returnedValue) {
    this.state = State.VALUE_RETURNED;
    this.value = returnedValue;
    this.exception = null;
    return this;
  }

  public CompatEvaluationContext<V> exceptionThrown(Throwable thrownException) {
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

  public static <V> CompatEvaluationContext<V> forValue(V value) {
    return new CompatEvaluationContext<>(State.VALUE_RETURNED, value, null);
  }

  public String toString() {
    return String.format("state:%s, value:%s, exception:%s", state, value, exception);
  }
}
