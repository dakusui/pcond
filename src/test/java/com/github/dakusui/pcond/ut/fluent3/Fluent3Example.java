package com.github.dakusui.pcond.ut.fluent3;

import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Objects;

import static com.github.dakusui.thincrest.TestFluents.assertAll;
import static com.github.dakusui.thincrest.TestFluents.stringStatement;

@RunWith(Enclosed.class)
public class Fluent3Example {

  public static class Done {
    @Test(expected = ComparisonFailure.class)
    public void secondExample() {
      assertAll(
          stringStatement("Hello")
              .then()
              .isNull().toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void thirdExample() {
      assertAll(
          stringStatement("Hello")
              .allOf()
              .then()
              .isNotNull()
              .isNull()
              .toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void thirdExample_a() {
      assertAll(
          stringStatement("Hello")
              .allOf()
              .then()
              .isNotNull()
              .isNull());
    }

    @Test(expected = ComparisonFailure.class)
    public void forthExample() {
      assertAll(
          stringStatement("Hello").length().then().greaterThan(10).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void fifth() {
      assertAll(
          stringStatement("Hello5").length().then().greaterThan(10).lessThan(1).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test9() {
      assertAll(
          stringStatement("Hello5")
              .then()
              .appendChild(v -> v.isNull().toPredicate())
              .toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test7() {
      assertAll(
          stringStatement("Hello5")
              .appendChild(tx -> tx.then().isNotNull().toPredicate())
              .appendChild(tx -> tx.then().isNull().toPredicate()).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test6b() {
      assertAll(
          stringStatement("Hello5")
              .appendChild(tx -> tx.then().isNull().toPredicate()).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test6c() {
      assertAll(
          stringStatement("Hello5")
              .anyOf()
              .appendChild(tx -> tx.then().isNull().toPredicate()).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test6d() {
      assertAll(
          stringStatement("Hello5")
              .appendChild(tx -> tx.appendPredicateAsChild(Objects::isNull).toPredicate())
              .appendChild(tx -> tx.appendPredicateAsChild(v -> !Objects.isNull(v)).toPredicate()).toStatement()
      );
    }

    /**
     * Works without calling `toStatement()` method.
     */
    @Test(expected = ComparisonFailure.class)
    public void test7_a() {
      assertAll(
          stringStatement("Hello5")
              .appendChild(tx -> tx.then().isNotNull().toPredicate())
              .appendChild(tx -> tx.then().isNull().toPredicate()));
    }
  }

  @Ignore
  public static class OnGoing {
    /*
    @Test(expected = StackOverflowError.class)
    public void test8() {
      assertAll(
          statementForString("Hello5")
              .then()
              .appendChild(v -> v.isNull().$())
              .appendChild(v -> v.isNotNull().$())
              .$());
    }
    @Test(expected = StackOverflowError.class)
    public void firstExample() {
      assertAll(
          statementForString("Hello").allOf()
              .appendChild(tx -> tx.length()
                  .then()
                  .allOf()
                  .greaterThan(10)
                  .lessThan(100)
                  .root().$())
              .$());
    }

    @Test//(expected = StackOverflowError.class)
    public void test6() {
      assertAll(
          statementForString("Hello5")
              .appendChild(tx -> tx.length().then().greaterThan(10).root().$()).$());
    }
   */
  }
}
