package com.github.dakusui.pcond.propertybased.tests;

import com.github.dakusui.pcond.propertybased.utils.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static com.github.dakusui.pcond.forms.Experimentals.toVariableBundlePredicate;
import static com.github.dakusui.pcond.forms.Experimentals.toCurriedStream;
import static com.github.dakusui.pcond.forms.Functions.streamOf;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.propertybased.utils.TestCheck.equalsPredicate;

@RunWith(Parameterized.class)
public class CurriedContextPredicateTest extends PropertyBasedTestBase {

  public CurriedContextPredicateTest(String testName, TestCase<?, ?> testCase) {
    super(testName, testCase);
  }

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    return TestCaseUtils.parameters(CurriedContextPredicateTest.class);
  }

  @TestCaseParameter
  public static TestCase<Object, Throwable> givenVariableBundlePredicate_whenExpectedValue_thenValueReturned() {
    Object v;
    return new TestCase.Builder.ForReturnedValue<>(
        v = "hello",
        transform(streamOf()                                        // (1)
            .andThen(toCurriedStream()))                            // (2)
            .check(anyMatch(toVariableBundlePredicate(isNotNull()))),
        Object.class)
        .addExpectationPredicate(equalsPredicate(v))
        .build();
  }
}
