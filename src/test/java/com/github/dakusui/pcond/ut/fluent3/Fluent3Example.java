package com.github.dakusui.pcond.ut.fluent3;

import com.github.dakusui.pcond.ut.fluent4.Fluent4Example;
import com.github.dakusui.shared.ReportParser;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.fluent.Fluents.stringValue;
import static com.github.dakusui.pcond.forms.Functions.identity;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.forms.Printables.predicate;
import static com.github.dakusui.pcond.internals.InternalUtils.makeSquashable;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;
import static com.github.dakusui.thincrest.TestFluents.assertAll;

@RunWith(Enclosed.class)
public class Fluent3Example {

  public static class Done {
    @Test(expected = ComparisonFailure.class)
    public void secondExample() {
      assertAll(
          stringValue("Hello")
              .then()
              .isNull());
    }

    @Test(expected = ComparisonFailure.class)
    public void thirdExample() {
      assertAll(
          stringValue("Hello")
              .allOf()
              .then()
              .isNotNull()
              .isNull());
    }

    @Test(expected = ComparisonFailure.class)
    public void thirdExample_a() {
      assertAll(
          stringValue("Hello")
              .allOf()
              .then()
              .isNotNull()
              .isNull());
    }

    @Test(expected = ComparisonFailure.class)
    public void forthExample() {
      assertAll(
          stringValue("Hello").length().then().greaterThan(10));
    }

    @Test(expected = ComparisonFailure.class)
    public void fifth() {
      assertAll(
          stringValue("Hello5").length().then().greaterThan(10).lessThan(1));
    }

    @Test(expected = ComparisonFailure.class)
    public void test9() {
      assertAll(
          stringValue("Hello5")
              .then()
              .check(v -> v.isNull().toPredicate())
      );
    }

