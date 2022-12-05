package com.github.dakusui.pcond.core;

import static com.github.dakusui.pcond.core.Evaluator.Impl.EVALUATION_SKIPPED;
import static com.github.dakusui.pcond.internals.InternalChecks.requireState;
import static java.util.Objects.requireNonNull;

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

  public Object value() {
    if (isValueReturned())
      return this.returnedValue();
    if (isEvaluationSkipped())
      return EVALUATION_SKIPPED;
    if (isExceptionThrown())
      return this.thrownException();
    throw new IllegalStateException();
  }

  public static <V> EvaluationResultHolder<V> forValue(V value) {
    return new EvaluationResultHolder<>(State.VALUE_RETURNED, value, null);
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
      <V> V value(EvaluationResultHolder<V> evaluationResultHolder) {
        return evaluationResultHolder.value;
      }
    },
    EXCEPTION_THROWN {
      <V> Throwable exception(EvaluationResultHolder<V> vContextVariable) {
        return vContextVariable.exception;
      }
    },
    EVALUATION_SKIPPED {
    };

    <V> V value(EvaluationResultHolder<V> evaluationResultHolder) {
      throw new IllegalStateException();
    }

    <V> Throwable exception(EvaluationResultHolder<V> vContextVariable) {
      throw new IllegalStateException();
    }
  }

  public EvaluationResultHolder<V> valueReturned(V value) {
    requireState(this.state, v -> v.equals(State.NOT_YET_EVALUATED), v -> messageNotYetEvaluatedStateIsRequired(v, this));
    return new EvaluationResultHolder<>(State.VALUE_RETURNED, requireNonNull(value), null);
  }

  public EvaluationResultHolder<V> exceptionThrown(Throwable throwable) {
    requireState(this.state, v -> v.equals(State.NOT_YET_EVALUATED), v -> messageNotYetEvaluatedStateIsRequired(v, this));
    return new EvaluationResultHolder<>(State.EXCEPTION_THROWN, null, requireNonNull(throwable));
  }

  public EvaluationResultHolder<V> evaluationSkipped() {
    requireState(this.state, v -> v.equals(State.NOT_YET_EVALUATED), v -> messageNotYetEvaluatedStateIsRequired(v, this));
    return new EvaluationResultHolder<>(State.EVALUATION_SKIPPED, null, null);
  }

  static <E> EvaluationResultHolder<E> create() {
    return new EvaluationResultHolder<>(State.NOT_YET_EVALUATED, null, null);
  }

  private static String messageNotYetEvaluatedStateIsRequired(State v, Object thisValue) {
    return "state:<" + State.NOT_YET_EVALUATED + "> of <" + thisValue + "> is expected but it was <" + v + ">";
  }
}
