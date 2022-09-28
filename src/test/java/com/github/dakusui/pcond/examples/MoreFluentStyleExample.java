package com.github.dakusui.pcond.examples;

import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.pcond.utils.TestUtils;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static com.github.dakusui.pcond.fluent.Fluents.assertWhen;
import static com.github.dakusui.pcond.fluent.Fluents.value;
import static com.github.dakusui.pcond.forms.Functions.*;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class MoreFluentStyleExample {
  @Test
  public void test() {
    String givenValue = "helloWorld";
    assertWhen(value(givenValue)
        .exercise(TestUtils.stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("HELLOWORLD"));
  }

  @Test
  public void test2() {
    List<String> givenValues = asList("hello", "world");
    assertWhen(value(givenValues).elementAt(0)
        .exercise(TestUtils.stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("HELLO"));
  }

  @Test
  public void test3() {
    List<String> givenValues = asList("hello", "world");
    assertWhen(value(givenValues).elementAt(0)
        .exercise(TestUtils.stringToLowerCase())
        .then()
        .asString()
        .isEqualTo("HELLO"));
  }

  @Test(expected = ComparisonFailure.class)
  public void test4() {
    try {
      assertWhen(
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

    assertWhen(value(database)
        .exercise(lookUpMemberWith.apply(identifier))
        .then()
        .intoStringWith(memberLastName)
        .isNotNull()
        .isNotEmpty()
        .isEqualTo("Do"));
  }

  @Test
  public void givenKnownLastName_whenFindMembersByFullName_thenLastNastIsNotNullAndContainedInFullName() {
    MemberDatabase database = new MemberDatabase();
    String lastName = database.lookUp("0001")
        .orElseThrow(NoSuchElementException::new)
        .lastName();
    List<String> fullName = database.findMembersByLastName(lastName).get(0).toFullName();
    assertWhen(
        value(lastName)
            .then()
            .verifyWith(allOf(
                isNotNull(),
                not(isEmptyString()))),
        value(fullName).asListOfClass(String.class)
            .then()
            .contains("DOE"));
  }

  @Test
  public void givenValidName_whenValidatePersonName_thenPass() {
    String s = "John Doe";

    assertWhen(value(s).asString().split(" ").size()
        .then().isEqualTo(2));
  }

  @Test
  public void givenValidName_whenValidatePersonName_thenPass_2() {
    String s = "John doe";

    assertWhen(
        value(s).asString().split(" ").thenVerifyWith(allOf(
            transform(size()).check(isEqualTo(2)),
            transform(elementAt(0).andThen(cast(String.class))).check(matchesRegex("[A-Z][a-z]+")),
            transform(elementAt(1).andThen(cast(String.class))).check(matchesRegex("[A-Z][a-z]+"))
        )));
  }
}
