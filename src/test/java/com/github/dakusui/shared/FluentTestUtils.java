package com.github.dakusui.shared;

import com.github.dakusui.pcond.core.fluent.Fluent;

import java.util.List;

import static com.github.dakusui.pcond.fluent.FluentUtils.fluent;
import static com.github.dakusui.pcond.fluent.FluentUtils.value;
import static java.util.Arrays.asList;

public enum FluentTestUtils {
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
  public static <T> Fluent<T> whenValueOf(@SuppressWarnings("unused") T value) {
    return fluent("WHEN");
  }

  public static <T> Fluent<T> whenValueOfClass(@SuppressWarnings("unused") Class<T> klass) {
    return whenValueOf(value());
  }

  public static List<?> list(Object... args) {
    return asList(args);
  }

}
