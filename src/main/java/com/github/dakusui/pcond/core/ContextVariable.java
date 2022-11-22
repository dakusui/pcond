package com.github.dakusui.pcond.core;

import static com.github.dakusui.pcond.core.Evaluator.Snapshottable.toSnapshotIfPossible;
import static java.lang.String.format;

public class ContextVariable<V> {
  enum Type {
    VALUE {
      <V> V value(ContextVariable<V> vContextVariable) {
        return vContextVariable.value;
      }

      <V> Throwable exception(ContextVariable<V> vContextVariable) {
        throw new UnsupportedOperationException();
      }
    },
    EXCEPTION {
      <V> V value(ContextVariable<V> vContextVariable) {
        throw new UnsupportedOperationException();
      }

      <V> Throwable exception(ContextVariable<V> vContextVariable) {
        return vContextVariable.exception;
      }
    };

    abstract <V> V value(ContextVariable<V> vContextVariable);

    abstract <V> Throwable exception(ContextVariable<V> vContextVariable);
  }

  final Type      type;
  final V         value;
  final Throwable exception;

  private ContextVariable(Type type, V value, Throwable exception) {
    this.type = type;
    this.value = value;
    this.exception = exception;
  }

  public Type type() {
    return this.type;
  }

  public V returnedValue() {
    return this.type.value(this);
  }

  public Throwable thrownException() {
    return this.type.exception(this);
  }

  public Object value() {
    if (this.type() == Type.VALUE)
      return this.returnedValue();
    return this.thrownException();
  }

  public Object toSnapshot() {
    return toSnapshotIfPossible(this.value());
  }

  public static <V> ContextVariable<V> forValue(V value) {
    return new ContextVariable<>(Type.VALUE, value, null);
  }

  public static <V> ContextVariable<V> forException(Throwable exception) {
    return new ContextVariable<>(Type.EXCEPTION, null, exception);
  }

  public String toString() {
    return String.format("type:%s, value:%s, exception:%s", type, value, exception);
  }
}
