package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.internals.TransformingPredicate;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.util.Objects.requireNonNull;

public enum Predicates {
  ;

  private static final Predicate<?>                                        ALWAYS_TRUE                      = Printables.predicate("alwaysTrue", t -> true);
  private static final Predicate<Boolean>                                  IS_TRUE                          = Printables.predicate("isTrue", (Boolean v) -> v);
  private static final Predicate<Boolean>                                  IS_FALSE                         = Printables.predicate("isFalse", (Boolean v) -> !v);
  private static final Predicate<?>                                        IS_NULL                          = Printables.predicate("isNull", Objects::isNull);
  private static final Predicate<?>                                        IS_NOT_NULL                      = Printables.predicate("isNotNull", Objects::nonNull);
  private static final Predicate<String>                                   IS_EMPTY_STRING                  = Printables.predicate("isEmpty", String::isEmpty);
  private static final Predicate<String>                                   IS_EMPTY_OR_NULL_STRING          = Printables.predicate("isEmptyOrNullString", s -> Objects.isNull(s) || isEmptyString().test(s)
  );
  private static final Predicate<Object[]>                                 IS_EMPTY_ARRAY                   = Printables.predicate("isEmptyArray", objects -> objects.length == 0);
  private static final Predicate<Collection<?>>                            IS_EMPTY_COLLECTION              = Printables.predicate("isEmpty", Collection::isEmpty);
  private static final PrintablePredicate.Factory<Object, Object>          EQUAL_TO_FACTORY                 = Printables.predicateFactory(
      (arg) -> String.format("equalTo[%s]", formatObject(arg)),
      arg -> v -> Objects.equals(v, arg));
  private static final PrintablePredicate.Factory<Collection<?>, Object>   CONTAINS_FACTORY                 = Printables.predicateFactory(
      arg -> String.format("contains[%s]", formatObject(arg)),
      arg -> (Collection<?> c) -> c.contains(arg));
  private static final PrintablePredicate.Factory<Object, Object>          OBJECT_IS_SAME_AS_FACTORY        = Printables.predicateFactory(
      arg -> String.format("==[%s]", formatObject(arg)),
      arg -> v -> v == arg);
  @SuppressWarnings({ "SimplifiableConditionalExpression" })
  private static final PrintablePredicate.Factory<Object, Class<?>>        OBJECT_IS_INSTANCE_OF_FACTORY    = Printables.predicateFactory(
      (arg) -> String.format("isInstanceOf[%s]", arg.getCanonicalName()),
      arg -> v -> v == null ?
          false :
          arg.isAssignableFrom(v.getClass()));
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<?, Comparable<?>>        GT_FACTORY                       = Printables.predicateFactory(
      (arg) -> String.format(">[%s]", formatObject(arg)),
      arg -> v -> ((Comparable) v).compareTo(arg) > 0);
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<?, Comparable<?>>        GE_FACTORY                       = Printables.predicateFactory(
      (arg) -> String.format(">=[%s]", formatObject(arg)),
      arg -> v -> ((Comparable) v).compareTo(arg) >= 0);
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<?, Comparable<?>>        LE_FACTORY                       = Printables.predicateFactory(
      (arg) -> String.format("<=[%s]", formatObject(arg)),
      arg -> v -> ((Comparable) v).compareTo(arg) <= 0);
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<?, Comparable<?>>        LT_FACTORY                       = Printables.predicateFactory(
      (arg) -> String.format("<[%s]", formatObject(arg)),
      arg -> v -> ((Comparable) v).compareTo(arg) < 0);
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<?, Comparable<?>>        EQ_FACTORY                       = Printables.predicateFactory(
      (arg) -> String.format("~[%s]", formatObject(arg)),
      arg -> v -> ((Comparable) v).compareTo(arg) == 0);
  private static final PrintablePredicate.Factory<String, String>          STRING_MATCHES_REGEX_FACTORY     = Printables.predicateFactory(
      (arg) -> String.format("matchesRegex[%s]", formatObject(arg)),
      arg -> (String s) -> s.matches(arg));
  private static final PrintablePredicate.Factory<String, String>          STRING_CONTAINS_FACTORY          = Printables.predicateFactory(
      (arg) -> String.format("containsString[%s]", formatObject(arg)),
      arg -> (String s) -> s.contains(arg));
  private static final PrintablePredicate.Factory<String, String>          STRING_STARTS_WITH_FACTORY       = Printables.predicateFactory(
      (arg) -> String.format("startsWith[%s]", formatObject(arg)),
      (arg) -> (String s) -> s.startsWith(arg));
  private static final PrintablePredicate.Factory<String, String>          STRING_ENDS_WITH_FACTORY         = Printables.predicateFactory(
      (arg) -> String.format("endsWith[%s]", formatObject(arg)),
      arg -> (String s) -> s.endsWith(arg));
  private static final PrintablePredicate.Factory<String, String>          STRING_EQUALS_IGNORECASE_FACTORY = Printables.predicateFactory(
      (arg) -> String.format("equalsIgnoreCase[%s]", formatObject(arg)),
      arg -> (String s) -> s.equalsIgnoreCase(arg));
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<Stream<?>, Predicate<?>> STREAM_ALL_MATCH_FACTORY         = Printables.predicateFactory(
      (arg) -> String.format("allMatch[%s]", requireNonNull(arg)),
      arg -> (Stream<?> stream) -> stream.allMatch((Predicate) arg)
  );
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<Stream<?>, Predicate<?>> STREAM_NONE_MATCH_FACTORY        = Printables.predicateFactory(
      (arg) -> String.format("noneMatch[%s]", requireNonNull(arg)),
      arg -> (Stream<?> stream) -> stream.noneMatch((Predicate) arg)
  );
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final PrintablePredicate.Factory<Stream<?>, Predicate<?>> STREAM_ANY_MATCH_FACTORY         = Printables.predicateFactory(
      (arg) -> String.format("anyMatch[%s]", requireNonNull(arg)),
      arg -> (Stream<?> stream) -> stream.anyMatch((Predicate) arg)
  );

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> alwaysTrue() {
    return (Predicate<T>) ALWAYS_TRUE;
  }

