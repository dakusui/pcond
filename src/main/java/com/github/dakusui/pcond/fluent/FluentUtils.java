package com.github.dakusui.pcond.fluent;

import com.github.dakusui.pcond.core.fluent.Checker;
import com.github.dakusui.pcond.core.fluent.Fluent;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.dummyFunction;
import static com.github.dakusui.pcond.internals.InternalUtils.isDummyFunction;

/**
 * Not made this class `enum` in order to provide a method whose name is `valueOf`.
 */
public class FluentUtils {
  private FluentUtils() {
  }

  /**
   * A synonym with `valueOf(value())`.
   *
   * @param <T> The type for which returned `Fluent` object is created.
   * @return Returns a `Fluent<T>` object.
   */
  public static <T> Fluent<T> fluentValue() {
    return valueOf(value());
  }

  /**
   * Use this method inside "when" clause.
   *
   * Use the value returned from `value()` as the argument for this
   * method as a place-holder for the sake of readability.
   *
   * [source]
   * ----
   * as((Map<String, Object>)value())
   * ----
   *
   * @param value A value place-holder.
   * @param <T>   The type of the object to be verified.
   * @return A new ObjectTransformer for type `T`.
   */
  public static <T> Fluent<T> valueOf(@SuppressWarnings("unused") T value) {
    return fluent();
  }

  public static <T> Fluent<T> valueOfClass(@SuppressWarnings("unused") Class<T> klass) {
    return valueOf(value());
  }

  /**
   * Returns a "type place-holder".
   * A type place-holder is a value that can be cast to any class, even if it has
   * a generic type parameters.
   * Note that accessing any field or method of the returned value results in
   * `NullPointerException`.
   *
   * @param <T> A parameter type of class that the returned value represents.
   * @return A `null` value.
   */
  public static <T> T value() {
    return Fluent.value();
  }

  public static <IN> Fluent<IN> fluent() {
    return fluent(null);
  }

  public static <IN> Fluent<IN> fluent(String transformerName) {
    return new Fluent<>(transformerName);
  }

  public static <T> Predicate<T> toPredicateIfChecker(Predicate<T> each) {
    if (each instanceof Checker)
      return ((Checker<?, T, ?>) each).toPredicate();
    return each;
  }

  @SuppressWarnings("unchecked")
  public static <I, M, O> Function<I, O> chainFunctions(Function<I, ? extends M> func, Function<? super M, O> after) {
    if (isDummyFunction(func) && isDummyFunction(after))
      return dummyFunction();
    if (isDummyFunction(func))
      return (isDummyFunction(after)) ? dummyFunction() : (Function<I, O>) after;
    else
      return isDummyFunction(after) ? (Function<I, O>) func : func.andThen(after);
  }
}
