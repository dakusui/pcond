package com.github.dakusui.pcond;

import com.github.dakusui.pcond.core.fluent.Fluent;
import com.github.dakusui.pcond.core.fluent.transformers.ListTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.ObjectTransformer;

import java.util.List;

import static com.github.dakusui.pcond.forms.Printables.function;
import static com.github.dakusui.pcond.forms.Printables.predicate;

public enum Fluents {
  ;


  public static <IN> Fluent<IN, IN> when(IN value) {
    return new Fluent<>();
  }

  public static <IN> Fluent<IN, IN> when() {
    return new Fluent<>();
  }

  public static <IN, OUT> Fluent<IN, OUT> fluent() {
    return new Fluent<>();
  }

  /**
   * Returns a value that can be cast to any class, even if it has a generic type parameters.
   * Note that accessing any field or method of the returned value results in
   * `NullPointerException`.
   *
   * @param <T> A parameter type of class that the returned value represents.
   * @return A `null` value
   */
  public static <T> T value() {
    return null;
  }

  public static <T> ObjectTransformer<T, T> whenObjectOf(T value) {
    return when(value).object();
  }

  @SuppressWarnings("unchecked")
  public static <E> ListTransformer<List<E>, E> whenListOf(E element) {
    return when((List<E>)value()).listOf(element);
  }


}
