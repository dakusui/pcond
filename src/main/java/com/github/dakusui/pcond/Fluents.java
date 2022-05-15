package com.github.dakusui.pcond;

import com.github.dakusui.pcond.core.fluent.Fluent;
import com.github.dakusui.pcond.core.fluent.transformers.ListTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.ObjectTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.StringTransformer;

import java.util.List;

public enum Fluents {
  ;


  public static <T> ObjectTransformer<T, T> when(T value) {
    return whenValueOf(value);
  }

  /**
   * Use the value returned from {@link this#value()} as the argument for this
   * method as a place-holder for the sake of readability.
   *
   * @param value A value place-holder.
   * @param <T>   The type of the object to be verified.
   * @return A new ObjectTransformer for type `T`.
   */
  public static <T> ObjectTransformer<T, T> whenValueOf(T value) {
    return fluent(value).object("WHEN");
  }

  public static <T> ObjectTransformer<T, T> whenInstanceOf(Class<T> value) {
    return whenValueOf(value());
  }

  @SuppressWarnings("unchecked")
  public static <E> ListTransformer<List<E>, E> whenListOf(E element) {
    return fluent((List<E>) value()).listOf("WHEN", element);
  }

  @SuppressWarnings("unchecked")
  public static <E> StringTransformer<E> whenString() {
    return fluent((E) value()).string("WHEN");
  }

  /**
   * Use this method inside "when" clause.
   *
   * Use the value returned from {@link this#value()} as the argument for this
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
  public static <T> ObjectTransformer<T, T> as(T value) {
    return asValueOf(value);
  }

  public static <T> ObjectTransformer<T, T> asValueOf(T value) {
    return fluent(value).object(null);
  }

  public static <T> ObjectTransformer<T, T> asInstanceOf(Class<T> value) {
    return asValueOf(value());
  }

  @SuppressWarnings("unchecked")
  public static <E> ListTransformer<List<E>, E> asListOf(E element) {
    return fluent((List<E>) value()).listOf(null, element);
  }

  @SuppressWarnings("unchecked")
  public static <E> StringTransformer<E> asString() {
    return fluent((E) value()).string("WHEN");
  }

  /**
   * Returns a "type place-holder".
   * A type place-holder is a value that can be cast to any class, even if it has
   * a generic type parameters.
   * Note that accessing any field or method of the returned value results in
   * `NullPointerException`.
   *
   * @param <T> A parameter type of class that the returned value represents.
   * @return A `null` value
   */
  public static <T> T value() {
    return Fluent.value();
  }

  /**
   * Explicitly cast the returned {@link Fluent} object.
   *
   * @param value A type place-holder.
   * @param <IN>  A type for which a new `Fluent` object is created.
   * @return A new `Fluent` object.
   */
  private static <IN> Fluent<IN, IN> fluent(IN value) {
    return fluent();
  }

  public static <IN, OUT> Fluent<IN, OUT> fluent() {
    return new Fluent<>();
  }
}
