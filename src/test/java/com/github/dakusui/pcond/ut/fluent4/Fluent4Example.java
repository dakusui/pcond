package com.github.dakusui.pcond.ut.fluent4;

import com.github.dakusui.pcond.core.fluent4.sandbox.StringTransformer;
import com.github.dakusui.pcond.forms.Functions;
import org.junit.Test;

import static com.github.dakusui.pcond.fluent.Fluents.makeTrivial;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;

public class Fluent4Example {
  @Test
  public void test() {
    assertThat("INPUT_VALUE", new StringTransformer.Impl<>(makeTrivial(Functions.identity()))
        .parseBoolean()
        .then()
        .isTrue()
        .toPredicate());
  }
}
