package com.github.dakusui.thincrest;

import com.github.dakusui.pcond.fluent.Fluents;
import com.github.dakusui.pcond.fluent.Statement;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public enum TestFluents {
  ;

  /**
   * Fluent version of {@link TestAssertions#assertThat(Object, Predicate)}.
   *
   * @param statement A statement to be verified
   * @param <T>       The type of the value to be verified which a given statement holds.
   */
  public static <T> void assertStatement(Statement<T> statement) {
    TestAssertions.assertThat(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link TestAssertions#assertThat(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * You can use {@link TestFluents#assertStatement(Statement)}, if you have only one statement to be verified, for readability's sake.
   *
   * @param statements Statements to be verified
   * @see TestFluents#assertStatement(Statement)
   */
  public static void assertAll(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    TestAssertions.assertThat(values, Fluents.createPredicateForAllOf(statements));
  }

  /**
   * Fluent version of {@link TestAssertions#assumeThat(Object, Predicate)}.
   *
   * @param statement A statement to be verified
   */
  public static <T> void assumeStatement(Statement<T> statement) {
    TestAssertions.assumeThat(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link TestAssertions#assumeThat(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * You can use {@link TestFluents#assumeStatement(Statement)}}, if you have only one statement to be verified, for readability's sake.
   *
   * @param statements Statements to be verified
   * @see TestFluents#assumeStatement(Statement)
   */
  public static void assumeAll(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    TestAssertions.assumeThat(values, Fluents.createPredicateForAllOf(statements));
  }
}
