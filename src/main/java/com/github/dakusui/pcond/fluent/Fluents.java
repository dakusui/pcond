package com.github.dakusui.pcond.fluent;

import com.github.dakusui.pcond.core.fluent.builtins.*;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.forms.Functions.elementAt;
import static com.github.dakusui.pcond.forms.Predicates.allOf;
import static com.github.dakusui.pcond.forms.Predicates.transform;

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
   * Returns a transformer for a `String` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see StringTransformer
   */
  public static StringTransformer<String>
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
  public static DoubleTransformer<Double>
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
  public static FloatTransformer<Float>
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
  public static
  LongTransformer<Long>
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
  public static IntegerTransformer<Integer>
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
  public static ShortTransformer<Short>
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
  public static BooleanTransformer<Boolean>
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
  public static <E>
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

  public static <E>
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
  public static <E>
  StreamTransformer<Stream<E>, E>
  streamValue(Stream<E> value) {
    return StreamTransformer.create(() -> value);
  }

  public static Predicate<? super List<?>> createPredicateForAllOf(Statement<?>[] statements) {
    AtomicInteger i = new AtomicInteger(0);
    @SuppressWarnings("unchecked") Predicate<? super List<?>>[] predicates = Arrays.stream(statements)
        .map(e -> InternalUtils.makeTrivial(transform(InternalUtils.makeTrivial(elementAt(i.getAndIncrement()))).check((Predicate<? super Object>) e.statementPredicate())))
        .toArray(Predicate[]::new);
    return InternalUtils.makeTrivial(allOf(predicates));
  }

  public static <T> Statement<T> statement(T value, Predicate<T> predicate) {
    return objectValue(value).then().checkWithPredicate(predicate);
  }
}
