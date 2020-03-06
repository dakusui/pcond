package com.github.dakusui.crest.core;

import org.opentest4j.AssertionFailedError;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Thrown when a test programs detects that a certain runtime requirement to exercise
 * a test is not satisfied.
 * <p>
 * For completely predictable tests or tests expected to be so, such as unit tests,
 * this shouldn't be thrown.
 * <p>
 * However in non-unit testing phases, where tests depend on external conditions
 * outside JVM, even if inputs are all valid, still preconditions for them can be
 * unsatisfied. This is an exception to be thrown in such cases.
 * <p>
 * For instance, suppose that an integration test code tries to build a test fixture
 * where a database table is created and data set is loaded into it and then exercises
 * a test case for business logic. And before the exercise, it also verifies such
 * a fixture is sound and worth starting the actual test. In this situation, throwing
 * {@code TestAbortedException} or {@code AssumptionViolatedException} is not a good
 * idea because it will be silently ignored by testing framework (such as JUnit) but
 * it still may suggest some bug in SUT since such preparation is often implemented
 * using functionalities of the SUT.
 */
public class ExecutionFailure extends AssertionFailedError {
  public ExecutionFailure(String message, String expected, String actual, List<Throwable> throwables) {
    super(
        message, expected, actual,
        requireNonNull(throwables).isEmpty()
            ? null
            : throwables.get(0)
    );
    throwables.stream().distinct().skip(1).forEach(this::addSuppressed);
  }
}
