package com.github.dakusui.pcond.propertybased;

import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.shared.ReportParser;
import com.github.dakusui.shared.utils.ut.TestBase;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.forms.Experimentals.toVariableBundlePredicate;
import static com.github.dakusui.pcond.forms.Experimentals.toVariableBundleStream;
import static com.github.dakusui.pcond.forms.Functions.streamOf;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.propertybased.ReportCheckUtils.*;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Objects.requireNonNull;


@RunWith(Enclosed.class)
public class PropertyBasedTest {
  @Retention(RUNTIME)
  @Target(METHOD)
  @interface TestCaseParameter {
  }

  private static abstract class Base extends TestBase {
    private final TestCase<?, ?> testCase;

    public Base(@SuppressWarnings("unused") String testName, TestCase<?, ?> testCase) {
      this.testCase = requireNonNull(testCase);
    }

    @SuppressWarnings("unused")
    @Test
    public void exerciseTestCase() throws Throwable {
      TestCaseUtils.exerciseTestCase(testCase);
    }
  }

  @RunWith(Parameterized.class)
  public static class SimplePredicate extends Base {

    public SimplePredicate(String testName, TestCase<?, ?> testCase) {
      super(testName, testCase);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> parameters() {
      return TestCaseUtils.parameters(SimplePredicate.class);
    }

    @TestCaseParameter
    static TestCase<String, Throwable> givenSimplePredicate_whenExpectedValue_thenValueReturned() {
      return new TestCase.Builder.ForReturnedValue<>("HELLO", Predicates.isEqualTo("HELLO"))
          .expectedClass(String.class)
          .addExpectationPredicate(equalsPredicate("HELLO"))
          .build();
    }

    @TestCaseParameter
    static TestCase<String, ComparisonFailure> givenSimplePredicate_whenUnexpectedValue_thenComparisonFailureThrown() {
      return new TestCase.Builder.ForThrownException<>("Hello", Predicates.isEqualTo("HELLO"), ComparisonFailure.class)
          .addExpectationPredicate(numberOfSummaryRecordsForActualIsEqualTo(1))
          .addExpectationPredicate(numberOfSummaryRecordsForActualAndExpectedAreEqual())
          .build();
    }
  }

  @RunWith(Parameterized.class)
  public static class NegatedPredicate extends Base {

    public NegatedPredicate(String testName, TestCase<?, ?> testCase) {
      super(testName, testCase);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> parameters() {
      return TestCaseUtils.parameters(NegatedPredicate.class);
    }

    @TestCaseParameter
    static TestCase<String, Throwable> givenNegatedSimplePredicateReturningFalse_whenExpectedValue_thenValueReturned() {
      return new TestCase.Builder.ForReturnedValue<>("HELLO", Predicates.not(Predicates.isEqualTo("hello")), String.class)
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
          .addExpectationPredicate(numberOfSummariesWithDetailsInExpectationIsEqualTo(1))
          .addExpectationPredicate(numberOfSummaryRecordsForActualAndExpectedAreEqual())
          .addExpectationPredicate(numberOfSummariesWithDetailsForExpectationAndActualAreEqual())
          .build();
    }
  }

  @RunWith(Parameterized.class)
  public static class AllOfPredicate extends Base {

    public AllOfPredicate(String testName, TestCase<?, ?> testCase) {
      super(testName, testCase);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> parameters() {
      return TestCaseUtils.parameters(AllOfPredicate.class);
    }

    @TestCaseParameter
    static TestCase<String, Throwable> whenPredicatesAllReturningTrueUnderAllOf_thenPasses() {
      return new TestCase.Builder.ForReturnedValue<>(
          "Hello",
          Predicates.allOf(alwaysTrue(), alwaysTrue()),
          String.class)
          .build();
    }

    @TestCaseParameter
    static TestCase<String, ComparisonFailure> whenPredicatesFirstReturningFalseRestReturningTrueUnderAllOf_whenComparisonFailure() {
      return new TestCase.Builder.ForThrownException<>(
          "Hello",
          Predicates.allOf(alwaysFalse(), alwaysTrue(), alwaysTrue()),
          ComparisonFailure.class)
          .addExpectationPredicate(expectationSummarySizeIsEqualTo(1 /*all*/ + 3 /*alwaysFalse, alwaysTrue, alwaysTrue*/))
          .build();
    }

    @TestCaseParameter
    static TestCase<String, ComparisonFailure> whenOnePredicateReturningFalseUnderAllOf_whenComparisonFailure() {
      return new TestCase.Builder.ForThrownException<>(
          "Hello",
          Predicates.allOf(alwaysFalse()),
          ComparisonFailure.class)
          .addExpectationPredicate(expectationSummarySizeIsEqualTo(1 /*all*/ + 1 /*alwaysFalse*/))
          .build();
    }
  }

  @RunWith(Parameterized.class)
  public static class AnyOfPredicate extends Base {

