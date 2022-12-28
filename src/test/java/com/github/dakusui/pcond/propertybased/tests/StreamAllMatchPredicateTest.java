package com.github.dakusui.pcond.propertybased.tests;

import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.propertybased.utils.*;
import org.junit.ComparisonFailure;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.stream.Stream;

import static com.github.dakusui.pcond.forms.Predicates.containsString;
import static com.github.dakusui.pcond.forms.Predicates.isNotNull;
import static com.github.dakusui.pcond.propertybased.utils.ReportCheckUtils.*;

@RunWith(Parameterized.class)
public class StreamAllMatchPredicateTest extends PropertyBasedTestBase {

  public StreamAllMatchPredicateTest(String testName, TestCase<?, ?> testCase) {
    super(testName, testCase);
  }

  @Parameterized.Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(StreamAllMatchPredicateTest.class);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @TestCaseParameter
  public static TestCase<Stream<String>, Throwable> givenStreamPredicate_whenExpectedValue_thenValueReturned() {
    Stream<String> v;
    return new TestCase.Builder.ForReturnedValue<>(
        v = Stream.of("hello", "world"),
        Predicates.allMatch(isNotNull()),
        (Class<Stream<String>>) (Class) Stream.class)
        .addExpectationPredicate(equalsPredicate(v))
        .build();
  }

  @TestCaseParameter
  public static TestCase<Stream<String>, ComparisonFailure> givenStreamPredicate_whenUnexpectedValue_thenComparisonFailure() {
    Stream<String> v;
    return new TestCase.Builder.ForThrownException<>(
        v = Stream.of("hello", "world", "HELLO", "WORLD"),
        Predicates.allMatch(containsString("o")),
        ComparisonFailure.class)
        .addExpectationPredicate(TransformingPredicateForPcondUT.numberOfSummaryRecordsForActualIsEqualTo(4))
        .addExpectationPredicate(TransformingPredicateForPcondUT.numberOfSummaryRecordsForActualAndExpectedAreEqual())
        .build();
  }

  @TestCaseParameter
  public static TestCase<Stream<String>, ComparisonFailure> givenStreamPredicate_whenUnexpectedNullValue_thenComparisonFailure() {
    Stream<String> v;
    return new TestCase.Builder.ForThrownException<>(
        v = Stream.of("hello", "world", null, "HELLO", "WORLD"),
        Predicates.allMatch(isNotNull()),
        ComparisonFailure.class)
        .addExpectationPredicate(TransformingPredicateForPcondUT.numberOfSummaryRecordsForActualIsEqualTo(4))
        .addExpectationPredicate(TransformingPredicateForPcondUT.numberOfSummaryRecordsForActualAndExpectedAreEqual())
        .build();
  }
}
