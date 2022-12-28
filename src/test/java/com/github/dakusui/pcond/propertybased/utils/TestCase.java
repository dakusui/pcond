package com.github.dakusui.pcond.propertybased.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.propertybased.utils.ReportCheckUtils.makePrintableFunction;
import static java.util.Objects.requireNonNull;

public interface TestCase<V, T extends Throwable> {
  Predicate<V> targetPredicate();

  V targetValue();

  Optional<Expectation<T>> expectationForThrownException();

  Optional<Expectation<V>> expectationForReturnedValue();

  interface Expectation<E> {
    Class<E> expectedClass();

    List<Builder.ForThrownException.TransformingPredicateForPcondUT<E, ?>> checks();
  }

  abstract class Builder<B extends Builder<B, V, T>, V, T extends Throwable> {
    private final V            value;
    private       Predicate<V> predicate;
    Expectation<V> expectationForReturnedValue   = null;
    Expectation<T> expectationForThrownException = null;

    public Builder(V value) {
      this.value = value;
    }

    public Builder(V value, Predicate<V> predicate) {
      this(value);
      this.predicate(predicate);
    }

    @SuppressWarnings("unchecked")
    public B predicate(Predicate<V> predicate) {
      this.predicate = requireNonNull(predicate);
      return (B) this;
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

    public static class ForReturnedValue<V> extends Builder<ForReturnedValue<V>, V, Throwable> {
      private       Class<V>                                                       expectedClass;
      private final List<ForThrownException.TransformingPredicateForPcondUT<V, ?>> expectations = new LinkedList<>();

      public ForReturnedValue(V value, Predicate<V> predicate, Class<V> expectedClass) {
        this(value, predicate);
        this.expectedClass(expectedClass);
      }

      public ForReturnedValue(V value, Predicate<V> predicate) {
        this(value);
        predicate(predicate);
      }

      public ForReturnedValue(V value) {
        super(value);
      }

      public ForReturnedValue<V> expectedClass(Class<V> expectedClass) {
        this.expectedClass = requireNonNull(expectedClass);
        return this;
      }

      public ForReturnedValue<V> addExpectationPredicate(Predicate<V> predicate) {
        return this.addExpectationPredicate(makePrintableFunction("identity", Function.identity()), predicate);
      }

      public <W> ForReturnedValue<V> addExpectationPredicate(Function<V, W> function, Predicate<W> predicate) {
        this.expectations.add(new ForThrownException.TransformingPredicateForPcondUT<>(function, predicate));
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
          public List<ForThrownException.TransformingPredicateForPcondUT<V, ?>> checks() {
            return expectations;
          }
        };
        return super.build();
      }
    }

    public static class ForThrownException<V, T extends Throwable> extends Builder<ForThrownException<V, T>, V, T> {
      static class TransformingPredicateForPcondUT<T, R> {
        final Function<T, R> transform;
        final Predicate<R>   check;

        TransformingPredicateForPcondUT(Function<T, R> transform, Predicate<R> check) {
          this.transform = requireNonNull(transform);
          this.check = requireNonNull(check);
        }

        static <T> TransformingPredicateForPcondUT<T, T> createFromSimplePredicate(Predicate<T> predicate) {
          return new TransformingPredicateForPcondUT<>(makePrintableFunction("identity", Function.identity()), predicate);
        }
      }

      private final List<TransformingPredicateForPcondUT<T, ?>> expectations = new LinkedList<>();
      private       Class<T>                                    expectedExceptionClass;

      public ForThrownException(V value) {
        super(value);
      }

      public ForThrownException(V value, Predicate<V> predicate) {
        this(value);
        this.predicate(predicate);
      }

      public ForThrownException(V value, Predicate<V> predicate, Class<T> expectedExceptionClass) {
        this(value, predicate);
        this.expectedExceptionClass(expectedExceptionClass);
      }

      public ForThrownException<V, T> expectedExceptionClass(Class<T> expectedExceptionClass) {
        this.expectedExceptionClass = requireNonNull(expectedExceptionClass);
        return this;
      }

      public ForThrownException<V, T> addExpectationPredicate(Predicate<T> predicate) {
        this.expectations.add(TransformingPredicateForPcondUT.createFromSimplePredicate(predicate));
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
          public List<TransformingPredicateForPcondUT<T, ?>> checks() {
            return expectations;
          }
        };
        return super.build();
      }
    }
  }
}
