package com.github.dakusui.pcond;

import com.github.dakusui.pcond.provider.AssertionProvider;

import java.util.NoSuchElementException;
import java.util.function.Predicate;

public enum TestAssertions {

  ;

  public static <T> void assertThat(T value, Predicate<? super T> predicate) {
    AssertionProvider.INSTANCE.assertThat(value, predicate);
  }

  public static <T> void assumeThat(T value, Predicate<? super T> predicate) {
    AssertionProvider.INSTANCE.assumeThat(value, predicate);
  }

  public static <T> void assertStatement(Statement<T> statement) {
    assertThat(statement.statementValue(), statement.statementPredicate());
  }

  public static <T> void assumeStatement(Statement<T> statement) {
    assumeThat(statement.statementValue(), statement.statementPredicate());
  }

  @FunctionalInterface
  public interface Statement<T> extends Predicate<T> {
    default T statementValue() {
      throw new NoSuchElementException();
    }

    default Predicate<T> statementPredicate() {
      return this;
    }
  }
}