    @Test(expected = ComparisonFailure.class)
    public void test7() {
      assertAll(
          stringValue("Hello5")
              .transform(tx -> tx.then().isNotNull().toPredicate())
              .transform(tx -> tx.then().isNull().toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test6b() {
      assertAll(
          stringValue("Hello5")
              .transform(tx -> tx.then().isNull().toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test6c() {
      assertAll(
          stringValue("Hello5")
              .anyOf()
              .transform(tx -> tx.then().isNull().toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test6d() {
      assertAll(
          stringValue("Hello5")
              .transform(tx -> tx.checkWithPredicate(Objects::isNull).toPredicate())
              .transform(tx -> tx.checkWithPredicate(v -> !Objects.isNull(v)).toPredicate())
      );
    }

    /**
     * Works without calling `toStatement()` method.
     */
    @Test(expected = ComparisonFailure.class)
    public void test7_a() {
      assertAll(
          stringValue("Hello5")
              .transform(tx -> tx.then().isNotNull().toPredicate())
              .transform(tx -> tx.then().isNull().toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test6() {
      assertAll(
          stringValue("Hello5")
              .transform(tx -> tx.length().then().greaterThan(10).done()));
    }

  }


  @Ignore
  public static class OnGoing {
    @Test(expected = ComparisonFailure.class)
    public void test8() {
      assertAll(
          stringValue("Hello5")
              .then()
              .check(v -> v.isNull().done())
              .check(v -> v.isNotNull().done()));
    }

    @Test(expected = ComparisonFailure.class)
    public void firstExample() {
      assertAll(
          stringValue("Hello").allOf()
              .transform(tx -> tx.length()
                  .then()
                  .allOf()
                  .greaterThan(10)
                  .lessThan(100)
                  .done()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test7_C() {
      assertAll(
          stringValue("Hello5")
              .transform(tx -> tx
                  .transform(ty -> ty.length()
                      .then()
                      .greaterThanOrEqualTo(800)
                      .lessThan(1000)
                      .done())
                  .transform(ty -> ty.then().isNotNull().done()).done())
              .transform(tx -> tx.then().isNull().done()));
    }

    @Test
    public void givenBookTitle_whenLength_thenNotNullAndAppropriateLength() {
      assertAll(
          stringValue("De Bello Gallico")
              .transform(ty -> ty.then().isNotNull().done())
              .transform(ty -> ty.length().then()
                  .greaterThanOrEqualTo(10)
                  .lessThan(40)
                  .done()));
    }

    @Test(expected = ComparisonFailure.class)
    public void givenBookTitleAndAbstract_whenCheckThem_thenTheyAreNotNullAndAppropriateLength() {
      String bookTitle = "De Bello Gallico";
      String bookAbstract = "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.";
      assertAll(
          stringValue(bookTitle)
              .transform(ty -> ty.then().isNotNull().done())
              .transform(ty -> ty.length().then()
                  .greaterThanOrEqualTo(10)
                  .lessThan(40)
                  .done()),
          stringValue(bookAbstract)
              .transform(ty -> ty.then().isNotNull().done())
              .transform(ty -> ty.length().then()
                  .greaterThanOrEqualTo(200)
                  .lessThan(400)
                  .done()));
    }

    @Test
    public void givenBook_whenCheckTitleAndAbstract_thenTheyAreNotNullAndAppropriateLength() {
      Fluent4Example.OnGoing.Book book = new Fluent4Example.OnGoing.Book("De Bello Gallico", "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.");
      assertAll(
          new Fluent4Example.OnGoing.BookTransformer(book)
              .transform(b -> b.title()
                  .transform(ty -> ty.then().isNotNull().toPredicate()).done())
              .transform(b -> b.abstractText()
                  .transform(ty -> ty.then().isNotNull().toPredicate()).done()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test6() {
      assertAll(
          stringValue("Hello5")
              .transform(tx -> tx.length().then().greaterThanOrEqualTo(10).lessThan(100).done()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test6b() {
      assertAll(stringValue("Hello5")
          .length()
          .then()
          .greaterThanOrEqualTo(10)
          .lessThan(100));
    }


    @Test(expected = ComparisonFailure.class)
    public void thirdExample_a() {
      assertAll(
          stringValue("Hello")
              .length()
              .then()
              .allOf()
              .isNotNull()
              .isNull());
    }

    @Test(expected = ComparisonFailure.class)
    public void test7() {
      assertAll(
          stringValue("Hello5")
              .transform(tx -> tx.then().isNotNull().done())
              .transform(tx -> tx.then().isNull().done()));
    }

    @Test//(expected = ComparisonFailure.class)
    public void givenBook_whenCheckTitleAndAbstract_thenTheyAreNotNullAndAppropriateLength_2() {
      Fluent4Example.OnGoing.Book book = new Fluent4Example.OnGoing.Book("De Bello Gallico", "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.");
      try {
        assertAll(
            stringValue("hello").length().then().greaterThan(1),
            new Fluent4Example.OnGoing.BookTransformer(book)
                .transform(b -> b.title()
                    .transform(ty -> ty.then().isNotNull().done())
                    .transform(ty -> ty.parseInt().then()
                        .greaterThanOrEqualTo(10)
                        .lessThan(40)
                        .done()).done())
                .transform(b -> b.abstractText()
                    .transform(ty -> ty.then().checkWithPredicate(isNull().negate()).done())
                    .transform(ty -> ty.length().then()
                        .greaterThanOrEqualTo(200)
                        .lessThan(400)
                        .done()).done()));
      } catch (ComparisonFailure e) {
        ReportParser reportParser = new ReportParser(e.getActual());
        System.out.println("summary=" + reportParser.summary());
        reportParser.details().forEach(
            each -> {
              System.out.println(each.subject());
              System.out.println(each.body());
            }
        );
        throw e;
      }
    }

    @Test//(expected = ComparisonFailure.class)
    public void givenBook_whenCheckTitleAndAbstract_thenTheyAreNotNullAndAppropriateLength_3() {
      Fluent4Example.OnGoing.Book book = new Fluent4Example.OnGoing.Book("De Bello Gallico", "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.");
      try {
        assertAll(
            new Fluent4Example.OnGoing.BookTransformer(book)
                .transform(b -> b.title()
                    .transform(ty -> ty.then().isNotNull().done())
                    .transform(ty -> ty.length().then()
                        .greaterThanOrEqualTo(10)
                        .checkWithPredicate(errorThrowingPredicate())
                        .done()).done())
                .transform(b -> b.abstractText()
                    .transform(ty -> ty.then().isNotNull().done())
                    .transform(ty -> ty.length().then()
                        .greaterThanOrEqualTo(200)
                        .lessThan(400)
                        .done()).done()));
      } catch (ComparisonFailure e) {
        ReportParser reportParser = new ReportParser(e.getActual());
        System.out.println("summary=" + reportParser.summary());
        reportParser.details().forEach(
            each -> {
              System.out.println(each.subject());
              System.out.println(each.body());
            }
        );
        throw e;
      }
    }

    private Predicate<? super Integer> errorThrowingPredicate() {
      return predicate("errorThrowingPredicate", p -> {
        throw new RuntimeException("Intentional runtime exception!!!");
      });
    }

    @Test(expected = ComparisonFailure.class)
    public void makeTrivialTest() {
      assertThat("hello", transform(makeSquashable(identity())).check(transform(makeSquashable(identity())).check(isEqualTo("HELLO"))));
    }
  }

  public static class Smoke {
    @Test(expected = ComparisonFailure.class)
    public void givenBook_whenCheckTitleAndAbstract_thenTheyAreNotNullAndAppropriateLength_2() throws Throwable {
      smoke();
    }

    @Ignore
    @Test
    public void smoke() {
      String title = "De Bello Gallico";
      String abstractText = "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.";
      Fluent4Example.OnGoing.Book book = new Fluent4Example.OnGoing.Book(title, abstractText);
      try {
        assertAll(
            stringValue("hello").length().then().greaterThan(1),
            new Fluent4Example.OnGoing.BookTransformer(book)
                .transform(b -> b.title()
                    .transform(ty -> ty.then().isNotNull().done())
                    .transform(ty -> ty.parseInt().then()
                        .greaterThanOrEqualTo(10)
                        .lessThan(40)
                        .done()).done())
                .transform(b -> b.abstractText()
                    .transform(ty -> ty.then().checkWithPredicate(isNull().negate()).done())
                    .transform(ty -> ty.length().then()
                        .greaterThanOrEqualTo(200)
                        .lessThan(400)
                        .done()).done()));
      } catch (ComparisonFailure e) {
        e.printStackTrace();
        ReportParser reportParserForActualValue = new ReportParser(e.getActual());
        ReportParser reportParserForExpectation = new ReportParser(e.getExpected());

        assertAllRunnables(
            () -> assertAllRunnables(
                () -> {
                  System.out.println("summary=" + reportParserForActualValue.summary());
                  reportParserForActualValue.details().forEach(
                      each -> {
                        System.out.println(each.subject());
                        System.out.println(each.body());
                      }
                  );
                },
                () -> MatcherAssert.assertThat(
                    countDetailIndicesInSummary(reportParserForExpectation),
                    CoreMatchers.equalTo(3L))
            ),
            () -> assertAllRunnables(
                () -> {
                  MatcherAssert.assertThat(detailSubjectFor(reportParserForActualValue, 0), CoreMatchers.containsString("[0]"));
                  MatcherAssert.assertThat(detailBodyFor(reportParserForActualValue, 0), CoreMatchers.allOf(
                      CoreMatchers.containsString("Input: 'De Bello Gallico'"),
                      CoreMatchers.containsString("Input Type: java.lang.String"),
                      CoreMatchers.containsString("Thrown Exception: 'java.lang.NumberFormatException'")
                  ));
                },
                () -> {
                  MatcherAssert.assertThat(
                      countDetailIndicesInSummary(reportParserForActualValue),
                      CoreMatchers.equalTo(3L));
                },
                () -> {
                  MatcherAssert.assertThat(detailSubjectFor(reportParserForActualValue, 1), CoreMatchers.containsString("[1]"));
                  MatcherAssert.assertThat(detailBodyFor(reportParserForActualValue, 1), CoreMatchers.containsString(abstractText));
                }),
            () -> assertAllRunnables(
                () -> {
                  MatcherAssert.assertThat(detailSubjectFor(reportParserForActualValue, 2), CoreMatchers.containsString("[2]"));
                  MatcherAssert.assertThat(Integer.parseInt(detailBodyFor(reportParserForActualValue, 2)), CoreMatchers.equalTo(abstractText.length()));
                },
                () -> MatcherAssert.assertThat(reportParserForActualValue.details().size(), CoreMatchers.allOf(
                    CoreMatchers.equalTo(reportParserForExpectation.details().size()),
                    CoreMatchers.equalTo(3)))));
        throw e;
      }
    }

    private static long countDetailIndicesInSummary(ReportParser reportParserForExpectation) {
      return reportParserForExpectation.summary().records().stream().filter((ReportParser.Summary.Record each) -> each.detailIndex().isPresent()).count();
    }

    private static void assertAllRunnables(Runnable... runnables) {
      class TestFailed extends RuntimeException {
        TestFailed(Throwable cause) {
          super(cause);
        }
      }
      List<Throwable> throwables = new LinkedList<>();
      for (Runnable each : runnables) {
        try {
          each.run();
        } catch (AssertionError error) {
          throwables.add(error);
        }
      }
      if (!throwables.isEmpty()) {
        for (Throwable error : throwables) {
          System.err.println(".ASSERTION FAILED:");
          System.err.println("----");
          error.printStackTrace();
          System.err.println("----");
          System.err.println();
        }
        // Make sure the entire test method fails even when an assertion library throws a ComparisonFailure.
        // I did this when I change an assertion library for some reason, it may throw a ComparisonFailure and once it happens, test failure will be masked.
        throw new TestFailed(throwables.get(0));
      }
    }

    private static String detailSubjectFor(ReportParser reportParserForActualValue, int index) {
      return reportParserForActualValue.details().get(index).subject();
    }

    private static String detailBodyFor(ReportParser reportParserForActualValue, int index) {
      return String.join("\n", reportParserForActualValue.details().get(index).body());
    }
  }
}
