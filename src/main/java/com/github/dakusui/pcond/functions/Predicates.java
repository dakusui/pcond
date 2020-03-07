package com.github.dakusui.pcond.functions;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.util.Objects.requireNonNull;

public enum Predicates {
  ;

  private static final Predicate<?>                                        ALWAYS_TRUE                      = Printable.predicate("alwaysTrue", t -> true);
  private static final Predicate<Boolean>                                  IS_TRUE                          = Printable.predicate("isTrue", (Boolean v) -> v);
  private static final Predicate<Boolean>                                  IS_FALSE                         = Printable.predicate("isFalse", (Boolean v) -> !v);
  private static final Predicate<?>                                        IS_NULL                          = Printable.predicate("isNull", Objects::isNull);
  private static final Predicate<?>                                        IS_NOT_NULL                      = Printable.predicate("isNotNull", Objects::nonNull);
  private static final Predicate<String>                                   IS_EMPTY_STRING                  = Printable.predicate("isEmpty", String::isEmpty);
  private static final Predicate<String>                                   IS_EMPTY_OR_NULL_STRING          = Printable.predicate("isEmptyOrNullString", s -> Objects.isNull(s) || isEmptyString().test(s)
  );
  private static final Predicate<Object[]>                                 IS_EMPTY_ARRAY                   = Printable.predicate("isEmptyArray", objects -> objects.length == 0);
  private static final Predicate<Collection<?>>                            IS_EMPTY_COLLECTION              = Printable.predicate("isEmpty", Collection::isEmpty);
  private static final PrintablePredicate.Factory<Object, Object>          EQUAL_TO_FACTORY                 = Printable.predicateFactory(
      (arg) -> String.format("equalTo[%s]", formatObject(arg)),
      arg -> v -> Objects.equals(v, arg));
  private static final PrintablePredicate.Factory<Collection<?>, Object>   CONTAINS_FACTORY                 = Printable.predicateFactory(
      arg -> String.format("contains[%s]", formatObject(arg)),
      arg -> (Collection<?> c) -> c.contains(arg));
  private static final PrintablePredicate.Factory<Object, Object>          OBJECT_IS_SAME_AS_FACTORY        = Printable.predicateFactory(
      arg -> String.format("==[%s]", formatObject(arg)),
      arg -> v -> v == arg);
  @SuppressWarnings({ "SimplifiableConditionalExpression" })
  private static final PrintablePredicate.Factory<Object, Class<?>>        OBJECT_IS_INSTANCE_OF_FACTORY    = Printable.predicateFactory(
      (arg) -> String.format("isInstanceOf[%s]", arg.getCanonicalName()),
      arg -> v -> v == null ?
          false :
          arg.isAssignableFrom(v.getClass()));
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<Object, Comparable<?>>   GT_FACTORY                       = Printable.predicateFactory(
      (arg) -> String.format(">[%s]", formatObject(arg)),
      arg -> v -> ((Comparable) v).compareTo(arg) > 0);
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<Object, Comparable<?>>   GE_FACTORY                       = Printable.predicateFactory(
      (arg) -> String.format(">=[%s]", formatObject(arg)),
      arg -> v -> ((Comparable) v).compareTo(arg) >= 0);
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<Object, Comparable<?>>   LE_FACTORY                       = Printable.predicateFactory(
      (arg) -> String.format("<=[%s]", formatObject(arg)),
      arg -> v -> ((Comparable) v).compareTo(arg) <= 0);
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<Object, Comparable<?>>   LT_FACTORY                       = Printable.predicateFactory(
      (arg) -> String.format("<[%s]", formatObject(arg)),
      arg -> v -> ((Comparable) v).compareTo(arg) < 0);
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<Object, Comparable<?>>   EQ_FACTORY                       = Printable.predicateFactory(
      (arg) -> String.format("~[%s]", formatObject(arg)),
      arg -> v -> ((Comparable) v).compareTo(arg) == 0);
  private static final PrintablePredicate.Factory<String, String>          STRING_MATCHES_REGEX_FACTORY     = Printable.predicateFactory(
      (arg) -> String.format("matchesRegex[%s]", formatObject(arg)),
      arg -> (String s) -> s.matches(arg));
  private static final PrintablePredicate.Factory<String, String>          STRING_CONTAINS_FACTORY          = Printable.predicateFactory(
      (arg) -> String.format("containsString[%s]", formatObject(arg)),
      arg -> (String s) -> s.contains(arg));
  private static final PrintablePredicate.Factory<String, String>          STRING_STARTS_WITH_FACTORY       = Printable.predicateFactory(
      (arg) -> String.format("startsWith[%s]", formatObject(arg)),
      (arg) -> (String s) -> s.startsWith(arg));
  private static final PrintablePredicate.Factory<String, String>          STRING_ENDS_WITH_FACTORY         = Printable.predicateFactory(
      (arg) -> String.format("endsWith[%s]", formatObject(arg)),
      arg -> (String s) -> s.endsWith(arg));
  private static final PrintablePredicate.Factory<String, String>          STRING_EQUALS_IGNORECASE_FACTORY = Printable.predicateFactory(
      (arg) -> String.format("equalsIgnoreCase[%s]", formatObject(arg)),
      arg -> (String s) -> s.equalsIgnoreCase(arg));
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<Stream<?>, Predicate<?>> STREAM_ALL_MATCH_FACTORY         = Printable.predicateFactory(
      (arg) -> String.format("allMatch[%s]", requireNonNull(arg)),
      arg -> (Stream<?> stream) -> stream.allMatch((Predicate) arg)
  );
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<Stream<?>, Predicate<?>> STREAM_NONE_MATCH_FACTORY        = Printable.predicateFactory(
      (arg) -> String.format("noneMatch[%s]", requireNonNull(arg)),
      arg -> (Stream<?> stream) -> stream.noneMatch((Predicate) arg)
  );
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<Stream<?>, Predicate<?>> STREAM_ANY_MATCH_FACTORY         = Printable.predicateFactory(
      (arg) -> String.format("anyMatch[%s]", requireNonNull(arg)),
      arg -> (Stream<?> stream) -> stream.anyMatch((Predicate) arg)
  );

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

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <T> Predicate<T> equalTo(T value) {
    return Predicate.class.cast(EQUAL_TO_FACTORY.create(value));
  }

