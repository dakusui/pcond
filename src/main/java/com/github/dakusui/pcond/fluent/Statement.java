package com.github.dakusui.pcond.fluent;

import com.github.dakusui.pcond.core.fluent.builtins.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.forms.Functions.elementAt;
import static com.github.dakusui.pcond.forms.Predicates.allOf;
import static com.github.dakusui.pcond.forms.Predicates.transform;
import static com.github.dakusui.pcond.internals.InternalUtils.makeSquashable;

@FunctionalInterface
public interface Statement<T> {
  default T statementValue() {
    throw new NoSuchElementException();
  }

  Predicate<T> statementPredicate();

  /**
   * Returns a transformer for a `String` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see StringTransformer
   */
  static StringTransformer<String>
  stringValue(String value) {
    return StringTransformer.create(() -> value);
  }

  /**
   * Returns a transformer for a `double` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see DoubleTransformer
   */
  static DoubleTransformer<Double>
  doubleValue(Double value) {
    return DoubleTransformer.create(() -> value);
  }

  /**
   * Returns a transformer for a `float` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see FloatTransformer
   */
  static FloatTransformer<Float>
  floatValue(Float value) {
    return FloatTransformer.create(() -> value);
  }

  /**
   * Returns a transformer for a `long` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see LongTransformer
   */
  static LongTransformer<Long>
  longValue(Long value) {
    return LongTransformer.create(() -> value);
  }

  /**
   * Returns a transformer for a `int` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see IntegerTransformer
   */
  static IntegerTransformer<Integer>
  integerValue(Integer value) {
    return IntegerTransformer.create(() -> value);
  }

  /**
   * Returns a transformer for a `short` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see ShortTransformer
   */
  static ShortTransformer<Short>
  shortValue(Short value) {
    return ShortTransformer.create(() -> value);
  }

  /**
   * Returns a transformer for a `boolean` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see BooleanTransformer
   */
  static BooleanTransformer<Boolean>
  booleanValue(Boolean value) {
    return BooleanTransformer.create(() -> value);
  }

  /**
   * Returns a transformer for a general `Object` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see ObjectTransformer
   */
  static <E>
  ObjectTransformer<E, E>
  objectValue(E value) {
    return ObjectTransformer.create(() -> value);
  }

  /**
   * Returns a transformer for a `List` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see ListTransformer
   */

  static <E>
  ListTransformer<List<E>, E>
  listValue(List<E> value) {
    return ListTransformer.create(() -> value);
  }

  /**
   * Returns a transformer for a `Stream` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see StreamTransformer
   */
  static <E>
  StreamTransformer<Stream<E>, E>
  streamValue(Stream<E> value) {
    return StreamTransformer.create(() -> value);
  }

  static Predicate<? super List<?>> createPredicateForAllOf(Statement<?>[] statements) {
    AtomicInteger i = new AtomicInteger(0);
    @SuppressWarnings("unchecked") Predicate<? super List<?>>[] predicates = Arrays.stream(statements)
        .map(e -> makeSquashable(transform(elementAt(i.getAndIncrement())).check("WHEN", (Predicate<? super Object>) e.statementPredicate())))
        .toArray(Predicate[]::new);
    return makeSquashable(allOf(predicates));
  }

  static <T> Statement<T> statement(T value, Predicate<T> predicate) {
    return objectValue(value).then().checkWithPredicate(predicate);
  }
}
