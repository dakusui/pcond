package com.github.dakusui.pcond.examples;

import com.github.dakusui.pcond.utils.TestClassExpectation;
import com.github.dakusui.pcond.utils.TestClassExpectation.EnsureJUnitResult;
import com.github.dakusui.pcond.utils.TestClassExpectation.ResultPredicateFactory.RunCountIsEqualTo;
import com.github.dakusui.pcond.utils.TestClassExpectation.ResultPredicateFactory.SizeOfFailuresIsEqualTo;
import com.github.dakusui.pcond.utils.TestClassExpectation.ResultPredicateFactory.WasNotSuccessful;
import com.github.dakusui.pcond.utils.TestMethodExpectation;
import org.junit.Test;

import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.functions.Predicates.*;
import static com.github.dakusui.pcond.utils.TestMethodExpectation.Result.FAILURE;
import static com.github.dakusui.pcond.utils.TestMethodExpectation.Result.PASSING;

@TestClassExpectation({
    @EnsureJUnitResult(type = WasNotSuccessful.class, args = {}),
    @EnsureJUnitResult(type = RunCountIsEqualTo.class, args = "2"),
    @EnsureJUnitResult(type = SizeOfFailuresIsEqualTo.class, args = "1")
})
public class ExampleUT {
  @TestMethodExpectation(PASSING)
  @Test
  public void shouldPass_testFirstNameOf() {
    String firstName = NameUtil.firstNameOf("Risa Kitajima");
    assertThat(firstName, and(not(containsString(" ")), startsWith("R")));
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void shouldFail_testFirstNameOf() {
    String firstName = NameUtil.firstNameOf("Yoshihiko Naito");
    assertThat(firstName, and(not(containsString(" ")), startsWith("N")));
  }
}
