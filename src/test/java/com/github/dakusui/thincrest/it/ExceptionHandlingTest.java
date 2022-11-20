package com.github.dakusui.thincrest.it;

import com.github.dakusui.thincrest.TestAssertions;
import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.github.dakusui.pcond.core.refl.MethodQuery.classMethod;
import static com.github.dakusui.pcond.forms.Functions.*;
import static com.github.dakusui.pcond.forms.Predicates.*;

public class ExceptionHandlingTest {
  @Test(expected = ComparisonFailure.class)
  public void evenWhenExceptionThrown_thenTestGoesToLast() {
    String var = "abc";
    TestAssertions.assertThat(var,
        allOf(
            transform(
                call(classMethod(Integer.class, "parseInt", parameter()))
                    .andThen(cast(Integer.class)))
                .check(lt(0)),
            isNotNull()));
  }
}
