package com.github.dakusui.pcond.ut.fluent3;

import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.core.fluent3.typesupports.StringTransformer;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Objects;

import static com.github.dakusui.thincrest.TestFluents.assertAll;

@RunWith(Enclosed.class)
public class Fluent3Example {
  static <R extends Matcher<R, R, String, String>> StringTransformer<String, R> statementForString(String value) {
    return StringTransformer.create(value);
  }

  public static class Done {
    @Test(expected = ComparisonFailure.class)
    public void secondExample() {
      assertAll(
          statementForString("Hello")
              .then()
              .isNull().toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void thirdExample() {
      assertAll(
          statementForString("Hello")
              .allOf()
              .then()
              .isNotNull()
              .isNull()
              .toStatement());
    }


    @Test(expected = ComparisonFailure.class)
    public void forthExample() {
      assertAll(
          statementForString("Hello").length().then().greaterThan(10).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void fifth() {
      assertAll(
          statementForString("Hello5").length().then().greaterThan(10).lessThan(1).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test9() {
      assertAll(
          statementForString("Hello5")
              .then()
              .appendChild(v -> v.isNull().builtPredicate())
              .toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test7() {
      assertAll(
          statementForString("Hello5")
              .appendChild(tx -> tx.then().isNotNull().builtPredicate())
              .appendChild(tx -> tx.then().isNull().builtPredicate()).toStatement());

      assertAll(
          statementForString("Hello5")
              .appendChild(tx -> tx.then().isNotNull().toStatement())
              .appendChild(tx -> tx.then().isNull().toStatement()).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test8() {
      assertAll(
          statementForString("Hello5")
              .then()
              .appendChild(v -> v.isNull().builtPredicate())
              .appendChild(v -> v.isNotNull().builtPredicate())
              .toStatement());
    }
    @Test(expected = ComparisonFailure.class)
    public void test6b() {
      assertAll(
          statementForString("Hello5")
              .appendChild(tx -> tx.then().isNull().builtPredicate()).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test6c() {
      assertAll(
          statementForString("Hello5")
              .anyOf()
              .appendChild(tx -> tx.then().isNull().builtPredicate()).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test6d() {
      assertAll(
          statementForString("Hello5")
              .appendChild(tx -> tx.appendPredicateAsChild(Objects::isNull).builtPredicate())
              .appendChild(tx -> tx.appendPredicateAsChild(v -> !Objects.isNull(v)).builtPredicate()).toStatement()
      );
    }
  }

  public static class OnGoing {

    @Test(expected = StackOverflowError.class)
    public void firstExample() {
      assertAll(
          statementForString("Hello").allOf()
              .appendChild(tx -> tx.length()
                  .then()
                  .allOf()
                  .greaterThan(10)
                  .lessThan(100)
                  .root().toStatement())
              .toStatement());
    }

    @Test(expected = StackOverflowError.class)
    public void test6() {
      assertAll(
          statementForString("Hello5")
              .appendChild(tx -> tx.length().then().greaterThan(10).root().toStatement()).toStatement());
    }
  }
}
