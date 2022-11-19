package com.github.dakusui.pcond.ut.fluent4;

import com.github.dakusui.pcond.core.fluent4.sandbox.StringTransformer;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import org.junit.Test;

import static com.github.dakusui.pcond.internals.InternalUtils.makeTrivial;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;

public class Fluent4Example {
  @Test
  public void test() {
    assertThat("INPUT_VALUE", stringTransformer()
        .parseBoolean()
        .then()
        .isTrue()
        .toPredicate());
  }

  @Test
  public void test_allOf_inWhen() {
    assertThat("INPUT_VALUE", stringTransformer()
        .parseBoolean()
        .then()
        .isTrue()
        .isTrue()
        .toPredicate());
  }

  @Test
  public void test_allOf_inWhen_2() {
    assertThat("INPUT_VALUE", stringTransformer()
        .parseBoolean()
        .then()
        .addCheckPhrase(v -> v.check(Predicates.isNotNull()).toPredicate())
        .addCheckPhrase(v -> v.check(Predicates.isTrue()).toPredicate())
        .toPredicate());
  }

  private static StringTransformer.Impl<String> stringTransformer() {
    return new StringTransformer.Impl<>(makeTrivial(Functions.identity()));
  }
}
