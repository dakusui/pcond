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

import static com.github.dakusui.pcond.forms.Functions.length;
import static com.github.dakusui.pcond.forms.Predicates.isEqualTo;
import static com.github.dakusui.pcond.forms.Predicates.transform;
import static com.github.dakusui.pcond.internals.InternalUtils.makeTrivial;
import static com.github.dakusui.thincrest.TestFluents.assertStatement;

@RunWith(Enclosed.class)
public class Fluent4Example {
  public static class Done {
    @Test(expected = ComparisonFailure.class)
    public void test() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .parseBoolean()
          .then()
          .isTrue());
    }

    @Test(expected = ComparisonFailure.class)
    public void test_b() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .toLowerCase()
          .parseBoolean()
          .then()
          .isTrue());
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .parseBoolean()
          .then()
          .isTrue()
          .isTrue());
    }


    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_2() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .parseBoolean()
          .then()
          .addCheckPhrase(v -> v.checkWithPredicate(Predicates.isNotNull()).toPredicate())
          .addCheckPhrase(v -> v.checkWithPredicate(Predicates.isTrue()).toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_3() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .parseBoolean()
          .then()
          .check(v -> v.isTrue().toPredicate())
          .check(v -> v.isTrue().toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_4() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .toLowerCase()
          .then()
          .check(v -> v.contains("XYZ1").toPredicate())
          .check(v -> v.contains("ABC1").toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_5() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .toLowerCase()
          .parseBoolean()
          .then()
          .check(v -> v.isTrue().toPredicate())
          .check(v -> v.isTrue().toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_7() {
      assertStatement(stringTransformer("INPUT_VALUE")
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
                  .toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_6() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .transformAndCheck(
              tx -> {
                Predicate<String> stringPredicate = tx.toLowerCase()
                    .parseBoolean()
                    .then()
                    .isTrue()
                    .toPredicate();
                System.out.println(stringPredicate);
                return stringPredicate;
              }));
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_6a() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .transformAndCheck(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue()
                  .toPredicate())
          .checkWithPredicate(transform(length()).check(isEqualTo(10))));
    }
  }

  @Ignore
  public static class OnGoing {
    @Test
    public void test_allOf_inWhen_6a() {
      assertStatement(stringTransformer("helloWorld1")
          .transformAndCheck(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue()
                  .toPredicate())
          .checkWithPredicate(transform(length()).check(isEqualTo(10))));
    }

    @Test
    public void test_allOf_inWhen_6b() {
      assertStatement(stringTransformer("helloWorld2")
          .transformAndCheck(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue())
          .checkWithPredicate(transform(length()).check(isEqualTo(10)))
          .then());
    }
  }

  private static StringTransformer.Impl<String> stringTransformer(String value) {
    return new StringTransformer.Impl<>(() -> value, makeTrivial(Functions.identity()));
  }
}