package com.github.dakusui.pcond.propertybased;

import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.shared.ReportParser;
import com.github.dakusui.shared.utils.ut.TestBase;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.wrapIfNecessary;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;


@RunWith(Parameterized.class)
public class PropertyBasedTest extends TestBase {
  @Retention(RUNTIME)
  @Target(METHOD)
  @interface TestCaseParameter {
  }

  private final TestCase<?, ?> testCase;

  public PropertyBasedTest(@SuppressWarnings("unused") String testName, TestCase<?, ?> testCase) {
    this.testCase = requireNonNull(testCase);
  }

  @Test
  public void exerciseTestCase() throws Throwable {
    exerciseTestCase(testCase);
  }


  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return parameters(PropertyBasedTest.class);
  }


  private static List<Object[]> parameters(@SuppressWarnings("SameParameterValue") Class<?> testClass) {
    return Arrays.stream(requireNonNull(testClass).getDeclaredMethods())
        .filter(m -> m.isAnnotationPresent(TestCaseParameter.class))
        .filter(m -> isStatic(m.getModifiers()))
        .sorted(comparing(Method::getName))
        .map(m -> new Object[] { m.getName(), invokeMethod(m) })
        .collect(toList());
  }

  private static Object invokeMethod(Method m) {
    try {
      return m.invoke(null);
    } catch (Exception e) {
      throw wrapIfNecessary(e);
    }
  }

  @TestCaseParameter
  static TestCase<String, Throwable> givenSimplePredicate_whenExpectedValue_thenValueReturned() {
    return new TestCase.Builder.ForReturnedValue<>(
        String.class,
        "HELLO",
        Predicates.isEqualTo("HELLO"))
        .addExpectationPredicate(equalsPredicate("HELLO"))
        .build();
  }

  @TestCaseParameter
  static TestCase<String, ComparisonFailure> givenSimplePredicate_whenUnexpectedValue_thenComparisonFailureThrown() {
    return new TestCase.Builder.ForThrownException<>(
        "Hello",
        Predicates.isEqualTo("HELLO"),
        ComparisonFailure.class)
        .addExpectationPredicate(numberOfSummaryRecordsForActualIsEqualTo(1))
        .addExpectationPredicate(numberOfSummaryRecordsForActualAndExpectedAreEqual())
        .build();
  }

  @TestCaseParameter
  static TestCase<String, Throwable> givenNegatedSimplePredicateReturningFalse_whenExpectedValue_thenValueReturned() {
    return new TestCase.Builder.ForReturnedValue<>(
        String.class,
        "HELLO",
        Predicates.not(Predicates.isEqualTo("hello")))
        .addExpectationPredicate(equalsPredicate("HELLO"))
        .build();
  }

  @TestCaseParameter
  static TestCase<String, ComparisonFailure> givenNegatedSimplePredicateReturningTrue_whenUnexpectedValue_thenComparisonFailureThrown() {
    return new TestCase.Builder.ForThrownException<>(
        "Hello",
        Predicates.not(Predicates.isEqualTo("Hello")),
        ComparisonFailure.class)
        .addExpectationPredicate(numberOfSummaryRecordsForActualIsEqualTo(1))
        .addExpectationPredicate(numberOfSummaryRecordsForActualAndExpectedAreEqual())
        .build();
  }

  private static Predicate<String> equalsPredicate(Object w) {
    return makePrintable("equals(" + w + ")", v -> Objects.equals(v, w));
  }

  private static Predicate<ComparisonFailure> numberOfSummaryRecordsForActualIsEqualTo(@SuppressWarnings("SameParameterValue") int numberOfExpectedSummaryRecordsForActual) {
    return makePrintable("# of records (actual) = 1", e -> Objects.equals(numberOfExpectedSummaryRecordsForActual, new ReportParser(e.getActual()).summary().records().size()));
  }

  private static Predicate<ComparisonFailure> numberOfSummaryRecordsForActualAndExpectedAreEqual() {
    return makePrintable("# of records (actual) = # of records (expected)", e -> Objects.equals(new ReportParser(e.getActual()).summary().records().size(), new ReportParser(e.getExpected()).summary().records().size()));
  }

  private static <T, E extends Throwable> void exerciseTestCase(TestCase<T, E> testCase) throws Throwable {
    try {
      T value;
      assertThat(value = testCase.targetValue(), testCase.targetPredicate());
      examineReturnedValue(testCase, value);
    } catch (Throwable t) {
      examineThrownException(testCase, t);
    }
  }

  private static <T, E extends Throwable> void examineThrownException(TestCase<T, E> testCase, Throwable t) throws Throwable {
    if (testCase.expectationForThrownException().isPresent()) {
      TestCase.Expectation<E> comparisonFailureExpectation = testCase.expectationForThrownException().get();
      if (comparisonFailureExpectation.expectedClass().isAssignableFrom(t.getClass())) {
        List<Predicate<E>> errors = new LinkedList<>();
        for (Predicate<E> each : comparisonFailureExpectation.checks()) {
          if (!each.test((E) t))
            errors.add(each);
        }
        if (!errors.isEmpty()) {
          throw new AssertionError(String.format("Thrown exception: <" + t + "> did not satisfy following conditions:%n" +
              errors.stream()
                  .map(each -> String.format("%s", each))
                  .collect(joining("%n", "- ", ""))));
        }
      }
    } else {
      throw t;
    }
  }

  private static <T, E extends Throwable> void examineReturnedValue(TestCase<T, E> testCase, T value) {
    if (testCase.expectationForThrownException().isPresent())
      throw new AssertionError("An exception that satisfies: <" + testCase.expectationForThrownException().get().expectedClass() + "> was expected to be thrown, but not");
    else if (testCase.expectationForReturnedValue().isPresent()) {
      List<Predicate<T>> errors = new LinkedList<>();
      for (Predicate<T> each : testCase.expectationForReturnedValue().get().checks()) {
        if (!each.test(value))
          errors.add(each);
      }
      if (!errors.isEmpty())
        throw new AssertionError("Returned value: <" + value + "> did not satisfy following conditions:%n" +
            errors.stream()
                .map(each -> String.format("%s", each))
                .collect(joining("%n", "- ", "")));
    } else
      assert false;
  }

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

        public ForReturnedValue(Class<V> expectedClass, V value, Predicate<V> predicate) {
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

  public static <T> Predicate<T> makePrintable(String s, Predicate<T> predicate) {
    return new Predicate<T>() {
      @Override
      public boolean test(T t) {
        return predicate.test(t);
      }

      @Override
      public String toString() {
        return s;
      }
    };
  }
}
