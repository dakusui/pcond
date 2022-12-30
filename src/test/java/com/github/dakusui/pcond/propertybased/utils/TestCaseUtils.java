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
import static java.lang.String.format;
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
    if (testCase.expectationForThrownException().isPresent()) {
      TestCase.Expectation<E> exceptionExpectation = testCase.expectationForThrownException().get();
      if (exceptionExpectation.expectedClass().isAssignableFrom(t.getClass())) {
        class CheckResult {
          final TestCheck<E, ?> testDef;
          final Object          transformOutput;
          final boolean                               passed;


          CheckResult(TestCheck<E, ?> testDef, Object transformOutput, boolean passed) {
            this.testDef = testDef;
            this.transformOutput = transformOutput;
            this.passed = passed;
          }
        }
        List<CheckResult> testresuls = new LinkedList<>();
        for (TestCheck<E, ?> each : exceptionExpectation.checks()) {
          Object v;
          boolean passed;
          passed = ((Predicate<Object>) each.check).test(v = each.transform.apply((E) t));
          testresuls.add(new CheckResult(each, v, passed));
        }
        if (testresuls.stream().anyMatch(r -> !r.passed)) {
          throw new AssertionError(format("Thrown exception: <" + formatObject(t) + "> did not satisfy some of following conditions:%n" +
              testresuls.stream()
                  .map((CheckResult each) ->
                      format("%-2s %s(%s(%s)->(%s))", each.passed ? "" : "NG", each.testDef.check, each.testDef.transform, formatObject(t, 16), formatObject(each.transformOutput)))
                  .collect(joining("%n- ", "----%n- ", "%n----"))) + format("%n%nTHROWN EXCEPTION DETAIL:%n") + formatException(t));
        }
      } else
        throw new AssertionError("Expected exception is '" + exceptionExpectation.expectedClass() + "' but thrown exception was: " + t);
    } else {
      throw t;
    }
  }

  private static Object formatException(Throwable t) {
    if (!(t instanceof ComparisonFailure))
      return t;
    StringBuilder b = new StringBuilder().append(format("%n"));
    b.append("MESSAGE:").append(format("%n"));
    b.append("- ").append(t.getMessage().replaceAll("\\n.+", ""));
    b.append("EXPECTATION:").append(format("%n"));
    for (String s : ((ComparisonFailure) t).getExpected().split("\n")) {
      b.append("  ").append(s).append(format("%n"));
    }
    b.append(format("%n"));
    b.append("ACTUAL:").append(format("%n"));
    for (String s : ((ComparisonFailure) t).getActual().split("\n")) {
      b.append("  ").append(s).append(format("%n"));
    }
    b.append(format("%n"));
    b.append("STACKTRACE:").append(format("%n"));
    for (StackTraceElement s : t.getStackTrace()) {
      b.append("  ").append(s).append(format("%n"));
    }
    return b.toString();
  }

  @SuppressWarnings("unchecked")
  private static <T, E extends Throwable> void examineReturnedValue(TestCase<T, E> testCase, T value) {
    if (testCase.expectationForThrownException().isPresent())
      throw new AssertionError("An exception that satisfies: <" + testCase.expectationForThrownException().get().expectedClass() + "> was expected to be thrown, but not");
    else if (testCase.expectationForReturnedValue().isPresent()) {
      List<TestCheck<T, ?>> errors = new LinkedList<>();
      for (TestCheck<T, ?> each : testCase.expectationForReturnedValue().get().checks()) {
        if (!((Predicate<Object>) each.check).test(each.transform.apply(value)))
          errors.add(each);
      }
      if (!errors.isEmpty())
        throw new AssertionError("Returned value: <" + value + "> did not satisfy following conditions:" + format("%n") +
            errors.stream()
                .map(each -> format("%s", each))
                .collect(joining("%n", "- ", "")));
    } else
      assert false;
  }
}
