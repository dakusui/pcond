package com.github.dakusui.pcond;

import com.github.dakusui.pcond.core.fluent.Fluent;
import com.github.dakusui.pcond.core.fluent.transformers.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.forms.Functions.elementAt;
import static com.github.dakusui.pcond.forms.Predicates.allOf;
import static com.github.dakusui.pcond.forms.Predicates.transform;
import static java.util.stream.Collectors.toList;

/**
 * An "entry-point" class to write a "more-fluent" style tests.
 * In order to avoid a conflict in `valueOf` method, this class is implemented as
 * a conventional class, not an `enum`.
 */
public class MoreFluents {
  private MoreFluents() {
  }

  public static <T> void assertWhen(Statement<T> statement) {
    TestAssertions.assertThat(statement.statementValue(), statement.statementPredicate());
  }

  public static void assertWhen(Statement<?>... statements) {
    List<?> values = Arrays.stream(statements).map(Statement::statementValue).collect(toList());
    AtomicInteger i = new AtomicInteger(0);
    @SuppressWarnings("unchecked") Predicate<? super List<?>>[] predicates = Arrays.stream(statements)
        .map(e -> transform(elementAt(i.getAndIncrement())).check((Predicate<? super Object>) e.statementPredicate()))
        .toArray(Predicate[]::new);

    TestAssertions.assertThat(values, allOf((predicates)));
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
