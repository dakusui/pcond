package com.github.dakusui.pcond.propertybased.utils;

import com.github.dakusui.pcond.core.DebuggingUtils;
import org.junit.ComparisonFailure;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static com.github.dakusui.pcond.internals.InternalUtils.wrapIfNecessary;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public enum TestCaseUtils {
  ;

  private static Object invokeStaticMethod(Method m) {
    try {
      return m.invoke(null);
    } catch (Exception e) {
      throw wrapIfNecessary(e);
    }
  }

  public static List<Object[]> parameters(@SuppressWarnings("SameParameterValue") Class<?> testClass) {
    return Arrays.stream(requireNonNull(testClass).getDeclaredMethods())
        .filter(m -> m.isAnnotationPresent(TestCaseParameter.class))
        .filter(m -> isStatic(m.getModifiers()))
        .sorted(comparing(Method::getName))
        .map(m -> new Object[] { m.getName(), invokeStaticMethod(m) })
        .collect(toList());
  }

  public static <T, E extends Throwable> void exerciseTestCase(TestCase<T, E> testCase) throws Throwable {
    try {
      T value;
      assertThat(value = testCase.targetValue(), testCase.targetPredicate());
      examineReturnedValue(testCase, value);
    } catch (Throwable t) {
      if (DebuggingUtils.passThroughComparisonFailure() && t instanceof ComparisonFailure) {
        throw t;
      }
      examineThrownException(testCase, t);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T, E extends Throwable, F> void examineThrownException(TestCase<T, E> testCase, Throwable t) throws Throwable {
    System.out.println(t.getMessage());
    if (t instanceof ComparisonFailure) {
      System.out.println(((ComparisonFailure)t).getExpected());
      System.out.println(((ComparisonFailure)t).getActual());
    }
    if (testCase.expectationForThrownException().isPresent()) {
      TestCase.Expectation<E> exceptionExpectation = testCase.expectationForThrownException().get();
      if (exceptionExpectation.expectedClass().isAssignableFrom(t.getClass())) {
        class ErrorInfo {
          final TransformingPredicateForPcondUT<E, ?> testDef;
          final Object                                transformOutput;

          ErrorInfo(TransformingPredicateForPcondUT<E, ?> testDef, Object transformOutput) {
            this.testDef = testDef;
            this.transformOutput = transformOutput;
          }
        }
        List<ErrorInfo> errors = new LinkedList<>();
        for (TransformingPredicateForPcondUT<E, ?> each : exceptionExpectation.checks()) {
          Object v;
          if (!((Predicate<Object>)each.check).test(v = each.transform.apply((E) t)))
            errors.add(new ErrorInfo(each, v));
        }
        if (!errors.isEmpty()) {
          throw new AssertionError(String.format("Thrown exception: <" + t + "> did not satisfy following conditions:%n" +
              errors.stream()
                  .map((ErrorInfo each) ->
                      String.format("(%s).%s->(%s).%s", formatObject(t), each.testDef.transform, formatObject(each.transformOutput), each.testDef.check))
                  .collect(joining("%n- ", "----%n- ", "%n----"))));
        }
      } else
        throw new AssertionError("Expected exception is '" + exceptionExpectation.expectedClass() +  "' but thrown exception was: " + t);
    } else {
      throw t;
    }
  }

  @SuppressWarnings("unchecked")
  private static <T, E extends Throwable> void examineReturnedValue(TestCase<T, E> testCase, T value) {
    if (testCase.expectationForThrownException().isPresent())
      throw new AssertionError("An exception that satisfies: <" + testCase.expectationForThrownException().get().expectedClass() + "> was expected to be thrown, but not");
    else if (testCase.expectationForReturnedValue().isPresent()) {
      List<TransformingPredicateForPcondUT<T, ?>> errors = new LinkedList<>();
      for (TransformingPredicateForPcondUT<T, ?> each : testCase.expectationForReturnedValue().get().checks()) {
        if (!((Predicate<Object>)each.check).test(each.transform.apply(value)))
          errors.add(each);
      }
      if (!errors.isEmpty())
        throw new AssertionError("Returned value: <" + value + "> did not satisfy following conditions:" + String.format("%n") +
            errors.stream()
                .map(each -> String.format("%s", each))
                .collect(joining("%n", "- ", "")));
    } else
      assert false;
  }
}
