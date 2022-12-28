package com.github.dakusui.pcond.propertybased.tests;

import com.github.dakusui.pcond.propertybased.utils.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static com.github.dakusui.pcond.forms.Experimentals.toVariableBundlePredicate;
import static com.github.dakusui.pcond.forms.Experimentals.toVariableBundleStream;
import static com.github.dakusui.pcond.forms.Functions.streamOf;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.propertybased.utils.ReportCheckUtils.equalsPredicate;

@RunWith(Parameterized.class)
public class VariableBundlePredicateTest extends PropertyBasedTestBase {

  public VariableBundlePredicateTest(String testName, TestCase<?, ?> testCase) {
    super(testName, testCase);
  }

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(VariableBundlePredicateTest.class);
  }

  @TestCaseParameter
  public static TestCase<Object, Throwable> givenVariableBundlePredicate_whenExpectedValue_thenValueReturned() {
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