    public AnyOfPredicate(String testName, TestCase<?, ?> testCase) {
      super(testName, testCase);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> parameters() {
      return TestCaseUtils.parameters(AnyOfPredicate.class);
    }

    @TestCaseParameter
    static TestCase<String, Throwable> whenPredicatesFirstReturningFalseRestReturningTrueUnderAnyOf_thenPass() {
      return new TestCase.Builder.ForReturnedValue<>(
          "Hello",
          anyOf(alwaysFalse(), alwaysTrue(), alwaysTrue()),
          String.class)
          .build();
    }

    @TestCaseParameter
    static TestCase<String, ComparisonFailure> whenOnePredicateReturningFalseUnderAnyOf_whenComparisonFailure() {
      return new TestCase.Builder.ForThrownException<>(
          "Hello",
          anyOf(alwaysFalse()), ComparisonFailure.class)
          .addExpectationPredicate(expectationSummarySizeIsEqualTo(1 + /* anyOf */ +1 /*alwaysFalse*/))
          .addExpectationPredicate(numberOfSummariesWithDetailsInExpectationIsEqualTo(1))
          .build();
    }

    @TestCaseParameter
    static TestCase<String, Throwable> whenPredicatesAllReturningTrueUnderAnyOf_thenPasses() {
      return new TestCase.Builder.ForReturnedValue<>(
          "Hello",
          anyOf(alwaysTrue(), alwaysTrue()),
          String.class)
          .build();
    }
  }

  @RunWith(Parameterized.class)
  public static class TransformAndCheckPredicate extends Base {

    public TransformAndCheckPredicate(String testName, TestCase<?, ?> testCase) {
      super(testName, testCase);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> parameters() {
      return TestCaseUtils.parameters(TransformAndCheckPredicate.class);
    }

    @SuppressWarnings("unchecked")
    @TestCaseParameter
    static TestCase<String, Throwable> givenTransformingPredicate_whenExpectedValue_thenValueReturned() {
      return new TestCase.Builder.ForReturnedValue<>("hello", (Predicate<String>) Predicates.transform(Functions.length()).check(Predicates.isEqualTo(5)), String.class)
          .addExpectationPredicate(equalsPredicate("hello"))
          .build();
    }

    @SuppressWarnings("unchecked")
    @TestCaseParameter
    static TestCase<String, Throwable> givenTransformingPredicate_whenNonExpectedValue_thenComparisonFailure() {
      return new TestCase.Builder.ForReturnedValue<>(
          "hello",
          (Predicate<String>) Predicates.transform(Functions.length()).check(Predicates.isEqualTo(6)), String.class)
          .addExpectationPredicate(equalsPredicate("hello"))
          .build();
    }

    @TestCaseParameter
    static TestCase<String, Throwable> givenDoubleChainedTransformingPredicate_whenNonExpectedValue_thenComparisonFailure() {
      return new TestCase.Builder.ForReturnedValue<>(
          "HELLO",
          Predicates.transform(toUpperCase().andThen(toLowerCase()).andThen(Functions.length())).check(Predicates.isEqualTo(6)), String.class)
          .addExpectationPredicate(equalsPredicate("hello"))
          .build();
    }

    @TestCaseParameter
    static TestCase<String, ComparisonFailure> givenTwoChainedTransformingPredicates_whenNonExpectedValue_thenComparisonFailure() {
      return new TestCase.Builder.ForThrownException<String, ComparisonFailure>("HELLO")
          .predicate(
              allOf(
                  Predicates.transform(toUpperCase().andThen(Functions.length())).check(Predicates.isEqualTo(6)),
                  Predicates.transform(toLowerCase().andThen(Functions.length())).check(Predicates.isEqualTo(6))))
          .expectedExceptionClass(ComparisonFailure.class)
          .addExpectationPredicate(numberOfSummaryRecordsForActualAndExpectedAreEqual())
          .addExpectationPredicate(numberOfSummaryRecordsForActualIsEqualTo(9))
          .addExpectationPredicate(numberOfSummariesWithDetailsForExpectationAndActualAreEqual())
          .addExpectationPredicate(numberOfSummariesWithDetailsInExpectationIsEqualTo(2))
          .build();
    }

    @TestCaseParameter
    static TestCase<String, ComparisonFailure> givenChainedTransformingPredicate_whenNonExpectedValue_thenComparisonFailure() {
      return new TestCase.Builder.ForThrownException<String, ComparisonFailure>("HELLO")
          .predicate(Predicates.transform(toLowerCase().andThen(Functions.length())).check(Predicates.isEqualTo(6)))
          .expectedExceptionClass(ComparisonFailure.class)
          .addExpectationPredicate(numberOfSummaryRecordsForActualAndExpectedAreEqual())
          .addExpectationPredicate(numberOfSummaryRecordsForActualIsEqualTo(9))
          .addExpectationPredicate(numberOfSummariesWithDetailsForExpectationAndActualAreEqual())
          .addExpectationPredicate(numberOfSummariesWithDetailsInExpectationIsEqualTo(2))
          .build();
    }
  }

