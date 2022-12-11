package com.github.dakusui.pcond.propertybased;

import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.shared.utils.ut.TestBase;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.github.dakusui.pcond.propertybased.TestCaseUtils.numberOfSummaryRecordsForActualAndExpectedAreEqual;
import static com.github.dakusui.pcond.propertybased.TestCaseUtils.numberOfSummaryRecordsForActualIsEqualTo;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;


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
    TestCaseUtils.exerciseTestCase(testCase);
  }

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(PropertyBasedTest.class);
  }


  @TestCaseParameter
  static TestCase<String, Throwable> givenSimplePredicate_whenExpectedValue_thenValueReturned() {
    return new TestCase.Builder.ForReturnedValue<>("HELLO", Predicates.isEqualTo("HELLO"), String.class)
        .addExpectationPredicate(TestCaseUtils.equalsPredicate("HELLO"))
        .build();
  }

  @TestCaseParameter
  static TestCase<String, ComparisonFailure> givenSimplePredicate_whenUnexpectedValue_thenComparisonFailureThrown() {
    return new TestCase.Builder.ForThrownException<>("Hello", Predicates.isEqualTo("HELLO"), ComparisonFailure.class)
        .addExpectationPredicate(numberOfSummaryRecordsForActualIsEqualTo(1))
        .addExpectationPredicate(numberOfSummaryRecordsForActualAndExpectedAreEqual())
        .build();
  }

  @TestCaseParameter
  static TestCase<String, Throwable> givenNegatedSimplePredicateReturningFalse_whenExpectedValue_thenValueReturned() {
    return new TestCase.Builder.ForReturnedValue<>("HELLO", Predicates.not(Predicates.isEqualTo("hello")), String.class)
        .addExpectationPredicate(TestCaseUtils.equalsPredicate("HELLO"))
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

  @TestCaseParameter
  static TestCase<String, Throwable> whenPredicatesAllReturningTrueUnderAllOf_thenPasses() {
    return new TestCase.Builder.ForReturnedValue<>(
        "Hello",
        Predicates.allOf(Predicates.alwaysTrue(), Predicates.alwaysTrue()),
        String.class)
        .build();
  }

  @TestCaseParameter
  static TestCase<String, ComparisonFailure> whenPredicatesFirstReturningFalseRestReturningTrueUnderAllOf_whenComparisonFailure() {
    return new TestCase.Builder.ForThrownException<>(
        "Hello",
        Predicates.allOf(Predicates.not(Predicates.alwaysTrue()), Predicates.alwaysTrue(), Predicates.alwaysTrue()),
        ComparisonFailure.class)
        .build();
  }

  @TestCaseParameter
  static TestCase<String, ComparisonFailure> whenOnePredicateReturningFalseUnderAllOf_whenComparisonFailure() {
    return new TestCase.Builder.ForThrownException<>(
        "Hello",
        Predicates.allOf(Predicates.not(Predicates.alwaysTrue())),
        ComparisonFailure.class)
        .build();
  }


  @TestCaseParameter
  static TestCase<String, Throwable> whenPredicatesAllReturningTrueUnderAnyOf_thenPasses() {
    return new TestCase.Builder.ForReturnedValue<>(
        "Hello",
        Predicates.anyOf(Predicates.alwaysTrue(), Predicates.alwaysTrue()),
        String.class)
        .build();
  }

  @TestCaseParameter
  static TestCase<String, Throwable> whenPredicatesFirstReturningFalseRestReturningTrueUnderAnyOf_thenPass() {
    return new TestCase.Builder.ForReturnedValue<>(
        "Hello",
        Predicates.anyOf(Predicates.not(Predicates.alwaysTrue()), Predicates.alwaysTrue(), Predicates.alwaysTrue()),
        String.class)
        .build();
  }

  @TestCaseParameter
  static TestCase<String, ComparisonFailure> whenOnePredicateReturningFalseUnderAnyOf_whenComparisonFailure() {
    return new TestCase.Builder.ForThrownException<>(
        "Hello",
        Predicates.anyOf(Predicates.not(Predicates.alwaysTrue())),
        ComparisonFailure.class)
        .build();
  }

}
