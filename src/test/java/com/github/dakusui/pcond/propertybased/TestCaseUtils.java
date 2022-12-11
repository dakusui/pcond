package com.github.dakusui.pcond.propertybased;

import com.github.dakusui.shared.ReportParser;
import org.junit.ComparisonFailure;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.wrapIfNecessary;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

enum TestCaseUtils {
  ;

  private static Object invokeMethod(Method m) {
    try {
      return m.invoke(null);
    } catch (Exception e) {
      throw wrapIfNecessary(e);
    }
  }

  static List<Object[]> parameters(@SuppressWarnings("SameParameterValue") Class<?> testClass) {
    return Arrays.stream(requireNonNull(testClass).getDeclaredMethods())
        .filter(m -> m.isAnnotationPresent(PropertyBasedTest.TestCaseParameter.class))
        .filter(m -> isStatic(m.getModifiers()))
        .sorted(comparing(Method::getName))
        .map(m -> new Object[] { m.getName(), invokeMethod(m) })
        .collect(toList());
  }

  static Predicate<String> equalsPredicate(@SuppressWarnings("SameParameterValue") Object w) {
    return makePrintable("equals(" + w + ")", v -> Objects.equals(v, w));
  }

  static Predicate<ComparisonFailure> numberOfSummaryRecordsForActualIsEqualTo(@SuppressWarnings("SameParameterValue") int numberOfExpectedSummaryRecordsForActual) {
    return makePrintable("# of records (actual) = " + numberOfExpectedSummaryRecordsForActual, e -> Objects.equals(numberOfExpectedSummaryRecordsForActual, new ReportParser(e.getActual()).summary().records().size()));
  }

  static Predicate<ComparisonFailure> numberOfSummaryRecordsForActualAndExpectedAreEqual() {
    return makePrintable("# of records (actual) = # of records (expected)", e -> Objects.equals(new ReportParser(e.getActual()).summary().records().size(), new ReportParser(e.getExpected()).summary().records().size()));
  }

  static <T, E extends Throwable> void exerciseTestCase(TestCase<T, E> testCase) throws Throwable {
    try {
      T value;
      assertThat(value = testCase.targetValue(), testCase.targetPredicate());
      examineReturnedValue(testCase, value);
    } catch (Throwable t) {
      examineThrownException(testCase, t);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T, E extends Throwable> void examineThrownException(TestCase<T, E> testCase, Throwable t) throws Throwable {
    if (testCase.expectationForThrownException().isPresent()) {
      TestCase.Expectation<E> exceptionExpectation = testCase.expectationForThrownException().get();
      if (exceptionExpectation.expectedClass().isAssignableFrom(t.getClass())) {
        List<Predicate<E>> errors = new LinkedList<>();
        for (Predicate<E> each : exceptionExpectation.checks()) {
          if (!each.test((E) t))
            errors.add(each);
        }
        if (!errors.isEmpty()) {
          throw new AssertionError(String.format("Thrown exception: <" + t + "> did not satisfy following conditions:%n" +
              errors.stream()
                  .map(each -> String.format("%s", each))
                  .collect(joining("%n", "- ", ""))));
        }
      } else
        throw new AssertionError("Expected exception is '" + exceptionExpectation.expectedClass() +  "' but thrown exception was: " + t);
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
