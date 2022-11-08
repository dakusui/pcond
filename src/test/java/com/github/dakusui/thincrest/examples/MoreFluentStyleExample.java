package com.github.dakusui.thincrest.examples;

import com.github.dakusui.pcond.core.fluent.Checker;
import com.github.dakusui.pcond.core.fluent.checkers.StringChecker;
import com.github.dakusui.pcond.core.fluent.transformers.ListTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.StringTransformer;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.shared.utils.TestUtils;
import com.github.dakusui.thincrest.TestFluents;
import com.github.dakusui.thincrest.examples.sut.MemberDatabase;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static com.github.dakusui.pcond.fluent.Fluents.statementAllOf;
import static com.github.dakusui.pcond.fluent.Fluents.value;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.shared.TestUtils.validateStatement;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class MoreFluentStyleExample {
  @Test
  public void test() {
    String givenValue = "helloWorld";
    TestFluents.assertStatemet(value(givenValue)
        .exercise(TestUtils.stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("HELLOWORLD"));
  }


  @Test
  public void testExpectingException() {
    String givenValue = "helloWorld";
    TestFluents.assertStatemet(value(givenValue)
        .expectException(Exception.class, TestUtils.stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("HELLOWORLD"));
  }

  @Test
  public void testExpectingException2() {
    String givenValue = "helloWorld";
    TestFluents.assertStatemet(value(givenValue)
        .expectException(Exception.class, throwRuntimeException())
        .getCause()
        .then()
        .isNotNull());
  }

  private static Function<String, Object> throwRuntimeException() {
    return Printables.function("throwRuntimeException", v -> {
      throw new RuntimeException();
    });
  }


  @Test
  public void test2() {
    List<String> givenValues = asList("hello", "world");
    TestFluents.assertStatemet(value(givenValues).elementAt(0)
        .exercise(TestUtils.stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("HELLO"));
  }

  @Test
  public void test3() {
    List<String> givenValues = asList("hello", "world");
    TestFluents.assertStatemet(value(givenValues).elementAt(0)
        .exercise(TestUtils.stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("HELLO"));
  }

  @Test(expected = ComparisonFailure.class)
  public void test4() {
    try {
      TestFluents.assertAll(
          value("hello").toUpperCase().then().isEqualTo("HELLO"),
          value("world").toLowerCase().then().contains("WORLD"));
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test
  public void test5() {
    String identifier = "0001";
    MemberDatabase database = new MemberDatabase();
    Function<String, Function<MemberDatabase, MemberDatabase.Member>> lookUpMemberWith =
        id -> Printables.function(
            () -> format("lookUpMember[%s]", id),
            d -> d.lookUp(id).orElseThrow(NoSuchElementException::new));
    Function<MemberDatabase.Member, String> memberLastName =
        Printables.function("memberLastName", MemberDatabase.Member::lastName);

    TestFluents.assertStatemet(value(database)
        .exercise(lookUpMemberWith.apply(identifier))
        .then()
        .intoStringWith(memberLastName)
        .isNotNull()
        .isNotEmpty()
        .isEqualTo("Do"));
  }

  /**
   * org.junit.ComparisonFailure: Value:["Doe",["John","Doe","PhD"]] violated: (at[0] WHEN(treatAsIs (isNotNull&&!isEmpty))&&at[1] WHEN(treatAsList contains["DOE"]))
   */
  @Test
  public void givenKnownLastName_whenFindMembersByFullName_thenLastNastIsNotNullAndContainedInFullName() {
    MemberDatabase database = new MemberDatabase();
    String lastName = database.lookUp("0001")
        .orElseThrow(NoSuchElementException::new)
        .lastName();
    List<String> fullName = database.findMembersByLastName(lastName).get(0).toFullName();
    TestFluents.assertAll(
        value(lastName)
            .then()
            .verifyWith(allOf(
                isNotNull(),
                not(isEmptyString()))),
        value(fullName).asListOfClass(String.class)
            .then()
            .contains("DOE"));
  }

  /**
   * org.junit.ComparisonFailure: Value:["Doe",["John","Doe","PhD"]] violated: (at[0] WHEN(DUMMY_FUNCTION:ALWAYSTHROW (isNotNull&&!isEmpty))&&at[1] WHEN(treatAsList contains["DOE"]))
   */
  @Test
  public void givenKnownLastName_whenFindMembersByFullName_thenLastNastIsNotNullAndContainedInFullName_2() {
    MemberDatabase database = new MemberDatabase();
    String lastName = database.lookUp("0001")
        .orElseThrow(NoSuchElementException::new)
        .lastName();
    List<String> fullName = database.findMembersByLastName(lastName).get(0).toFullName();
    TestFluents.assertAll(
        value(lastName)
            .then()
            .with(Checker::isNotNull)
            .with(StringChecker::isNotEmpty),
        value(fullName).asListOfClass(String.class)
            .then()
            .contains("DOE"));
  }

  @Test
  public void givenValidName_whenValidatePersonName_thenPass() {
    String s = "John Doe";

    TestFluents.assertStatemet(value(s).asString().split(" ").size()
        .then().isEqualTo(2));
  }

  @Test
  public void givenValidName_whenValidatePersonName_thenPass_2() {
    String s = "John doe";

    TestFluents.assertStatemet(
        value(s)
            .asString()
            .split(" ")
            .thenWith((ListTransformer<String, String> tx) -> statementAllOf(
                tx.originalInputValue(),
                tx.size().then().isEqualTo(2),
                tx.elementAt(0).asString().then().matchesRegex("[A-Z][a-z]+"),
                tx.elementAt(1).asString().then().matchesRegex("[A-Z][a-z]+"))));
  }

  @Test
  public void checkTwoValues() {
    String s = "HI";
    List<String> strings = asList("HELLO", "WORLD");

    TestFluents.assertAll(
        value(s).asString()
            .exercise(TestUtils.stringToLowerCase())
            .then()
            .isEqualTo("HI"),
        value(strings).asListOf((String) value())
            .then()
            .findElementsInOrder("HELLO", "WORLD"));
  }

  @Test
  public void checkTwoAspectsOfOneValue() {
    String s = "helloWorld";
    TestFluents.assertAll(
        value(s)
            .then()
            .isNotNull(),
        value(s).length()
            .then()
            .greaterThan(100));
  }

  @Test
  public void checkTwoAspectsOfOneValue_2() {
    List<String> list = asList("helloWorld", "HI");
    String s = list.get(0);
    TestFluents.assertAll(
        value(list).size()
            .then()
            .greaterThan(3),
        value(list).elementAt(0).asString()
            .then()
            .isNotNull(),
        value(list).elementAt(0).asString().length()
            .then()
            .greaterThan(100));
  }

  @Test
  public void checkTwoAspectsOfOneValue_3a() {
    List<String> list = asList("helloWorld", "HI");
    String s = list.get(0);
    TestFluents.assertAll(
        value(list).thenAllOf(asList(
            tx -> tx.size().then().greaterThan(3),
            tx -> tx.elementAt(0).asString().then().isNotNull(),
            tx -> tx.elementAt(0).asString().length().then().greaterThan(100))));
  }

  @Test
  public void checkTwoAspectsOfOneValue_3aa() {
    List<String> list = asList("helloWorld", "HI");
    String s = list.get(0);
    TestFluents.assertAll(
        value(list).thenAllOf(asList(
            tx -> tx.then().isEmpty(),
            tx -> tx.elementAt(0).asString().then().isNotNull(),
            tx -> tx.elementAt(0).asString().length().then().greaterThan(100),
            tx -> tx.elementAt(0).asString().then().findSubstrings("XYZ"))));
  }

  @Test
  public void checkTwoAspectsOfOneValue_3b() {
    List<String> list = asList("helloWorld", "HI");
    String s = list.get(0);
    validateStatement(
        value(list).thenAllOf(asList(
            tx -> tx.size().then().greaterThan(3),
            tx -> tx.elementAt(0).asString().then().isNotNull(),
            tx -> tx.elementAt(0).asString().length().then().greaterThan(100))));
  }

  @Test
  public void checkTwoAspectsOfOneValue_3c() {
    List<String> list = asList("helloWorld", "HI");
    String s = list.get(0);
    validateStatement(
        value(list).thenAllOf(asList(
            tx -> tx.then().isNull(),
            tx -> tx.elementAt(0).asString().then().isNotNull(),
            tx -> tx.elementAt(0).asString().length().then().greaterThan(100))));
  }

  @Test
  public void checkTwoAspectsOfOneValue_4() {
    List<String> list = asList("helloWorld", "HI");
    TestFluents.assertStatemet(
        value(list).thenAllOf(asList(
            tx -> tx.size().then().greaterThan(3),
            tx -> tx.elementAt(0).asString().thenAllOf(asList(
                (StringTransformer<List<String>> ty) -> ty.then().isNotNull(),
                (StringTransformer<List<String>> ty) -> ty.asString().length().then().greaterThan(100))))));
  }

  @Test
  public void checkTwoAspectsOfOneValue_5() {
    List<String> list = asList("helloWorld", "HI");
    TestFluents.assertStatemet(
        value(list).thenAllOf(asList(
            tx -> tx.size().then().greaterThan(3),
            tx -> tx.elementAt(0).thenAllOf(asList(
                ty -> ty.then().isNotNull(),
                ty -> ty.asString().length().then().greaterThan(100))))));
  }

}