  @RunWith(Parameterized.class)
  public static class StreamAllMatchPredicate extends Base {

    public StreamAllMatchPredicate(String testName, TestCase<?, ?> testCase) {
      super(testName, testCase);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> parameters() {
      return TestCaseUtils.parameters(StreamAllMatchPredicate.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @TestCaseParameter
    static TestCase<Stream<String>, Throwable> givenStreamPredicate_whenExpectedValue_thenValueReturned() {
      Stream<String> v;
      return new TestCase.Builder.ForReturnedValue<>(
          v = Stream.of("hello", "world"),
          Predicates.allMatch(isNotNull()),
          (Class<Stream<String>>) (Class) Stream.class)
          .addExpectationPredicate(equalsPredicate(v))
          .build();
    }

    @TestCaseParameter
    static TestCase<Stream<String>, ComparisonFailure> givenStreamPredicate_whenUnexpectedValue_thenComparisonFailure() {
      Stream<String> v;
      return new TestCase.Builder.ForThrownException<>(
          v = Stream.of("hello", "world", "HELLO", "WORLD"),
          Predicates.allMatch(containsString("o")),
          ComparisonFailure.class)
          .addExpectationPredicate(numberOfSummaryRecordsForActualIsEqualTo(4))
          .addExpectationPredicate(numberOfSummaryRecordsForActualAndExpectedAreEqual())
          .build();
    }

    @TestCaseParameter
    static TestCase<Stream<String>, ComparisonFailure> givenStreamPredicate_whenUnexpectedNullValue_thenComparisonFailure() {
      Stream<String> v;
      return new TestCase.Builder.ForThrownException<>(
          v = Stream.of("hello", "world", null, "HELLO", "WORLD"),
          Predicates.allMatch(isNotNull()),
          ComparisonFailure.class)
          .addExpectationPredicate(numberOfSummaryRecordsForActualIsEqualTo(4))
          .addExpectationPredicate(numberOfSummaryRecordsForActualAndExpectedAreEqual())
          .build();
    }
  }

  @RunWith(Parameterized.class)
  public static class VariableBundlePredicate extends Base {

    public VariableBundlePredicate(String testName, TestCase<?, ?> testCase) {
      super(testName, testCase);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> parameters() {
      return TestCaseUtils.parameters(VariableBundlePredicate.class);
    }

    @TestCaseParameter
    static TestCase<Object, Throwable> givenVariableBundlePredicate_whenExpectedValue_thenValueReturned() {
      Object v;
      return new TestCase.Builder.ForReturnedValue<>(
          v = "hello",
          transform(streamOf()                                        // (1)
              .andThen(toVariableBundleStream()))                            // (2)
              .check(anyMatch(toVariableBundlePredicate(isNotNull()))),
          Object.class)
          .addExpectationPredicate(equalsPredicate(v))
          .build();
    }
  }

  private static Function<String, String> toLowerCase() {
    return Printables.function("toLowerCase", String::toLowerCase);
  }

  private static Function<String, String> toUpperCase() {
    return Printables.function("toUpperCase", String::toUpperCase);
  }

  private static Predicate<String> alwaysFalse() {
    return Printables.predicate("alwaysFalse", v -> false);
  }

  private static Predicate<ComparisonFailure> expectationSummarySizeIsEqualTo(final int expectedSize) {
    return Printables.predicate(
        "expectationSummarySize==" + expectedSize,
        comparisonFailure -> new ReportParser(comparisonFailure.getExpected()).summary().records().size() == expectedSize);
  }

  private static Predicate<ComparisonFailure> numberOfSummariesWithDetailsInExpectationIsEqualTo(int numberOfSummariesWithDetails) {
    return Printables.predicate("numberOfSummariesWithDetailInExpectation==" + numberOfSummariesWithDetails, comparisonFailure -> new ReportParser(comparisonFailure.getExpected()).summary().records().stream().filter(e -> e.detailIndex().isPresent()).count() == numberOfSummariesWithDetails);
  }

  private static Predicate<ComparisonFailure> numberOfSummariesWithDetailsForExpectationAndActualAreEqual() {
    return Printables.predicate("numberOfSummariesWithDetailsForExpectationAndActualAreEqual", comparisonFailure -> {
      List<ReportParser.Summary.Record> expectationSummariesWithDetails = summariesWithDetailsOf(comparisonFailure.getExpected());
      List<ReportParser.Summary.Record> actualValueSummariesWithDetails = summariesWithDetailsOf(comparisonFailure.getActual());
      return expectationSummariesWithDetails.size() == actualValueSummariesWithDetails.size();
    });
  }

  private static List<ReportParser.Summary.Record> summariesWithDetailsOf(String expectedOrActualStringInComparisonFailure) {
    return new ReportParser(expectedOrActualStringInComparisonFailure)
        .summary()
        .records()
        .stream()
        .filter(r -> r.detailIndex().isPresent())
        .collect(Collectors.toList());
  }
}
