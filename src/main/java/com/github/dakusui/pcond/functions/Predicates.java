package com.github.dakusui.pcond.functions;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.util.Objects.requireNonNull;

public enum Predicates {
  ;

  public static final Predicate<?>             ALWAYS_TRUE             = Printable.predicate("alwaysTrue", t -> true);
  public static final Predicate<Boolean>       IS_TRUE                 = Printable.predicate("isTrue", (Boolean v) -> v);
  public static final Predicate<Boolean>       IS_FALSE                = Printable.predicate("isFalse", (Boolean v) -> !v);
  public static final Predicate<?>             IS_NULL                 = Printable.predicate("isNull", Objects::isNull);
  public static final Predicate<?>             IS_NOT_NULL             = Printable.predicate("isNotNull", Objects::nonNull);
  public static final Predicate<String>        IS_EMPTY_STRING         = Printable.predicate("isEmpty", String::isEmpty);
  public static final Predicate<String>        IS_EMPTY_OR_NULL_STRING = Printable.predicate("isEmptyOrNullString", s -> Objects.isNull(s) || isEmptyString().test(s)
  );
  public static final Predicate<Object[]>      IS_EMPTY_ARRAY          = Printable.predicate("isEmptyArray", objects -> objects.length == 0);
  public static final Predicate<Collection<?>> IS_EMPTY_COLLECTION     = Printable.predicate("isEmpty", Collection::isEmpty);

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> alwaysTrue() {
    return (Predicate<T>) ALWAYS_TRUE;
  }

  public static Predicate<? super Boolean> isTrue() {
    return IS_TRUE;
  }

  public static Predicate<Boolean> isFalse() {
    return IS_FALSE;
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> isNull() {
    return (Predicate<T>) IS_NULL;
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> isNotNull() {
    return (Predicate<T>) IS_NOT_NULL;
  }

  public static <T> Predicate<T> equalTo(T value) {
    return Printable.predicate(
        () -> String.format("equalTo[%s]", formatObject(value)),
        v -> Objects.equals(v, value)
    );
  }

  public static <T> Predicate<? super T> isSameAs(T value) {
    return Printable.predicate(
        () -> String.format("==[%s]", formatObject(value)),
        v -> v == value
    );
  }

  public static <T> Predicate<? super T> isInstanceOf(Class<?> value) {
    requireNonNull(value);
    //noinspection SimplifiableConditionalExpression
    return Printable.predicate(
        () -> String.format("isInstanceOf[%s]", value.getCanonicalName()),
        v -> v == null ?
            false :
            value.isAssignableFrom(v.getClass())
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> gt(T value) {
    return Printable.predicate(
        () -> String.format(">[%s]", formatObject(value)),
        v -> v.compareTo(value) > 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> ge(T value) {
    return Printable.predicate(
        () -> String.format(">=[%s]", formatObject(value)),
        v -> v.compareTo(value) >= 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> lt(T value) {
    return Printable.predicate(
        () -> String.format("<[%s]", formatObject(value)),
        v -> v.compareTo(value) < 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> le(T value) {
    return Printable.predicate(
        () -> String.format("<=[%s]", formatObject(value)),
        v -> v.compareTo(value) <= 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> eq(T value) {
    return Printable.predicate(
        () -> String.format("~[%s]", formatObject(value)),
        v -> v.compareTo(value) == 0
    );
  }

  public static Predicate<String> matchesRegex(String regex) {
    requireNonNull(regex);
    return Printable.predicate(
        () -> String.format("matchesRegex[%s]", formatObject(regex)),
        s -> s.matches(regex)
    );
  }

  public static Predicate<String> containsString(String string) {
    requireNonNull(string);
    return Printable.predicate(
        () -> String.format("containsString[%s]", formatObject(string)),
        s -> s.contains(string)
    );
  }

  public static Predicate<String> startsWith(String string) {
    requireNonNull(string);
    return Printable.predicate(
        () -> String.format("startsWith[%s]", formatObject(string)),
        s -> s.startsWith(string)
    );
  }

  public static Predicate<String> endsWith(String string) {
    requireNonNull(string);
    return Printable.predicate(
        () -> String.format("endsWith[%s]", formatObject(string)),
        s -> s.endsWith(string)
    );
  }

  public static Predicate<String> equalsIgnoreCase(String string) {
    requireNonNull(string);
    return Printable.predicate(
        () -> String.format("equalsIgnoreCase[%s]", formatObject(string)),
        s -> s.equalsIgnoreCase(string)
    );
  }

  public static Predicate<String> isEmptyString() {
    return IS_EMPTY_STRING;
  }

  public static Predicate<String> isEmptyOrNullString() {
    return IS_EMPTY_OR_NULL_STRING;
  }

  public static <E> Predicate<? super Collection<E>> contains(Object entry) {
    requireNonNull(entry);
    //noinspection SuspiciousMethodCalls
    return Printable.predicate(
        () -> String.format("contains[%s]", formatObject(entry)),
        c -> c.contains(entry)
    );
  }

  public static Predicate<Object[]> isEmptyArray() {
    return IS_EMPTY_ARRAY;
  }

  public static Predicate<? super Collection<?>> isEmpty() {
    return IS_EMPTY_COLLECTION;
  }

  public static <E> Predicate<? super Stream<? extends E>> allMatch(Predicate<E> predicate) {
    return Printable.predicate(
        () -> String.format("allMatch[%s]", requireNonNull(predicate)),
        stream -> stream.allMatch(predicate)
    );
  }

  public static <E> Predicate<? super Stream<? extends E>> noneMatch(Predicate<E> predicate) {
    return Printable.predicate(
        () -> String.format("noneMatch[%s]", requireNonNull(predicate)),
        stream -> stream.noneMatch(predicate)
    );
  }

  public static <E> Predicate<? super Stream<? extends E>> anyMatch(Predicate<E> predicate) {
    return Printable.predicate(
        () -> String.format("anyMatch[%s]", requireNonNull(predicate)),
        stream -> stream.anyMatch(predicate)
    );
  }
}
