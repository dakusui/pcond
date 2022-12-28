package com.github.dakusui.pcond.propertybased.tests;

import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.propertybased.utils.*;
import com.github.dakusui.shared.utils.TestUtils;
import org.junit.ComparisonFailure;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.function.Predicate;

import static com.github.dakusui.pcond.forms.Predicates.allOf;
import static com.github.dakusui.pcond.propertybased.utils.ReportCheckUtils.equalsPredicate;
import static com.github.dakusui.pcond.propertybased.utils.TransformingPredicateForPcondUT.numberOfSummaryRecordsForActualIsEqualTo;
import static com.github.dakusui.pcond.propertybased.utils.TransformingPredicateForPcondUT.numberOfSummaryRecordsForActualAndExpectedAreEqual;
import static com.github.dakusui.shared.utils.TestUtils.toLowerCase;
import static com.github.dakusui.shared.utils.TestUtils.toUpperCase;

@RunWith(Parameterized.class)
public class TransformAndCheckPredicateTest extends PropertyBasedTestBase {

  public TransformAndCheckPredicateTest(String testName, TestCase<?, ?> testCase) {
    super(testName, testCase);
  }

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(TransformAndCheckPredicateTest.class);
  }

  @TestCaseParameter
  public static TestCase<String, ComparisonFailure> givenTwoChainedTransformingPredicates_whenNonExpectedValue_thenComparisonFailure() {
    return new TestCase.Builder.ForThrownException<String, ComparisonFailure>("HELLO")
        .predicate(
            allOf(
                Predicates.transform(toUpperCase().andThen(Functions.length())).check(Predicates.isEqualTo(6)),
                Predicates.transform(toLowerCase().andThen(Functions.length())).check(Predicates.isEqualTo(6))))
        .expectedExceptionClass(ComparisonFailure.class)
        .addExpectationPredicate(numberOfSummaryRecordsForActualAndExpectedAreEqual())
        .addExpectationPredicate(numberOfSummaryRecordsForActualIsEqualTo(9))
        .addExpectationPredicate(TestUtils.numberOfSummariesWithDetailsForExpectationAndActualAreEqual())
        .addExpectationPredicate(TestUtils.numberOfSummariesWithDetailsInExpectationIsEqualTo(2))
        .build();
  }

  @TestCaseParameter
  public static TestCase<String, Throwable> givenDoubleChainedTransformingPredicate_whenNonExpectedValue_thenComparisonFailure() {
    return new TestCase.Builder.ForReturnedValue<>(
        "HELLO",
        Predicates.transform(toUpperCase().andThen(toLowerCase()).andThen(Functions.length())).check(Predicates.isEqualTo(6)), String.class)
        .addExpectationPredicate(equalsPredicate("hello"))
        .build();
  }

  @SuppressWarnings("unchecked")
  @TestCaseParameter
  public static TestCase<String, Throwable> givenTransformingPredicate_whenExpectedValue_thenValueReturned() {
    return new TestCase.Builder.ForReturnedValue<>("hello", (Predicate<String>) Predicates.transform(Functions.length()).check(Predicates.isEqualTo(5)), String.class)
        .addExpectationPredicate(equalsPredicate("hello"))
        .build();
  }

  @SuppressWarnings("unchecked")
  @TestCaseParameter
  public static TestCase<String, ComparisonFailure> givenTransformingPredicate_whenNonExpectedValue_thenComparisonFailure() {
    return new TestCase.Builder.ForThrownException<String, ComparisonFailure>("hello")
        .predicate((Predicate<String>) Predicates.transform(Functions.length()).check(Predicates.isEqualTo(6)))
        .expectedExceptionClass(ComparisonFailure.class)
        .addExpectationPredicate(numberOfSummaryRecordsForActualAndExpectedAreEqual())
        .addExpectationPredicate(numberOfSummaryRecordsForActualIsEqualTo(9))
        .addExpectationPredicate(TestUtils.numberOfSummariesWithDetailsForExpectationAndActualAreEqual())
        .addExpectationPredicate(TestUtils.numberOfSummariesWithDetailsInExpectationIsEqualTo(2))
        .build();
  }

  @TestCaseParameter
  public static TestCase<String, ComparisonFailure> givenChainedTransformingPredicate_whenNonExpectedValue_thenComparisonFailure() {
    return new TestCase.Builder.ForThrownException<String, ComparisonFailure>("HELLO")
        .predicate(Predicates.transform(toLowerCase().andThen(Functions.length())).check(Predicates.isEqualTo(6)))
        .expectedExceptionClass(ComparisonFailure.class)
        .addExpectationPredicate(numberOfSummaryRecordsForActualAndExpectedAreEqual())
        .addExpectationPredicate(numberOfSummaryRecordsForActualIsEqualTo(9))
        .addExpectationPredicate(TestUtils.numberOfSummariesWithDetailsForExpectationAndActualAreEqual())
        .addExpectationPredicate(TestUtils.numberOfSummariesWithDetailsInExpectationIsEqualTo(2))
        .build();
  }
}
