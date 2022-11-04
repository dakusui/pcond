package com.github.dakusui.pcond.examples;

import com.github.dakusui.pcond.utils.TestClassExpectation;
import com.github.dakusui.pcond.utils.TestClassExpectation.EnsureJUnitResult;
import com.github.dakusui.pcond.utils.TestClassExpectation.ResultPredicateFactory.*;
import com.github.dakusui.pcond.utils.TestMethodExpectation;
import org.junit.Test;

import static com.github.dakusui.thincrest.TestAssertions.assertThat;
import static com.github.dakusui.thincrest.TestAssertions.assumeThat;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.utils.TestMethodExpectation.Result.*;

@TestClassExpectation({
    @EnsureJUnitResult(type = WasNotSuccessful.class, args = {}),
    @EnsureJUnitResult(type = RunCountIsEqualTo.class, args = "3"),
    @EnsureJUnitResult(type = IgnoreCountIsEqualTo.class, args = "0"),
    @EnsureJUnitResult(type = AssumptionFailureCountIsEqualTo.class, args = "1"),
    @EnsureJUnitResult(type = SizeOfFailuresIsEqualTo.class, args = "1")
})
public class UTExample {
  @TestMethodExpectation(PASSING)
  @Test
  public void shouldPass_testFirstNameOf() {
    String firstName = NameUtils.firstNameOf("Risa Kitajima");
    assertThat(firstName, allOf(not(containsString(" ")), startsWith("R")));
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void shouldFail_testFirstNameOf() {
    String firstName = NameUtils.firstNameOf("Yoshihiko Naito");
    assertThat(firstName, allOf(not(containsString(" ")), startsWith("N")));
  }

  @TestMethodExpectation(ASSUMPTION_FAILURE)
  @Test
  public void shouldBeIgnored_testFirstNameOf() {
    String firstName = NameUtils.firstNameOf("Yoshihiko Naito");
    assumeThat(firstName, allOf(not(containsString(" ")), startsWith("N")));
  }
}