  public static <T> Predicate<? super T> isSameAs(T value) {
    return OBJECT_IS_SAME_AS_FACTORY.create(value);
  }

  public static <T> Predicate<? super T> isInstanceOf(Class<?> value) {
    requireNonNull(value);
    return OBJECT_IS_INSTANCE_OF_FACTORY.create(value);
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> gt(T value) {
    return GT_FACTORY.create(value);
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> ge(T value) {
    return GE_FACTORY.create(value);
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> lt(T value) {
    return LT_FACTORY.create(value);
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> le(T value) {
    return LE_FACTORY.create(value);
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> eq(T value) {
    return EQ_FACTORY.create(value);
  }

  public static Predicate<String> matchesRegex(String regex) {
    requireNonNull(regex);
    return STRING_MATCHES_REGEX_FACTORY.create(regex);
  }

  public static Predicate<String> containsString(String string) {
    requireNonNull(string);
    return STRING_CONTAINS_FACTORY.create(string);
  }

  public static Predicate<String> startsWith(String string) {
    requireNonNull(string);
    return STRING_STARTS_WITH_FACTORY.create(string);
  }

  public static Predicate<String> endsWith(String string) {
    requireNonNull(string);
    return STRING_ENDS_WITH_FACTORY.create(string);
  }

  public static Predicate<String> equalsIgnoreCase(String string) {
    requireNonNull(string);
    return STRING_EQUALS_IGNORECASE_FACTORY.create(string);
  }

  public static Predicate<String> isEmptyString() {
    return IS_EMPTY_STRING;
  }

  public static Predicate<String> isEmptyOrNullString() {
    return IS_EMPTY_OR_NULL_STRING;
  }

  public static <E> Predicate<? super Collection<E>> contains(Object entry) {
    requireNonNull(entry);
    return CONTAINS_FACTORY.create(entry);
  }

  public static Predicate<Object[]> isEmptyArray() {
    return IS_EMPTY_ARRAY;
  }

  public static Predicate<? super Collection<?>> isEmpty() {
    return IS_EMPTY_COLLECTION;
  }

  public static <E> Predicate<? super Stream<? extends E>> allMatch(Predicate<E> predicate) {
    return STREAM_ALL_MATCH_FACTORY.create(predicate);
  }

  public static <E> Predicate<? super Stream<? extends E>> noneMatch(Predicate<E> predicate) {
    return STREAM_NONE_MATCH_FACTORY.create(predicate);
  }

  public static <E> Predicate<? super Stream<? extends E>> anyMatch(Predicate<E> predicate) {
    return STREAM_ANY_MATCH_FACTORY.create(predicate);
  }
}
