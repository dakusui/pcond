package com.github.dakusui.pcond.forms;

import com.github.dakusui.pcond.core.printable.Matcher;

import static com.github.dakusui.pcond.forms.Printables.function;
import static com.github.dakusui.pcond.forms.Printables.predicate;

public enum Matchers {
  ;


  public static <IN> Matcher.Builder.Builder0.Builder1<IN, IN> when(Class<IN> klass) {
    return new Matcher.Builder.Builder0.Builder1<>();
  }

  public static <IN> Matcher.Builder.Builder0.Builder1<IN, IN> when() {
    return new Matcher.Builder.Builder0.Builder1<>();
  }

  public static <IN, OUT> Matcher.Builder.Builder0.Builder1<IN, OUT> matcher() {
    return new Matcher.Builder.Builder0.Builder1<>();
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
}
