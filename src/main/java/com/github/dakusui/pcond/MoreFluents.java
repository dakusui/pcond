package com.github.dakusui.pcond;

import com.github.dakusui.pcond.core.fluent.Fluent;
import com.github.dakusui.pcond.core.fluent.transformers.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class MoreFluents {
  private MoreFluents() {
  }

  public static <T> void assertWhen(Statement<T> statement) {
    TestAssertions.assertThat(statement.statementValue(), statement.statementPredicate());
  }

  public static <T> void assumeWhen(Statement<T> statement) {
    TestAssertions.assumeThat(statement.statementValue(), statement.statementPredicate());
  }

  public static StringTransformer<String> valueOf(String value) {
    return new Fluent<>("WHEN", value).asString();
  }

  public static IntegerTransformer<Integer> valueOf(int value) {
    return new Fluent<>("WHEN", value).asInteger();
  }

  public static BooleanTransformer<Boolean> valueOf(boolean value) {
    return new Fluent<>("WHEN", value).asBoolean();
  }

  public static <T> ObjectTransformer<T, T> valueOf(T value) {
    return new Fluent<>("WHEN", value).asObject();
  }

  public static <E> ListTransformer<List<E>, E> valueOf(List<E> value) {
    return new Fluent<>("WHEN", value).asListOf(Fluents.$());
  }

  @SafeVarargs
  public static <E> ListTransformer<List<E>, E> valuesOf(E... vargs) {
    return new Fluent<>("WHEN", asList(vargs))
        .asListOf(Fluents.$());
  }

  public static <E> StreamTransformer<Stream<E>, E> valueOf(Stream<E> value) {
    return new Fluent<>("WHEN", value).asStreamOf(Fluents.$());
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
