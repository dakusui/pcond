package com.github.dakusui.ut.thincrest.examples;

import com.github.dakusui.pcond.CallTest;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.ut.thincrest.examples.sut.NameUtils;
import com.github.dakusui.shared.utils.TestClassExpectation;
import com.github.dakusui.shared.utils.TestClassExpectation.EnsureJUnitResult;
import com.github.dakusui.shared.utils.TestClassExpectation.ResultPredicateFactory.*;
import com.github.dakusui.shared.utils.TestMethodExpectation;
import org.junit.Test;

import java.util.function.Predicate;

import static com.github.dakusui.pcond.forms.Functions.call;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;
import static com.github.dakusui.thincrest.TestAssertions.assumeThat;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.shared.utils.TestMethodExpectation.Result.*;

@TestClassExpectation({
        @EnsureJUnitResult(type = WasNotSuccessful.class, args = {}),
        @EnsureJUnitResult(type = RunCountIsEqualTo.class, args = "4"),
        @EnsureJUnitResult(type = IgnoreCountIsEqualTo.class, args = "0"),
        @EnsureJUnitResult(type = AssumptionFailureCountIsEqualTo.class, args = "1"),
        @EnsureJUnitResult(type = SizeOfFailuresIsEqualTo.class, args = "1")
})
public class UTExample {
    @TestMethodExpectation(PASSING)
    @Test
    public void shouldPass_testFirstNameOf() {
        String firstName = NameUtils.firstNameOf("Risa Kitajima");
        assertThat(firstName, allOf(not(containsString(" ")), startsWith("Risa")));
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

    @Test
    public void exampleTestMethod() {
        assertThat(
                new CallTest.ExtendsBase(),
                Predicates.<CallTest.ExtendsBase, String>transform(call("method", "Hello"))
                        .check(allOf(
                                containsString("Hello"),
                                containsString("extendsBase"))));
    }
}
