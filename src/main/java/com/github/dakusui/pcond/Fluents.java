package com.github.dakusui.pcond;

import com.github.dakusui.pcond.core.fluent.Fluent;
import com.github.dakusui.pcond.core.fluent.transformers.ListTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.ObjectTransformer;

import java.util.List;

public enum Fluents {
  ;


  public static <T> ObjectTransformer<T, T> when(T value) {
    return whenValueOf(value);
  }

  public static <T> ObjectTransformer<T, T> whenValueOf(T value) {
    return fluent(value).object();
  }

  public static <T> ObjectTransformer<T, T> whenInstanceOf(Class<T> value) {
    return whenValueOf(value());
  }

  @SuppressWarnings("unchecked")
  public static <E> ListTransformer<List<E>, E> whenListOf(E element) {
    return fluent((List<E>)value()).listOf(element);
  }

  /**
   * Returns a type place-holder.
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
   * @param value  A type place-holder.
   * @return A new `Fluent` object.
   * @param <IN> A type for which a new `Fluent` object is created.
   */
  private static <IN> Fluent<IN, IN> fluent(IN value) {
    return fluent();
  }

  public static <IN, OUT> Fluent<IN, OUT> fluent() {
    return new Fluent<>();
  }
}
