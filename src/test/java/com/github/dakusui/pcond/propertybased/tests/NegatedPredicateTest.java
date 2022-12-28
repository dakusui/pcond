package com.github.dakusui.pcond.propertybased.tests;

import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.propertybased.utils.*;
import org.junit.ComparisonFailure;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.github.dakusui.pcond.propertybased.utils.TransformingPredicateForPcondUT.numberOfExpectAndActualSummariesAreEqual;
import static com.github.dakusui.pcond.propertybased.utils.TransformingPredicateForPcondUT.numberOfExpectAndActualSummariesWithDetailsAreEqual;
import static com.github.dakusui.pcond.propertybased.utils.TransformingPredicateForPcondUT.numberOfExpectSummariesWithDetailsIsEqualTo;
import static com.github.dakusui.pcond.propertybased.utils.ReportCheckUtils.*;

@RunWith(Parameterized.class)
public class NegatedPredicateTest extends PropertyBasedTestBase {

  public NegatedPredicateTest(String testName, TestCase<?, ?> testCase) {
    super(testName, testCase);
  }

  @Parameterized.Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(NegatedPredicateTest.class);
  }

  @TestCaseParameter
  public static TestCase<String, Throwable> givenNegatedSimplePredicateReturningFalse_whenExpectedValue_thenValueReturned() {
    return new TestCase.Builder.ForReturnedValue<>("HELLO", Predicates.not(Predicates.isEqualTo("hello")), String.class)
        .addExpectationPredicate(equalsPredicate("HELLO"))
        .build();
  }

  @TestCaseParameter
  public static TestCase<String, ComparisonFailure> givenNegatedSimplePredicateReturningTrue_whenUnexpectedValue_thenComparisonFailureThrown() {
    return new TestCase.Builder.ForThrownException<>(
        "Hello",
        Predicates.not(Predicates.isEqualTo("Hello")),
        ComparisonFailure.class)
        .addExpectationPredicate(TransformingPredicateForPcondUT.numberOfActualSummariesIsEqualTo(1))
        .addExpectationPredicate(numberOfExpectSummariesWithDetailsIsEqualTo(1))
        .addExpectationPredicate(numberOfExpectAndActualSummariesAreEqual())
        .addExpectationPredicate(numberOfExpectAndActualSummariesWithDetailsAreEqual())
        .build();
  }
}