  public static Predicate<Boolean> isTrue() {
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

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> isSameAs(T value) {
    return (Predicate<T>) OBJECT_IS_SAME_AS_FACTORY.create(value);
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> isInstanceOf(Class<?> value) {
    requireNonNull(value);
    return (Predicate<T>) OBJECT_IS_INSTANCE_OF_FACTORY.create(value);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> Predicate<T> gt(T value) {
    return (Predicate<T>) GT_FACTORY.create(value);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> Predicate<T> ge(T value) {
    return (Predicate<T>) GE_FACTORY.create(value);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> Predicate<T> lt(T value) {
    return (Predicate<T>) LT_FACTORY.create(value);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> Predicate<T> le(T value) {
    return (Predicate<T>) LE_FACTORY.create(value);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> Predicate<T> eq(T value) {
    return (Predicate<T>) EQ_FACTORY.create(value);
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

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <E> Predicate<Collection<E>> contains(Object entry) {
    requireNonNull(entry);
    return (Predicate) CONTAINS_FACTORY.create(entry);
  }

  public static Predicate<Object[]> isEmptyArray() {
    return IS_EMPTY_ARRAY;
  }

  public static Predicate<Collection<?>> isEmpty() {
    return IS_EMPTY_COLLECTION;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <E> Predicate<Stream<? extends E>> allMatch(Predicate<E> predicate) {
    return (Predicate) STREAM_ALL_MATCH_FACTORY.create(predicate);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <E> Predicate<Stream<? extends E>> noneMatch(Predicate<E> predicate) {
    return (Predicate) STREAM_NONE_MATCH_FACTORY.create(predicate);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <E> Predicate<Stream<? extends E>> anyMatch(Predicate<E> predicate) {
    return (Predicate) STREAM_ANY_MATCH_FACTORY.create(predicate);
  }

  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <T> Predicate<T> and(Predicate<? super T> car, Predicate<? super T>... cdr) {
    Predicate<T> ret = (Predicate<T>) car;
    for (Predicate<? super T> predicate : cdr)
      ret = ret.and(predicate);
    return ret;
  }

  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <T> Predicate<T> or(Predicate<? super T> car, Predicate<? super T>... cdr) {
    requireNonNull(car);
    Predicate<T> ret = (Predicate<T>) car;
    for (Predicate<? super T> predicate : cdr)
      ret = ret.or(predicate);
    return ret;
  }

  public static <T> Predicate<T> not(Predicate<T> cond) {
    return cond.negate();
  }

  public static <O, P> TransformingPredicate.Factory<P, O> when(String funcName, Function<? super O, ? extends P> func) {
    return when(Printables.function(funcName, func));
  }

  public static <O, P> TransformingPredicate.Factory<P, O> when(Function<? super O, ? extends P> function) {
    return cond -> new TransformingPredicate<>(cond, function);
  }
}
