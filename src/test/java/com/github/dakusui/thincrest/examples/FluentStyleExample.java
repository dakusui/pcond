package com.github.dakusui.thincrest.examples;

import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.thincrest.examples.sut.MemberDatabase;
import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.forms.Printables.function;
import static com.github.dakusui.shared.FluentTestUtils.when;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class FluentStyleExample {
  @Test
  public void test1() {
    String id = "0001";
    MemberDatabase database = new MemberDatabase();
    Function<MemberDatabase, String> lookUpMemberAndGetLastName = function(
        () -> format("lookUpMemberAndGetLastName[%s]", id),
        d -> d.lookUp(id)
            .orElseThrow(NoSuchElementException::new)
            .lastName());

    assertThat(
        database,
        when().asValueOfClass(MemberDatabase.class)
            .exercise(lookUpMemberAndGetLastName)
            .then()
            .isNotNull());
  }

  @Test
  public void test2() {
    String identifier = "0001";
    MemberDatabase database = new MemberDatabase();
    Function<String, Function<MemberDatabase, MemberDatabase.Member>> lookUpMemberWith =
        id -> Printables.function(
            () -> format("lookUpMember[%s]", id),
            d -> d.lookUp(id).orElseThrow(NoSuchElementException::new));
    Function<MemberDatabase.Member, String> memberLastName =
        Printables.function("memberLastName", MemberDatabase.Member::lastName);

    assertThat(
        database,
        when().asValueOfClass(MemberDatabase.class)
            .exercise(lookUpMemberWith.apply(identifier))
            .then()
            .toStringWith(memberLastName)
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
    assertThat(
        asList(lastName, fullName),
        allOf(
            when().asListOfClass(String.class)
                .elementAt(0)
                .then().verify(allOf(
                    isNotNull(),
                    not(isEmptyString()))),
            when().asListOfClass(String.class)
                .then()
                .contains(lastName)));
  }
}
