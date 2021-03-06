package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory.Leaf;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory.ParameterizedLeafFactory;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.core.refl.Parameter;
import com.github.dakusui.pcond.internals.InternalChecks;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.refl.ReflUtils.invokeMethod;
import static com.github.dakusui.pcond.functions.Printables.function;
import static com.github.dakusui.pcond.functions.Printables.predicate;
import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

public enum Predicates {
  ;

  public static <T> Predicate<T> alwaysTrue() {
    return Leaf.ALWAYS_TRUE.instance();
  }

  public static Predicate<Boolean> isTrue() {
    return Leaf.IS_TRUE.instance();
  }

  public static Predicate<Boolean> isFalse() {
    return Leaf.IS_FALSE.instance();
  }

  public static <T> Predicate<T> isNull() {
    return Leaf.IS_NULL.instance();
  }

  public static <T> Predicate<T> isNotNull() {
    return Leaf.IS_NOT_NULL.instance();
  }

  public static <T> Predicate<T> isEqualTo(T value) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.IS_EQUAL_TO, singletonList(value));
  }

  public static <T> Predicate<T> isSameReferenceAs(T value) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.OBJECT_IS_SAME_AS, singletonList(value));
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <T> Function<Class<?>, Predicate<T>> isInstanceOf() {
    return Function.class.cast(Def.IS_INSTANCE_OF$2);
  }

  public static <T> Predicate<T> isInstanceOf(Class<?> value) {
    return applyOnceExpectingPredicate(requireNonNull(value), isInstanceOf());
  }

  private static <T, R> Predicate<R> applyOnceExpectingPredicate(T value, Function<T, Predicate<R>> p) {
    return predicate(() -> format("%s[%s]", p, formatObject(value)), p.apply(value));
  }

  public static <T extends Comparable<? super T>> Predicate<T> gt(T value) {
    return greaterThan(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> greaterThan(T value) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.GREATER_THAN, singletonList(value));
  }

  public static <T extends Comparable<? super T>> Predicate<T> ge(T value) {
    return greaterThanOrEqualTo(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> greaterThanOrEqualTo(T value) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.GREATER_THAN_OR_EQUAL_TO, singletonList(value));
  }

  public static <T extends Comparable<? super T>> Predicate<T> lt(T value) {
    return lessThan(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> lessThan(T value) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.LESS_THAN, singletonList(value));
  }

  public static <T extends Comparable<? super T>> Predicate<T> le(T value) {
    return lessThanOrEqualTo(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> lessThanOrEqualTo(T value) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.LESS_THAN_OR_EQUAL_TO, singletonList(value));
  }

  public static <T extends Comparable<? super T>> Predicate<T> eq(T value) {
    return equalTo(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> equalTo(T value) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.EQUAL_TO, singletonList(value));
  }

  public static Predicate<String> matchesRegex(String regex) {
    requireNonNull(regex);
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.MATCHES_REGEX, singletonList(regex));
  }

  public static Predicate<String> containsString(String string) {
    requireNonNull(string);
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.CONTAINS_STRING, singletonList(string));
  }

  public static Predicate<String> startsWith(String string) {
    requireNonNull(string);
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.STARTS_WITH, singletonList(string));
  }

  public static Predicate<String> endsWith(String string) {
    requireNonNull(string);
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.ENDS_WITH, singletonList(string));
  }

  public static Predicate<String> equalsIgnoreCase(String string) {
    requireNonNull(string);
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.EQUALS_IGNORE_CASE, singletonList(string));
  }

  public static Predicate<String> isEmptyString() {
    return Leaf.IS_EMPTY_STRING.instance();
  }

  public static Predicate<String> isNullOrEmptyString() {
    return Leaf.IS_NULL_OR_EMPTY_STRING.instance();
  }

  public static <E> Predicate<Collection<E>> contains(Object entry) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.CONTAINS, singletonList(entry));
  }

  public static Predicate<Object[]> isEmptyArray() {
    return Leaf.IS_EMPTY_ARRAY.instance();
  }

  public static Predicate<? super Collection<?>> isEmpty() {
    return Leaf.IS_EMPTY_COLLECTION.instance();
  }

  public static <E> Predicate<Stream<? extends E>> allMatch(Predicate<E> predicate) {
    requireNonNull(predicate);
    return PrintablePredicateFactory.allMatch(predicate);
  }

  public static <E> Predicate<Stream<? extends E>> noneMatch(Predicate<E> predicate) {
    requireNonNull(predicate);
    return PrintablePredicateFactory.noneMatch(predicate);
  }

  public static <E> Predicate<Stream<? extends E>> anyMatch(Predicate<E> predicate) {
    requireNonNull(predicate);
    return PrintablePredicateFactory.anyMatch(predicate);
  }

  @SafeVarargs
  public static <T> Predicate<T> and(Predicate<? super T>... predicates) {
    return PrintablePredicateFactory.and(asList(predicates));
  }

  @SafeVarargs
  public static <T> Predicate<T> or(Predicate<? super T>... predicates) {
    return PrintablePredicateFactory.or(asList(predicates));
  }

  public static <T> Predicate<T> not(Predicate<T> cond) {
    return cond.negate();
  }

  public static <O, P> PrintablePredicateFactory.TransformingPredicate.Factory<P, O> transform(String funcName, Function<? super O, ? extends P> func) {
    return transform(function(funcName, func));
  }

  @SuppressWarnings("unchecked")
  public static <O, P> PrintablePredicateFactory.TransformingPredicate.Factory<P, O> transform(Function<? super O, ? extends P> function) {
    return PrintablePredicateFactory.transform((Function<O, P>) function);
  }

  /**
   * // @formatter:off
   * Returns a {@link Predicate} created from a method specified by a {@code methodQuery}.
   * If the {@code methodQuery} matches none or more than one methods, a {@code RuntimeException} will be thrown.
   *
   * The suffix {@code p} stands for "predicate" following the custom in LISP culture
   * and it is necessary to avoid collision with {@link Functions#call( MethodQuery )} method.
   *
   * // @formatter:on
   * @param methodQuery A query object that specifies a method to be invoked by the returned predicate.
   * @param <T>         the type of the input to the returned predicate
   * @return Created predicate.
   * @see Functions#classMethod(Class, String, Object[])
   * @see Functions#instanceMethod(Object, String, Object[])
   */
  public static <T> Predicate<T> callp(MethodQuery methodQuery) {
    return predicate(
        methodQuery.describe(),
        t -> InternalChecks.ensureValue(
            invokeMethod(methodQuery.bindActualArguments((o) -> o instanceof Parameter, o -> t)),
            v -> v instanceof Boolean,
            v -> format("Method matched with '%s' must return a boolean value but it gave: '%s'.", methodQuery.describe(), v)));
  }

  enum Def {
    ;

    public static final Function<Class<?>, Predicate<?>> IS_INSTANCE_OF$2 = function(() -> "isInstanceOf", (Class<?> c) -> c::isInstance);
  }
}
