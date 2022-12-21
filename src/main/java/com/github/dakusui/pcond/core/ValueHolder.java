package com.github.dakusui.pcond.core;

import static com.github.dakusui.pcond.core.Evaluator.Impl.EVALUATION_SKIPPED;
import static java.util.Objects.requireNonNull;

public class ValueHolder<V> implements Cloneable {

  private final State state;
  V         value;
  Throwable exception;

  private ValueHolder(State state, V value, Throwable exception) {
    this.state = state;
    this.value = value;
    this.exception = exception;
  }

  @SuppressWarnings("unchecked")
  public ValueHolder<V> clone() {
    try {
      return (ValueHolder<V>) super.clone();
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

  public Object value() {
    if (isValueReturned())
      return this.returnedValue();
    if (isEvaluationSkipped())
      return EVALUATION_SKIPPED;
    if (isExceptionThrown())
      return this.thrownException();
    throw new IllegalStateException();
  }

  public static <V> ValueHolder<V> forValue(V value) {
    return new ValueHolder<>(State.VALUE_RETURNED, value, null);
  }

  public String toString() {
    return String.format("state:%s, value:%s, exception:%s", state, value, exception);
  }

  public boolean isValueReturned() {
    return this.state() == State.VALUE_RETURNED;
  }

  public boolean isExceptionThrown() {
    return this.state() == State.EXCEPTION_THROWN;
  }

  public boolean isEvaluationSkipped() {
    return this.state() == State.EVALUATION_SKIPPED;
  }

  public boolean wasEvaluationAttempted() {
    return this.state() != State.NOT_YET_EVALUATED;
  }

  public enum State {
    NOT_YET_EVALUATED {
    },
    VALUE_RETURNED {
      <V> V value(ValueHolder<V> valueHolder) {
        return valueHolder.value;
      }
    },
    EXCEPTION_THROWN {
      <V> Throwable exception(ValueHolder<V> vContextVariable) {
        return vContextVariable.exception;
      }
    },
    EVALUATION_SKIPPED {
    };

    <V> V value(ValueHolder<V> valueHolder) {
      throw new IllegalStateException();
    }

    <V> Throwable exception(ValueHolder<V> vContextVariable) {
      throw new IllegalStateException();
    }
  }

  public ValueHolder<V> valueReturned(V value) {
//    requireState(this.state, v -> v.equals(State.NOT_YET_EVALUATED), v -> messageNotYetEvaluatedStateIsRequired(v, this));
    return new ValueHolder<>(State.VALUE_RETURNED, requireNonNull(value), null);
  }

  public ValueHolder<V> exceptionThrown(Throwable throwable) {
//    requireState(this.state, v -> v.equals(State.NOT_YET_EVALUATED), v -> messageNotYetEvaluatedStateIsRequired(v, this));
    return new ValueHolder<>(State.EXCEPTION_THROWN, null, requireNonNull(throwable));
  }

  public ValueHolder<V> evaluationSkipped() {
//    requireState(this.state, v -> v.equals(State.NOT_YET_EVALUATED), v -> messageNotYetEvaluatedStateIsRequired(v, this));
    return new ValueHolder<>(State.EVALUATION_SKIPPED, null, null);
  }

  static <E> ValueHolder<E> create() {
    return new ValueHolder<>(State.NOT_YET_EVALUATED, null, null);
  }

  private static String messageNotYetEvaluatedStateIsRequired(State v, Object thisValue) {
    return "state:<" + State.NOT_YET_EVALUATED + "> of <" + thisValue + "> is expected but it was <" + v + ">";
  }
}
