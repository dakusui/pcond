package com.github.dakusui.pcond;

import com.github.dakusui.pcond.core.fluent.Fluent;

import java.util.List;

import static java.util.Arrays.asList;

public enum Fluents {
  ;

  public static <T> Fluent<T> when() {
    return whenValueOf(value());
  }

    /**
   * Use the value returned from `value()` as the argument for this
   * method as a place-holder for the sake of readability.
   *
   * @param value A value place-holder.
   * @param <T>   The type of the object to be verified.
   * @return A new ObjectTransformer for type `T`.
   */
  public static <T> Fluent<T> whenValueOf(T value) {
    return fluent("WHEN");
  }

  public static <T> Fluent<T> whenValueOfClass(Class<T> klass) {
    return whenValueOf(value());
  }

  public static <T> Fluent<T> $() {
    return $(value());
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
  public static <T> Fluent<T> $(T value) {
    return fluent();
  }

  public static <T> Fluent<T> $valueOfClass(Class<T> klass) {
    return $(value());
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

  public static <IN> Fluent<IN> fluent() {
    return fluent(null);
  }

  public static <IN> Fluent<IN> fluent(String transformerName) {
    return new Fluent<>(transformerName);
  }

  public static List<?> list(Object... args) {
    return asList(args);
  }
}
