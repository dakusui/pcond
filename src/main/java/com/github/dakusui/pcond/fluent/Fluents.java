package com.github.dakusui.pcond.fluent;

import com.github.dakusui.pcond.Assertions;
import com.github.dakusui.pcond.Ensures;
import com.github.dakusui.pcond.Requires;
import com.github.dakusui.pcond.TestAssertions;
import com.github.dakusui.pcond.core.fluent.Fluent;
import com.github.dakusui.pcond.core.fluent.transformers.*;
import com.github.dakusui.pcond.core.printable.PrintableFunction;
import com.github.dakusui.pcond.core.printable.PrintablePredicate;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.forms.Functions.elementAt;
import static com.github.dakusui.pcond.forms.Predicates.allOf;
import static com.github.dakusui.pcond.forms.Predicates.transform;
import static java.util.stream.Collectors.toList;

/**
 * An "entry-point" class to write a "fluent" style tests.
 * Since the overloaded methods `value` are important entry-points of this interface, in order to avoid confusing users by `valueOf` method in `Enum`,
 * this class is implemented as a conventional class, not an `enum`.
 */
public class Fluents {
  private Fluents() {
  }

  /**
   * A method to return a value for a "casting placeholder value".
   *
   * @param <E> Type to cast to.
   * @return Casting placeholder value
   */
  public static <E> E value() {
    return null;
  }

  /**
   * A function to provide a place-holder for `MoreFluent` style.
   * So far, no valid usage of this method in `MoreFluent` style and this method might be
   * dropped from future releases.
   *
   * @param <T> The type to which the place-holder is cast.
   * @return A place-holder variable that can be cast to any type.
   */
  public static <T> T $() {
    return value();
  }

  /**
   * Fluent version of {@link TestAssertions#assertThat(Object, Predicate)}.
   *
   * @param statement A statement to be verified
   * @param <T>       The type of the value to be verified which a given statement holds.
   */
  public static <T> void assertThat(Statement<T> statement) {
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
    TestAssertions.assertThat(values, createPredicateForAllOf(statements));
  }

  /**
   * Fluent version of {@link TestAssertions#assumeThat(Object, Predicate)}.
   *
   * @param statement A statement to be verified
   */
  public static <T> void assumeThat(Statement<T> statement) {
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
    TestAssertions.assumeThat(values, createPredicateForAllOf(statements));
  }


