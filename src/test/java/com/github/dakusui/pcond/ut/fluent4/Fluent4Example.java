package com.github.dakusui.pcond.ut.fluent4;

import com.github.dakusui.pcond.core.fluent.builtins.StringTransformer;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;
import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.forms.Functions.length;
import static com.github.dakusui.pcond.forms.Predicates.isEqualTo;
import static com.github.dakusui.pcond.internals.InternalUtils.makeTrivial;
import static com.github.dakusui.thincrest.TestFluents.assertAll;
import static com.github.dakusui.thincrest.TestFluents.assertStatement;
import static java.util.Objects.requireNonNull;

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
          .transform(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue()
                  .toPredicate())
          .transform(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue()
                  .toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_6() {
      assertStatement(stringTransformer("INPUT_VALUE")
          .transform(
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
          .transform(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue()
                  .toPredicate())
          .checkWithPredicate(Predicates.transform(length()).check(isEqualTo(10))));
    }
  }

  @Ignore
  public static class OnGoing {
    @Test
    public void test_allOf_inWhen_6a() {
      assertStatement(stringTransformer("helloWorld1")
          .transform(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue()
                  .toPredicate())
          .checkWithPredicate(Predicates.transform(length()).check(isEqualTo(10))));
    }

    @Test(expected = IllegalStateException.class)
    public void test_allOf_inWhen_6b() {
      assertStatement(stringTransformer("helloWorld2")
          .transform(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue().done())
          .checkWithPredicate(Predicates.transform(length()).check(isEqualTo(10)))
          .then());
    }

    @Test(expected = ComparisonFailure.class)
    public void test_allOf_inWhen_6c() {
      assertStatement(stringTransformer("helloWorld2")
          .transform(
              tx -> tx.toLowerCase()
                  .parseBoolean()
                  .then()
                  .isTrue().done())
          .checkWithPredicate(Predicates.transform(length()).check(isEqualTo(10))));
    }

    @Test
    public void givenBook_whenCheckTitleAndAbstract_thenTheyAreNotNullAndAppropriateLength() {
      Book book = new Book(
          "De Bello Gallico",
          "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, "
              + "aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.");
      assertAll(
          new BookTransformer(book)
              .transform(tx -> tx.title()
                  .transform(ty -> ty
                      .then()
                      .isNotNull().done())
                  .transform(ty -> ty
                      .length()
                      .then()
                      .greaterThanOrEqualTo(10)
                      .lessThan(40)
                      .done())
                  .done())
              .transform(tx -> tx.abstractText()
                  .transform(ty -> ty
                      .then()
                      .isNotNull().done())
                  .transform(ty -> ty
                      .length()
                      .then()
                      .greaterThanOrEqualTo(200)
                      .lessThan(400).done())
                  .done()));
    }

    public static class Book {
      private final String abstractText;
      private final String title;

      public Book(String title, String abstractText) {
        this.abstractText = abstractText;
        this.title = title;
      }

      String title() {
        return title;
      }

      String abstractText() {
        return abstractText;
      }

      @Override
      public String toString() {
        return "Book:[title:<" + title + ">, abstract:<" + abstractText + ">]";
      }
    }

    public static class BookTransformer extends CustomTransformer<BookTransformer, Book> {
      public BookTransformer(Book rootValue) {
        super(rootValue);
      }

      public StringTransformer<Book> title() {
        return toString(Printables.function("title", Book::title));
      }

      public StringTransformer<Book> abstractText() {
        return toString(Printables.function("title", Book::abstractText));
      }

      @Override
      public BookTransformer transform(Function<BookTransformer, Predicate<Book>> clause) {
        requireNonNull(clause);
        return this.addTransformAndCheckClause(tx -> clause.apply((BookTransformer) tx));
      }

      @Override
      protected BookTransformer create(Book value) {
        return new BookTransformer(value);
      }

    }
  }

  private static StringTransformer.Impl<String> stringTransformer(String value) {
    return new StringTransformer.Impl<>(() -> value, makeTrivial(Functions.identity()));
  }
}