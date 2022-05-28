package com.github.dakusui.pcond;

import com.github.dakusui.pcond.provider.AssertionProvider;

import java.util.function.Predicate;

public enum TestAssertions {

  ;

  public static <T> void assertThat(T value, Predicate<? super T> predicate) {
    AssertionProvider.INSTANCE.assertThat(value, predicate);
  }

  public static <T> void assumeThat(T value, Predicate<? super T> predicate) {
    AssertionProvider.INSTANCE.assumeThat(value, predicate);
  }

  public static <T> void assertThat(Statement<T> statement) {
    assertThat(statement.value(), statement.predicate());
  }

  public static <T> void assumeThat(Statement<T> statement) {
    assumeThat(statement.value(), statement.predicate());
  }

  interface Statement<T> {
    T value();

    Predicate<T> predicate();
  }
}
