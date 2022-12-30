package com.github.dakusui.pcond.propertybased.tests;

import com.github.dakusui.pcond.propertybased.utils.*;
import org.junit.ComparisonFailure;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.stream.Stream;

import static com.github.dakusui.pcond.forms.Experimentals.nest;
import static com.github.dakusui.pcond.forms.Experimentals.toVariableBundlePredicate;
import static com.github.dakusui.pcond.forms.Functions.length;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.propertybased.utils.TestCheck.*;
import static java.util.Arrays.asList;

@RunWith(Parameterized.class)
public class StreamNoneMatchPredicateTest extends PropertyBasedTestBase {

  public StreamNoneMatchPredicateTest(String testName, TestCase<?, ?> testCase) {
    super(testName, testCase);
  }

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(StreamNoneMatchPredicateTest.class);
  }


  /*
    @Test//(expected = IllegalValueException.class)
    public void hello_b_e2() {
      try {
        TestAssertions.assertThat(
            //TestAssertions.assertThat(
            asList("Hi", "hello", "world", null),
            transform(stream().andThen(nest(asList("1", "2", "o"))))
                .check(noneMatch(
                    toVariableBundlePredicate(transform(Functions.length()).check(gt(3))))));
        //          |                         |         |                         |
        //          |                         |         |                         |
        //         (1)                       (2)       (3)                       (4)

   */
  @TestCaseParameter
  public static TestCase<Stream<?>, ComparisonFailure> givenStreamPredicate_whenUnexpectedValue_thenComparisonFailure2() {
    Stream<Object> v;
    return new TestCase.Builder.ForThrownException<Stream<?>, ComparisonFailure>(
        Stream.of("Hi", "hello", "world", null),
        transform(nest(asList("1", "2", "o")))
            .check(noneMatch(
                toVariableBundlePredicate(transform(length()).check(gt(3))))))
        //      |                         |         |                         |
        //      |                         |         |                         |
        //     (1)                       (2)       (3)                       (4),
        .expectedExceptionClass(ComparisonFailure.class)
        /* Test Case Specific Check*/
        .addCheck(numberOfActualSummariesIsEqualTo(
            1 /* nest */ +
                1 /* check:nonMatch */ +
                ((1 /*contextPredicate*/ + 1 /*length*/ + 1 /*check:gt(3)*/)/* for each element */
                    * 4/* fourth element cuts the stream */)))
        .addCheck(numberOfExpectSummariesWithDetailsIsEqualTo(1))
        .addCheck(expectDetailAtContainsToken(0, ">[3]"))
        .build();
  }
}
