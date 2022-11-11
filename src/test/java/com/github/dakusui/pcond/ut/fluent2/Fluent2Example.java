package com.github.dakusui.pcond.ut.fluent2;

import com.github.dakusui.pcond.core.fluent2.StringTransformer;
import org.junit.Test;

import static com.github.dakusui.thincrest.TestFluents.assertAll;

public class Fluent2Example {
  @Test
  public void firstExample() {
    assertAll(
        StringTransformer.create("Hello").allOf()
            .appendChild(tx -> tx.length()
                .then().allOf()
                .greaterThan(10)
                .lessThan(100))
    );
  }
}
