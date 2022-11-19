package com.github.dakusui.pcond.ut.fluent3;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.core.fluent3.Transformer;
import com.github.dakusui.pcond.core.fluent3.builtins.ObjectChecker;
import com.github.dakusui.pcond.core.fluent3.builtins.StringTransformer;
import com.github.dakusui.pcond.forms.Printables;
import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Objects;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.makeTrivial;
import static com.github.dakusui.pcond.fluent.Fluents.stringValue;
import static com.github.dakusui.pcond.forms.Functions.identity;
import static com.github.dakusui.pcond.forms.Predicates.isEqualTo;
import static com.github.dakusui.pcond.forms.Predicates.transform;
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
              .isNull().toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void thirdExample() {
      assertAll(
          stringValue("Hello")
              .allOf()
              .then()
              .isNotNull()
              .isNull()
              .toStatement());
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
          stringValue("Hello").length().then().greaterThan(10).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void fifth() {
      assertAll(
          stringValue("Hello5").length().then().greaterThan(10).lessThan(1).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test9() {
      assertAll(
          stringValue("Hello5")
              .then()
              .check(v -> v.isNull().toPredicate())
              .toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test7() {
      assertAll(
          stringValue("Hello5")
              .check(tx -> tx.then().isNotNull().toPredicate())
              .check(tx -> tx.then().isNull().toPredicate()).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test6b() {
      assertAll(
          stringValue("Hello5")
              .check(tx -> tx.then().isNull().toPredicate()).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test6c() {
      assertAll(
          stringValue("Hello5")
              .anyOf()
              .check(tx -> tx.then().isNull().toPredicate()).toStatement());
    }

    @Test(expected = ComparisonFailure.class)
    public void test6d() {
      assertAll(
          stringValue("Hello5")
              .check(tx -> tx.checkWithPredicate(Objects::isNull).toPredicate())
              .check(tx -> tx.checkWithPredicate(v -> !Objects.isNull(v)).toPredicate()).toStatement()
      );
    }

    /**
     * Works without calling `toStatement()` method.
     */
    @Test(expected = ComparisonFailure.class)
    public void test7_a() {
      assertAll(
          stringValue("Hello5")
              .check(tx -> tx.then().isNotNull().toPredicate())
              .check(tx -> tx.then().isNull().toPredicate()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test6() {
      assertAll(
          stringValue("Hello5")
              .check(tx -> tx.length().then().greaterThan(10).done()));
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
*/

    @Test
    public void test7_C() {
      assertAll(
          stringValue("Hello5")
              .check(tx -> tx
                  .check(ty -> ty.length()
                      .then()
                      .greaterThanOrEqualTo(800)
                      .lessThan(1000)
                      .done())
                  .check(ty -> ty.then().isNotNull().done()).done())
              .check(tx -> tx.then().isNull().done()));
    }

    @Test
    public void givenBookTitle_whenLength_thenNotNullAndAppropriateLength() {
      assertAll(
          stringValue("De Bello Gallico")
              .check(ty -> ty.then().isNotNull().done())
              .check(ty -> ty.length().then()
                  .greaterThanOrEqualTo(10)
                  .lessThan(40)
                  .done()));
    }

    @Test
    public void givenBookTitleAndAbstract_whenCheckThem_thenTheyAreNotNullAndAppropriateLength() {
      String bookTitle = "De Bello Gallico";
      String bookAbstract = "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.";
      assertAll(
          stringValue(bookTitle)
              .check(ty -> ty.then().isNotNull().done())
              .check(ty -> ty.length().then()
                  .greaterThanOrEqualTo(10)
                  .lessThan(40)
                  .done()),
          stringValue(bookAbstract)
              .check(ty -> ty.then().isNotNull().done())
              .check(ty -> ty.length().then()
                  .greaterThanOrEqualTo(200)
                  .lessThan(400)
                  .done()));
    }

    @Test
    public void givenBook_whenCheckTitleAndAbstract_thenTheyAreNotNullAndAppropriateLength() {
      Book book = new Book("De Bello Gallico", "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.");
      assertAll(
          new BookTransformer(book)
              .check(b -> b.title()
                  .check(ty -> ty.then().isNotNull().toPredicate()).done())
              .check(b -> b.abstractText()
                  .check(ty -> ty.then().isNotNull().toPredicate()).done()));
    }

    /*
    @Test
    public void givenBook_whenCheckTitleAndAbstract_thenTheyAreNotNullAndAppropriateLength() {
      Book book = new Book("De Bello Gallico", "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.");
      assertAll(
          new BookTransformer(book)
              .check(b -> b.title()
                  .check(ty -> ty.then().isNotNull().done())
                  .check(ty -> ty.length().then()
                      .greaterThanOrEqualTo(10)
                      .lessThan(40)
                      .done()).done())
              .check(b -> b.abstractText()
                  .check(ty -> ty.then().isNotNull().done())
                  .check(ty -> ty.length().then()
                      .greaterThanOrEqualTo(200)
                      .lessThan(400)
                      .done()));
    }
     */

    @Test(expected = ComparisonFailure.class)
    public void test6() {
      assertAll(
          stringValue("Hello5")
              .check(tx -> tx.length().then().greaterThanOrEqualTo(10).lessThan(100).done()));
    }

    @Test(expected = ComparisonFailure.class)
    public void test6b() {
      assertAll(stringValue("Hello5")
          .length()
          .then()
          .greaterThanOrEqualTo(10)
          .lessThan(100));
    }


    @Test
    public void thirdExample_a() {
      assertAll(
          stringValue("Hello")
              .length()
              .then()
              .allOf()
              .isNotNull()
              .isNull());
    }

    @Test
    public void test7() {
      assertAll(
          stringValue("Hello5")
              .check(tx -> tx.then().isNotNull().done())
              .check(tx -> tx.then().isNull().done()));
    }

    static class Book {
      private final String abstractText;
      private final String title;

      Book(String abstractText, String title) {
        this.abstractText = abstractText;
        this.title = title;
      }

      String title() {
        return title;
      }

      String abstractText() {
        return abstractText;
      }
    }

    static class BookTransformer extends CustomTransformer<BookTransformer, Book> {
      public BookTransformer(Book rootValue) {
        super(() -> rootValue);
      }

      public StringTransformer<BookTransformer, Book> title() {
        return toString(Printables.function("title", Book::title));
      }

      public StringTransformer<BookTransformer, Book> abstractText() {
        return toString(Printables.function("title", Book::abstractText));
      }
    }


    static abstract class CustomTransformer<
        TX extends Transformer<
            TX,
            TX,
            ObjectChecker<TX, OIN, OIN>,
            OIN,
            OIN>,
        OIN> extends
        Matcher.Base<
            TX,
            TX,
            OIN,
            OIN> implements
        AbstractObjectTransformer<
            TX,
            TX,
            ObjectChecker<TX, OIN, OIN>,
            OIN,
            OIN> {
      public CustomTransformer(Supplier<OIN> rootValue) {
        super(rootValue, null);
      }

      @Override
      public ObjectChecker<TX, OIN, OIN> createCorrespondingChecker(TX root) {
        throw new UnsupportedOperationException();
      }
    }

    @Test
    public void makeTrivialTest() {
      assertThat("hello", transform(makeTrivial(identity())).check(transform(makeTrivial(identity())).check(isEqualTo("HELLO"))));
    }
  }
}
