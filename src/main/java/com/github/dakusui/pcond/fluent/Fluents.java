package com.github.dakusui.pcond.fluent;

import com.github.dakusui.pcond.TestAssertions;
import com.github.dakusui.pcond.core.fluent.Fluent;
import com.github.dakusui.pcond.core.fluent.transformers.*;
import com.github.dakusui.pcond.core.printable.PrintableFunction;
import com.github.dakusui.pcond.core.printable.PrintablePredicate;
import com.github.dakusui.pcond.forms.Functions;

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
 * In order to avoid a conflict in `valueOf` method, this class is implemented as
 * a conventional class, not an `enum`.
 */
public class Fluents {
  private Fluents() {
  }

  /**
   * A function to provide a place-holder for `MoreFluent` style.
   * So far, no valid usage of this method in `MoreFluent` style and this method might be
   * dropped from future releases.
   *
   * @param <T> The type to which the place-holder is cast.
   * @return A place-holder variable that can be cast to any type.
   */
  public static <T> T value() {
    return Functions.value();
  }

  public static <T> void assertWhen(Statement<T> statement) {
    TestAssertions.assertThat(statement.statementValue(), statement.statementPredicate());
  }

  public static void assertWhen(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    TestAssertions.assertThat(values, createPredicateForAllOf(statements));
  }

  public static <T> void assumeWhen(Statement<T> statement) {
    TestAssertions.assumeThat(statement.statementValue(), statement.statementPredicate());
  }

  public static void assumeWhen(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    TestAssertions.assumeThat(values, createPredicateForAllOf(statements));
  }

  public static StringTransformer<String> valueOf(String value) {
    return fluent(value).asString();
  }

  public static DoubleTransformer<Double> valueOf(double value) {
    return fluent(value).asDouble();
  }

  public static FloatTransformer<Float> valueOf(float value) {
    return fluent(value).asFloat();
  }

  public static LongTransformer<Long> valueOf(long value) {
    return fluent(value).asLong();
  }

  public static IntegerTransformer<Integer> valueOf(int value) {
    return fluent(value).asInteger();
  }

  public static ShortTransformer<Short> valueOf(short value) {
    return fluent(value).asShort();
  }

  public static BooleanTransformer<Boolean> valueOf(boolean value) {
    return fluent(value).asBoolean();
  }

  public static <T> ObjectTransformer<T, T> valueOf(T value) {
    return fluent(value).asObject();
  }

  public static <E> ListTransformer<List<E>, E> valueOf(List<E> value) {
    return fluent(value).asListOf(FluentsInternal.value());
  }

  public static <E> StreamTransformer<Stream<E>, E> valueOf(Stream<E> value) {
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
