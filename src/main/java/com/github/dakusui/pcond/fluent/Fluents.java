package com.github.dakusui.pcond.fluent;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.core.fluent3.builtins.*;
import com.github.dakusui.pcond.core.printable.PrintableFunction;
import com.github.dakusui.pcond.core.printable.PrintablePredicate;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.forms.Functions.elementAt;
import static com.github.dakusui.pcond.forms.Predicates.allOf;
import static com.github.dakusui.pcond.forms.Predicates.transform;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
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
   * Returns a transformer for a `String` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see StringTransformer
   */
  public static <R extends Matcher<R, R, String, String>>
  StringTransformer<R, String>
  stringValue(String value) {
    return StringTransformer.create(() ->value);
  }


  /**
   * Returns a transformer for a `double` value.
   *
   * @param value A value for which a transformer is created.
   * @return A transformer for a {@code value}.
   * @see DoubleTransformer
   */
  public static <R extends Matcher<R, R, Double, Double>>
  DoubleTransformer<R, Double>
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
  public static <R extends Matcher<R, R, Float, Float>>
  FloatTransformer<R, Float>
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
  public static <R extends Matcher<R, R, Long, Long>>
  LongTransformer<R, Long>
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
  public static <R extends Matcher<R, R, Integer, Integer>>
  IntegerTransformer<R, Integer>
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
  public static <R extends Matcher<R, R, Short, Short>>
  ShortTransformer<R, Short>
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
  public static <R extends Matcher<R, R, Boolean, Boolean>>
  BooleanTransformer<R, Boolean>
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
  public static <
      R extends Matcher<R, R, E, E>,
      E>
  ObjectTransformer<R, E, E>
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

  public static <R extends Matcher<R, R, List<E>, List<E>>, E>
  ListTransformer<R, List<E>, E>
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
  public static <R extends Matcher<R, R, Stream<E>, Stream<E>>, E>
  StreamTransformer<R, Stream<E>, E>
  streamValue(Stream<E> value) {
    return StreamTransformer.create(() -> value);
  }

  public static Predicate<? super List<?>> createPredicateForAllOf(Statement<?>[] statements) {
    AtomicInteger i = new AtomicInteger(0);
    @SuppressWarnings("unchecked") Predicate<? super List<?>>[] predicates = Arrays.stream(statements)
        .map(e -> makeTrivial(transform(makeTrivial(elementAt(i.getAndIncrement()))).check((Predicate<? super Object>) e.statementPredicate())))
        .toArray(Predicate[]::new);
    return makeTrivial(allOf(predicates));
  }

  public static <T> Statement<T> statement(T value, Predicate<T> predicate) {
    return objectValue(value).then().checkWithPredicate(predicate);
  }

  @SafeVarargs
  public static <T> Statement<T> statementAllOf(T value, Predicate<? super T>... predicateArray) {
    List<Predicate<? super T>> predicates = asList(predicateArray);
    class Stmt implements Statement<T>, Evaluable.Conjunction<T>, Predicate<T> {
      @SuppressWarnings("unchecked")
      final List<Evaluable<? super T>> children = predicates.stream()
          .map(each -> each instanceof Evaluable ?
              each :
              PrintablePredicateFactory.leaf(each::toString, each))
          .map(each -> (Evaluable<? super T>) each)
          .collect(toList());

      @Override
      public T statementValue() {
        return value;
      }

      @Override
      public List<Evaluable<? super T>> children() {
        return children;
      }

      @Override
      public boolean shortcut() {
        return false;
      }

      @SuppressWarnings("unchecked")
      @Override
      public Predicate<T> statementPredicate() {
        return PrintablePredicateFactory.allOf(predicates
            .stream()
            .map(each -> (Predicate<T>) each)
            .collect(toList()));
      }

      @Override
      public boolean test(T t) {
        return statementPredicate().test(t);
      }

      @Override
      public String toString() {
        return children.stream()
            .map(Object::toString)
            .collect(joining("&&"));
      }
    }
    return new Stmt();
  }

  @SafeVarargs
  public static <T> Statement<T> statementAnyOf(T value, Predicate<? super T>... predicateArray) {
    List<Predicate<? super T>> predicates = asList(predicateArray);
    class Stmt implements Statement<T>, Evaluable.Disjunction<T>, Predicate<T> {
      @SuppressWarnings("unchecked")
      final List<Evaluable<? super T>> children = predicates.stream()
          .map(each -> each instanceof Evaluable ?
              each :
              PrintablePredicateFactory.leaf(each::toString, each))
          .map(each -> (Evaluable<? super T>) each)
          .collect(toList());

      @Override
      public T statementValue() {
        return value;
      }

      @Override
      public List<Evaluable<? super T>> children() {
        return children;
      }

      @Override
      public boolean shortcut() {
        return false;
      }

      @SuppressWarnings("unchecked")
      @Override
      public Predicate<T> statementPredicate() {
        return PrintablePredicateFactory.anyOf(predicates
            .stream()
            .map(each -> (Predicate<T>) each)
            .collect(toList()));
      }

      @Override
      public boolean test(T t) {
        return statementPredicate().test(t);
      }

      @Override
      public String toString() {
        return children.stream()
            .map(Object::toString)
            .collect(joining("||"));
      }
    }
    return new Stmt();
  }

  private static <T> Predicate<T> makeTrivial(Predicate<T> predicates) {
    return ((PrintablePredicate<T>) predicates).makeTrivial();
  }

  public static <T, R> Function<T, R> makeTrivial(Function<T, R> predicates) {
    return ((PrintableFunction<T, R>) predicates).makeTrivial();
  }

}
