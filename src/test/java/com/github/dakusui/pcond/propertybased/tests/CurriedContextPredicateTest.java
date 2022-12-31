package com.github.dakusui.pcond.propertybased.tests;

import com.github.dakusui.pcond.forms.Experimentals;
import com.github.dakusui.pcond.propertybased.utils.PropertyBasedTestBase;
import com.github.dakusui.pcond.propertybased.utils.TestCase;
import com.github.dakusui.pcond.propertybased.utils.TestCaseParameter;
import com.github.dakusui.pcond.propertybased.utils.TestCaseUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static com.github.dakusui.pcond.forms.Experimentals.toCurriedContextStream;
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
            .andThen(toCurriedContextStream()))                            // (2)
            .check(anyMatch(Experimentals.toCurriedContextPredicate(isNotNull()))),
        Object.class)
        .addExpectationPredicate(equalsPredicate(v))
        .build();
  }
}
