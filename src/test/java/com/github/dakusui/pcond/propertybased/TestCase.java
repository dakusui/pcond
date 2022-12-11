package com.github.dakusui.pcond.propertybased;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

interface TestCase<V, T extends Throwable> {
  Predicate<V> targetPredicate();

  V targetValue();

  Optional<Expectation<T>> expectationForThrownException();

  Optional<Expectation<V>> expectationForReturnedValue();

  interface Expectation<E> {
    Class<E> expectedClass();

    List<Predicate<E>> checks();
  }

  abstract class Builder<V, T extends Throwable> {
    private final V            value;
    private final Predicate<V> predicate;
    Expectation<V> expectationForReturnedValue   = null;
    Expectation<T> expectationForThrownException = null;

    public Builder(V value, Predicate<V> predicate) {
      this.value = value;
      this.predicate = requireNonNull(predicate);
    }

    public TestCase<V, T> build() {
      return new TestCase<V, T>() {
        @Override
        public Predicate<V> targetPredicate() {
          return predicate;
        }

        @Override
        public V targetValue() {
          return value;
        }

        @Override
        public Optional<Expectation<T>> expectationForThrownException() {
          return Optional.ofNullable(expectationForThrownException);
        }

        @Override
        public Optional<Expectation<V>> expectationForReturnedValue() {
          return Optional.ofNullable(expectationForReturnedValue);
        }
      };
    }

    public static class ForReturnedValue<V> extends Builder<V, Throwable> {
      private final Class<V>           expectedClass;
      private final List<Predicate<V>> expectations = new LinkedList<>();

      public ForReturnedValue(V value, Predicate<V> predicate, Class<V> expectedClass) {
        super(value, predicate);
        this.expectedClass = expectedClass;
      }

      public ForReturnedValue<V> addExpectationPredicate(Predicate<V> predicate) {
        this.expectations.add(predicate);
        return this;
      }

      @Override
      public TestCase<V, Throwable> build() {
        this.expectationForReturnedValue = new Expectation<V>() {
          @Override
          public Class<V> expectedClass() {
            return expectedClass;
          }

          @Override
          public List<Predicate<V>> checks() {
            return expectations;
          }
        };
        return super.build();
      }
    }

    public static class ForThrownException<V, T extends Throwable> extends Builder<V, T> {
      private final List<Predicate<T>> expectations = new LinkedList<>();
      private final Class<T>           expectedExceptionClass;

      public ForThrownException(V value, Predicate<V> predicate, Class<T> expectedExceptionClass) {
        super(value, predicate);
        this.expectedExceptionClass = requireNonNull(expectedExceptionClass);
      }

      public ForThrownException<V, T> addExpectationPredicate(Predicate<T> predicate) {
        this.expectations.add(predicate);
        return this;
      }

      @Override
      public TestCase<V, T> build() {
        this.expectationForThrownException = new Expectation<T>() {
          @Override
          public Class<T> expectedClass() {
            return expectedExceptionClass;
          }

          @Override
          public List<Predicate<T>> checks() {
            return expectations;
          }
        };
        return super.build();
      }
    }
  }
}
