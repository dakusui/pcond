package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.core.identifieable.IdentifiablePredicateFactory;
import com.github.dakusui.pcond.core.identifieable.IdentifiablePredicateFactory.ParameterizedLeafFactory;
import com.github.dakusui.pcond.core.preds.BasePredUtils;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.core.refl.Parameter;
import com.github.dakusui.pcond.internals.InternalChecks;
import com.github.dakusui.pcond.internals.TransformingPredicate;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.refl.ReflUtils.invokeMethod;
import static com.github.dakusui.pcond.functions.Printables.function;
import static com.github.dakusui.pcond.functions.Printables.predicate;
import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public enum Predicates {
  ;

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> alwaysTrue() {
    return (Predicate<T>) Def.ALWAYS_TRUE;
  }

  public static Predicate<Boolean> isTrue() {
    return Def.IS_TRUE;
  }

  public static Predicate<Boolean> isFalse() {
    return Def.IS_FALSE;
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> isNull() {
    return (Predicate<T>) Def.IS_NULL;
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> isNotNull() {
    return (Predicate<T>) Def.IS_NOT_NULL;
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <T> Predicate<T> isEqualTo(T value) {
    return Predicate.class.cast(Def.IS_EQUAL_TO_FACTORY.create(value));
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> isSameReferenceAs(T value) {
    return (Predicate<T>) Def.OBJECT_IS_SAME_AS_FACTORY.create(value);
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
    return ParameterizedLeafFactory.greaterThan(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> ge(T value) {
    return greaterThanOrEqualTo(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> greaterThanOrEqualTo(T value) {
    return ParameterizedLeafFactory.greaterThanOrEqualTo(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> lt(T value) {
    return lessThan(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> lessThan(T value) {
    return ParameterizedLeafFactory.lessThan(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> le(T value) {
    return lessThanOrEqualTo(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> lessThanOrEqualTo(T value) {
    return ParameterizedLeafFactory.lessThanOrEqualTo(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> eq(T value) {
    return equalTo(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> equalTo(T value) {
    return ParameterizedLeafFactory.equalTo(value);
  }

  public static Predicate<String> matchesRegex(String regex) {
    requireNonNull(regex);
    return ParameterizedLeafFactory.matchesRegex(regex);
  }

  public static Predicate<String> containsString(String string) {
    requireNonNull(string);
    return ParameterizedLeafFactory.containsString(string);
  }

  public static Predicate<String> startsWith(String string) {
    requireNonNull(string);
    return ParameterizedLeafFactory.startsWith(string);
  }

  public static Predicate<String> endsWith(String string) {
    requireNonNull(string);
    return ParameterizedLeafFactory.endsWith(string);
  }

  public static Predicate<String> equalsIgnoreCase(String string) {
    requireNonNull(string);
    return ParameterizedLeafFactory.equalsIgnoreCase(string);
  }

  public static Predicate<String> isEmptyString() {
    return Def.IS_EMPTY_STRING;
  }

  public static Predicate<String> isNullOrEmptyString() {
    return Def.IS_NULL_OR_EMPTY_STRING;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <E> Predicate<Collection<E>> contains(Object entry) {
    requireNonNull(entry);
    return (Predicate) Def.CONTAINS_FACTORY.create(entry);
  }

  public static Predicate<Object[]> isEmptyArray() {
    return Def.IS_EMPTY_ARRAY;
  }

  public static Predicate<? super Collection<?>> isEmpty() {
    return Def.IS_EMPTY_COLLECTION;
  }

  public static <E> Predicate<Stream<? extends E>> allMatch(Predicate<E> predicate) {
    requireNonNull(predicate);
    return IdentifiablePredicateFactory.allMatch(predicate);
  }

  public static <E> Predicate<Stream<? extends E>> noneMatch(Predicate<E> predicate) {
    requireNonNull(predicate);
    return IdentifiablePredicateFactory.noneMatch(predicate);
  }

  public static <E> Predicate<Stream<? extends E>> anyMatch(Predicate<E> predicate) {
    requireNonNull(predicate);
    return IdentifiablePredicateFactory.anyMatch(predicate);
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

  public static <O, P> TransformingPredicate.Factory<P, O> transform(String funcName, Function<? super O, ? extends P> func) {
    return transform(function(funcName, func));
  }

  public static <O, P> TransformingPredicate.Factory<P, O> transform(Function<? super O, ? extends P> function) {
    return cond -> new TransformingPredicate<>(cond, function);
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
  @SuppressWarnings("ConstantConditions")
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

    public static final  Function<Class<?>, Predicate<?>>             IS_INSTANCE_OF$2          = function(() -> "isInstanceOf", (Class<?> c) -> c::isInstance);
    private static final Predicate<?>                                 ALWAYS_TRUE               = predicate("alwaysTrue", t -> true);
    private static final Predicate<Boolean>                           IS_TRUE                   = predicate("isTrue", (Boolean v) -> v);
    private static final Predicate<Boolean>                           IS_FALSE                  = predicate("isFalse", (Boolean v) -> !v);
    private static final Predicate<?>                                 IS_NULL                   = predicate("isNull", Objects::isNull);
    private static final Predicate<?>                                 IS_NOT_NULL               = predicate("isNotNull", Objects::nonNull);
    private static final Predicate<String>                            IS_EMPTY_STRING           = predicate("isEmpty", String::isEmpty);
    private static final Predicate<String>                            IS_NULL_OR_EMPTY_STRING   = predicate("isNullOrEmptyString", s -> Objects.isNull(s) || isEmptyString().test(s)
    );
    private static final Predicate<Object[]>                          IS_EMPTY_ARRAY            = predicate("isEmptyArray", objects -> objects.length == 0);
    private static final Predicate<Collection<?>>                     IS_EMPTY_COLLECTION       = predicate("isEmpty", Collection::isEmpty);
    private static final BasePredUtils.Factory<Object, Object>        IS_EQUAL_TO_FACTORY       = Printables.predicateFactory(
        (arg) -> format("isEqualTo[%s]", formatObject(arg)),
        arg -> v -> Objects.equals(v, arg));
    private static final BasePredUtils.Factory<Collection<?>, Object> CONTAINS_FACTORY          = Printables.predicateFactory(
        (Object arg) -> format("contains[%s]", formatObject(arg)),
        (Object arg) -> (Collection<?> c) -> c.contains(arg));
    private static final BasePredUtils.Factory<Object, Object>        OBJECT_IS_SAME_AS_FACTORY = Printables.predicateFactory(
        arg -> format("==[%s]", formatObject(arg)),
        arg -> v -> v == arg);
  }
}
