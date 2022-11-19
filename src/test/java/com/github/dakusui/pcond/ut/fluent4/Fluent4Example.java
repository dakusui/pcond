package com.github.dakusui.pcond.ut.fluent4;

import com.github.dakusui.pcond.core.fluent4.sandbox.StringTransformer;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.makeTrivial;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;

@RunWith(Enclosed.class)
public class Fluent4Example {
  public static class Done {
    @Test(expected = ComparisonFailure.class)
    public void test() {
      assertThat("INPUT_VALUE", stringTransformer()
          .parseBoolean()
          .then()
          .isTrue()
          .toPredicate());
    }

    @Test(expected = ComparisonFailure.class)
    public void test_b() {
      assertThat("INPUT_VALUE", stringTransformer()
          .toLowerCase()
          .parseBoolean()
          .then()
          .isTrue()
          .toPredicate());
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen() {
      assertThat("INPUT_VALUE", stringTransformer()
          .parseBoolean()
          .then()
          .isTrue()
          .isTrue()
          .toPredicate());
    }


    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_2() {
      assertThat("INPUT_VALUE", stringTransformer()
          .parseBoolean()
          .then()
          .addCheckPhrase(v -> v.checkWithPredicate(Predicates.isNotNull()).toPredicate())
          .addCheckPhrase(v -> v.checkWithPredicate(Predicates.isTrue()).toPredicate())
          .toPredicate());
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_3() {
      assertThat("INPUT_VALUE", stringTransformer()
          .parseBoolean()
          .then()
          .check(v -> v.isTrue().toPredicate())
          .check(v -> v.isTrue().toPredicate())
          .toPredicate());
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_4() {
      assertThat("INPUT_VALUE", stringTransformer()
          .toLowerCase()
          .then()
          .check(v -> v.contains("XYZ1").toPredicate())
          .check(v -> v.contains("ABC1").toPredicate())
          .toPredicate());
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_5() {
      assertThat("INPUT_VALUE", stringTransformer()
          .toLowerCase()
          .parseBoolean()
          .then()
          .check(v -> v.isTrue().toPredicate())
          .check(v -> v.isTrue().toPredicate())
          .toPredicate());
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_7() {
      assertThat("INPUT_VALUE", stringTransformer()
          .transformAndCheck(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue()
                  .toPredicate())
          .transformAndCheck(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue()
                  .toPredicate()).toPredicate());
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_6() {
      assertThat("INPUT_VALUE", stringTransformer()
          .transformAndCheck(
              tx -> {
                Predicate<String> stringPredicate = tx.toLowerCase()
                    .parseBoolean()
                    .then()
                    .isTrue()
                    .toPredicate();
                System.out.println(stringPredicate);
                return stringPredicate;
              }).toPredicate());
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_6a() {
      assertThat("INPUT_VALUE", stringTransformer()
          .transformAndCheck(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue()
                  .toPredicate())
          .check(Predicates.transform(Functions.length()).check(Predicates.isEqualTo(10)))
          .toPredicate());
    }
  }

  @Ignore
  public static class OnGoing {
  }

  private static StringTransformer.Impl<String> stringTransformer() {
    return new StringTransformer.Impl<>(makeTrivial(Functions.identity()));
  }
}