  /**
   * Fluent version of {@link Requires#requireArgument(Object, Predicate)} (Object, Predicate)}.
   *
   * @param statement A statement to be verified
   */
  public static <T> T requireArgument(Statement<T> statement) {
    return Requires.requireArgument(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Requires#requireArgument(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  public static void requireArguments(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    Requires.requireArgument(values, createPredicateForAllOf(statements));
  }


  public static <T> T require(Statement<T> statement) {
    return Requires.require(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Requires#require(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  public static void require(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    Requires.require(values, createPredicateForAllOf(statements));
  }

  public static <T> T requireState(Statement<T> statement) {
    return Requires.requireState(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Requires#requireState(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  @SafeVarargs
  public static <T> void requireStates(Statement<T>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    Requires.requireState(values, createPredicateForAllOf(statements));
  }

  public static <T> T ensure(Statement<T> statement) {
    return Ensures.ensure(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Ensures#ensure(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  @SafeVarargs
  public static <T> void ensure(Statement<T>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    Ensures.ensure(values, createPredicateForAllOf(statements));
  }

  /**
   * Fluent version of {@link Ensures#ensureState(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statement A statement to be verified
   */
  public static <T> T ensureState(Statement<T> statement) {
    return Ensures.ensureState(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Ensures#ensureState(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  @SafeVarargs
  public static <T> void ensureStates(Statement<T>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    Ensures.ensureState(values, createPredicateForAllOf(statements));
  }

  /**
   * Fluent version of {@link Assertions#that(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statement A statement to be verified
   */
  public static <T> boolean that(Statement<T> statement) {
    return Assertions.that(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Assertions#that(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  @SafeVarargs
  public static <T> boolean all(Statement<T>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    return Assertions.that(values, createPredicateForAllOf(statements));
  }

  /**
   * Fluent version of {@link Assertions#precondition(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statement A statement to be verified
   */
  public static <T> boolean precondition(Statement<T> statement) {
    return Assertions.precondition(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Assertions#precondition(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  @SafeVarargs
  public static <T> boolean preconditions(Statement<T>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    return Assertions.precondition(values, createPredicateForAllOf(statements));
  }

  /**
   * Fluent version of {@link Assertions#postcondition(Object, Predicate)}.
   *
   * @param statement A statement to be verified
   */
  public static <T> boolean postcondition(Statement<T> statement) {
    return Assertions.postcondition(statement.statementValue(), statement.statementPredicate());
  }

  /**
   * Fluent version of {@link Assertions#postcondition(Object, Predicate)}.
   * Use this method when you need to verify multiple values.
   *
   * @param statements Statements to be verified
   */
  @SafeVarargs
  public static <T> boolean postconditions(Statement<T>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    return Assertions.postcondition(values, createPredicateForAllOf(statements));
  }

  /**
   * Returns a transformer for a `String` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see StringTransformer
   */
  public static StringTransformer<String> value(String value) {
    return fluent(value).asString();
  }

  /**
   * Returns a transformer for a `double` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see DoubleTransformer
   */
  public static DoubleTransformer<Double> value(double value) {
    return fluent(value).asDouble();
  }

  /**
   * Returns a transformer for a `float` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see FloatTransformer
   */
  public static FloatTransformer<Float> value(float value) {
    return fluent(value).asFloat();
  }

  /**
   * Returns a transformer for a `long` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see LongTransformer
   */
  public static LongTransformer<Long> value(long value) {
    return fluent(value).asLong();
  }

  /**
   * Returns a transformer for a `int` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see IntegerTransformer
   */
  public static IntegerTransformer<Integer> value(int value) {
    return fluent(value).asInteger();
  }

  /**
   * Returns a transformer for a `short` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see ShortTransformer
   */
  public static ShortTransformer<Short> value(short value) {
    return fluent(value).asShort();
  }

  /**
   * Returns a transformer for a `boolean` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see BooleanTransformer
   */
  public static BooleanTransformer<Boolean> value(boolean value) {
    return fluent(value).asBoolean();
  }

  /**
   * Returns a transformer for a general `Object` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see ObjectTransformer
   */
  public static <T> ObjectTransformer<T, T> value(T value) {
    return fluent(value).asObject();
  }

  /**
   * Returns a transformer for a `List` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see ListTransformer
   */
  public static <E> ListTransformer<List<E>, E> value(List<E> value) {
    return fluent(value).asListOf(FluentsInternal.value());
  }

  /**
   * Returns a transformer for a `Stream` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see StreamTransformer
   */
  public static <E> StreamTransformer<Stream<E>, E> value(Stream<E> value) {
    return fluent(value).asStreamOf(FluentsInternal.value());
  }

  private static <T> Fluent<T> fluent(T value) {
    return new Fluent<>("WHEN", value);
  }

  private static Predicate<? super List<?>> createPredicateForAllOf(Statement<?>[] statements) {
    AtomicInteger i = new AtomicInteger(0);
    @SuppressWarnings("unchecked") Predicate<? super List<?>>[] predicates = Arrays.stream(statements)
        .map(e -> makeTrivial(transform(makeTrivial(elementAt(i.getAndIncrement()))).check((Predicate<? super Object>) e.statementPredicate())))
        .toArray(Predicate[]::new);
    return makeTrivial(allOf(predicates));
  }

  private static <T> Predicate<T> makeTrivial(Predicate<T> predicates) {
    return ((PrintablePredicate<T>) predicates).makeTrivial();
  }

  private static <T, R> Function<T, R> makeTrivial(Function<T, R> predicates) {
    return ((PrintableFunction<T, R>) predicates).makeTrivial();
  }

  @FunctionalInterface
  public interface Statement<T> extends Predicate<T> {
    default T statementValue() {
      throw new NoSuchElementException();
    }

    default Predicate<T> statementPredicate() {
      return this;
    }
  }
}
