package com.github.dakusui.thincrest.examples;

import com.github.dakusui.pcond.fluent.Fluents;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.shared.utils.TestUtils;
import com.github.dakusui.thincrest.TestFluents;
import com.github.dakusui.thincrest.examples.sut.MemberDatabase;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static com.github.dakusui.pcond.forms.Predicates.isEmptyString;
import static com.github.dakusui.pcond.forms.Predicates.not;
import static com.github.dakusui.shared.TestUtils.validateStatement;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class MoreFluentStyleExample {
  @Test
  public void test() {
    String givenValue = "helloWorld";
    TestFluents.assertStatement(Fluents.stringStatement(givenValue)
        .toString(TestUtils.stringToLowerCase())
        .then()
        .isEqualTo("HELLOWORLD"));
  }


  @Test
  public void testExpectingException() {
    String givenValue = "helloWorld";
    TestFluents.assertStatement(Fluents.stringStatement(givenValue)
        .expectException(Exception.class, TestUtils.stringToLowerCase())
        .then()
        .isEqualTo("HELLOWORLD"));
  }

  @Test
  public void testExpectingException2() {
    String givenValue = "helloWorld";
    TestFluents.assertStatement(Fluents.stringStatement(givenValue)
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
    TestFluents.assertStatement(Fluents.listStatement(givenValues).elementAt(0)
        .toString(TestUtils.stringToLowerCase())
        .then()
        .isEqualTo("HELLO"));
  }

  @Test
  public void test3() {
    List<String> givenValues = asList("hello", "world");
    TestFluents.assertStatement(Fluents.listStatement(givenValues).elementAt(0)
        .toString(TestUtils.stringToLowerCase())
        .then()
        .isEqualTo("HELLO"));
  }

  @Test(expected = ComparisonFailure.class)
  public void test4() {
    try {
      TestFluents.assertAll(
          Fluents.stringStatement("hello").toUpperCase().then().isEqualTo("HELLO"),
          Fluents.stringStatement("world").toLowerCase().then().contains("WORLD"));
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

    TestFluents.assertStatement(Fluents.objectStatement(database)
        .toObject(lookUpMemberWith.apply(identifier))
        .toString(memberLastName)
        .then()
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
        Fluents.stringStatement(lastName)
            .then()
            .allOf()
            .isNotNull()
            .appendPredicateAsChild(not(isEmptyString())),
        Fluents.listStatement(fullName)
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
        Fluents.stringStatement(lastName)
            .then()
            .allOf()
            .isNotNull()
            .appendChild(v -> v.anyOf()
                .appendChild(w -> w.isNotNull().toPredicate())
                .appendChild(w -> w.isEmpty().toPredicate()).toPredicate())
            .appendChild(v -> v.isNotEmpty().toPredicate()),
        Fluents.listStatement(fullName)
            .then()
            .contains("DOE"));
  }

  @Test
  public void testAllOf() {
    MemberDatabase database = new MemberDatabase();
    String lastName = database.lookUp("0001")
        .orElseThrow(NoSuchElementException::new)
        .lastName();
    TestFluents.assertAll(
        Fluents.stringStatement(lastName)
            .then()
            .allOf()
            .appendChild(v -> v.isEqualTo("1"))
            .appendChild(v -> v.isEqualTo("2"))
            .appendChild(v -> v
                .anyOf()
                .appendChild(w -> w.isEqualTo("3"))
                .appendChild(w -> w.isEqualTo("4")))
            .appendChild(v -> v.isEqualTo("5")));
  }


  @Test
  public void givenValidName_whenValidatePersonName_thenPass() {
    String s = "John Doe";

    TestFluents.assertStatement(Fluents.stringStatement(s).split(" ").size()
        .then()
        .equalTo(2));
  }

  @Test
  public void givenValidName_whenValidatePersonName_thenPass_2() {
    String s = "John doe";

    TestFluents.assertStatement(
        Fluents.stringStatement(s)
            .split(" ")
            .then().allOf()
            .appendChild(tx -> tx.size().then().isEqualTo(2))
            .appendChild(tx -> tx.elementAt(0).asString().then().matchesRegex("[A-Z][a-z]+"))
            .appendChild(    tx -> tx.elementAt(1).asString().then().matchesRegex("[A-Z][a-z]+")));
  }

  @Test
  public void checkTwoValues() {
    String s = "HI";
    List<String> strings = asList("HELLO", "WORLD");

    TestFluents.assertAll(
        Fluents.stringStatement(s)
            .toString(TestUtils.stringToLowerCase())
            .then()
            .isEqualTo("HI"),
        Fluents.listStatement(strings)
            .then()
            .findElementsInOrder("HELLO", "WORLD"));
  }

  @Test
  public void checkTwoAspectsOfOneValue() {
    String s = "helloWorld";
    TestFluents.assertAll(
        Fluents.stringStatement(s)
            .then()
            .isNotNull(),
        Fluents.stringStatement(s).length()
            .then()
            .greaterThan(100));
  }

  @Test
  public void checkTwoAspectsOfOneValue_2() {
    List<String> list = asList("helloWorld", "HI");
    TestFluents.assertAll(
        Fluents.listStatement(list).size()
            .then()
            .greaterThan(3),
        Fluents.listStatement(list).elementAt(0).toString(v -> v)
            .then()
            .isNotNull(),
        Fluents.listStatement(list).elementAt(0).toString(v -> v).length()
            .then()
            .greaterThan(100));
  }

  @Test
  public void checkTwoAspectsOfOneValue_3a() {
    List<String> list = asList("helloWorld", "HI");
    TestFluents.assertAll(
        Fluents.listStatement(list).allOf()
            .appendChild(tx -> tx.size().then().greaterThan(3).toPredicate())
            .appendChild(tx -> tx.elementAt(0)
                .appendChild(ty -> ty.asString().then().isNotNull().toPredicate())
                .appendChild(ty -> ty.asString().length().then().greaterThan(100).toPredicate()).toPredicate()));
  }

  @Test
  public void checkTwoAspectsOfOneValue_3b() {
    List<String> list = asList("helloWorld", "HI");
    validateStatement(
        Fluents.listStatement(list).then().allOf()
            .appendChild(tx -> tx.size().then().greaterThan(3))
            .appendChild(tx -> tx.elementAt(0).asString().then().isNotNull())
            .appendChild(tx -> tx.elementAt(0).asString().length().then().greaterThan(100)));
  }

  @Test
  public void checkTwoAspectsOfOneValue_3c() {
    List<String> list = asList("helloWorld", "HI");
    validateStatement(
        Fluents.listStatement(list).then().allOf()
            .appendChild(tx -> tx.then().isNull())
            .appendChild(tx -> tx.elementAt(0).asString().then().isNotNull())
            .appendChild(tx -> tx.elementAt(0).asString().length().then().greaterThan(100)));
  }
}
