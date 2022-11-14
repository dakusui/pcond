package com.github.dakusui.thincrest;

import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.core.fluent3.builtins.*;
import com.github.dakusui.pcond.fluent.Fluents;
import com.github.dakusui.pcond.fluent.Statement;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
   * @param statements Statements to be verified
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
   * @param statements Statements to be verified
   */
  public static void assumeAll(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    TestAssertions.assumeThat(values, Fluents.createPredicateForAllOf(statements));
  }

  public static <R extends Matcher<R, R, String, String>>
  StringTransformer<R, String>
  stringStatement(String value) {
    return StringTransformer.create(value);
  }

  public static <R extends Matcher<R, R, Integer, Integer>>
  IntegerTransformer<R, Integer>
  integerStatement(Integer value) {
    return IntegerTransformer.create(value);
  }

  public static <R extends Matcher<R, R, Long, Long>>
  LongTransformer<R, Long>
  longStatement(Long value) {
    return LongTransformer.create(value);
  }

  public static <R extends Matcher<R, R, Short, Short>>
  ShortTransformer<R, Short>
  shortStatement(Short value) {
    return ShortTransformer.create(value);
  }

  public static <R extends Matcher<R, R, Double, Double>>
  DoubleTransformer<R, Double>
  doubleStatement(Double value) {
    return DoubleTransformer.create(value);
  }

  public static <R extends Matcher<R, R, Float, Float>>
  FloatTransformer<R, Float>
  floatStatement(Float value) {
    return FloatTransformer.create(value);
  }

  public static <R extends Matcher<R, R, List<E>, List<E>>, E>
  ListTransformer<R, List<E>, E>
  listStatement(List<E> value) {
    return ListTransformer.create(value);
  }

  public static <R extends Matcher<R, R, Stream<E>, Stream<E>>, E>
  StreamTransformer<R, Stream<E>, E>
  streamStatement(Stream<E> value) {
    return StreamTransformer.create(value);
  }

  public static <
      R extends Matcher<R, R, E, E>,
      E>
  ObjectTransformer<R, E, E>
  objectStatement(E value) {
    return ObjectTransformer.create(value);
  }
}
