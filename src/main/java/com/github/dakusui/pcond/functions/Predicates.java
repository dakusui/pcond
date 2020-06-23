package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.functions.preds.BasePredUtils;
import com.github.dakusui.pcond.functions.preds.StreamUtils;
import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcond.internals.TransformingPredicate;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.functions.Printables.function;
import static com.github.dakusui.pcond.functions.Printables.predicate;
import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.lang.String.format;
import static java.util.Arrays.asList;
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

  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> Predicate<T> greaterThan(T value) {
    return (Predicate<T>) Def.GT_FACTORY.create(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> ge(T value) {
    return greaterThanOrEqualTo(value);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> Predicate<T> greaterThanOrEqualTo(T value) {
    return (Predicate<T>) Def.GE_FACTORY.create(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> lt(T value) {
    return lessThan(value);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> Predicate<T> lessThan(T value) {
    return (Predicate<T>) Def.LT_FACTORY.create(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> le(T value) {
    return lessThanOrEqualTo(value);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> Predicate<T> lessThanOrEqualTo(T value) {
    return (Predicate<T>) Def.LE_FACTORY.create(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> eq(T value) {
    return equalTo(value);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> Predicate<T> equalTo(T value) {
    return (Predicate<T>) Def.EQ_FACTORY.create(value);
  }

  public static Predicate<String> matchesRegex(String regex) {
    requireNonNull(regex);
    return Def.STRING_MATCHES_REGEX_FACTORY.create(regex);
  }

  public static Predicate<String> containsString(String string) {
    requireNonNull(string);
    return Def.STRING_CONTAINS_FACTORY.create(string);
  }

  public static Predicate<String> startsWith(String string) {
    requireNonNull(string);
    return Def.STRING_STARTS_WITH_FACTORY.create(string);
  }

  public static Predicate<String> endsWith(String string) {
    requireNonNull(string);
    return Def.STRING_ENDS_WITH_FACTORY.create(string);
  }

  public static Predicate<String> equalsIgnoreCase(String string) {
    requireNonNull(string);
    return Def.STRING_EQUALS_IGNORECASE_FACTORY.create(string);
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

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <E> Predicate<Stream<? extends E>> allMatch(Predicate<E> predicate) {
    return (Predicate) Def.STREAM_ALL_MATCH_FACTORY.createStreamPred(predicate, predicate, true, false);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <E> Predicate<Stream<? extends E>> noneMatch(Predicate<E> predicate) {
    return (Predicate) Def.STREAM_NONE_MATCH_FACTORY.createStreamPred(predicate, predicate, true, true);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <E> Predicate<Stream<? extends E>> anyMatch(Predicate<E> predicate) {
    return (Predicate) Def.STREAM_ANY_MATCH_FACTORY.createStreamPred(predicate, predicate, false, true);
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

  public static Predicate<Context> endsWith() {
    return null;
  }

  public static <T> Predicate<Context> toContextPredicate(Predicate<T> predicate_, int argIndex) {
    return new PrintableContextPredicate(predicate_, argIndex);
  }

  public static <T> Predicate<Context> toContextPredicate(Predicate<T> predicate) {
    return toContextPredicate(predicate, 0);
  }

  /**
   * Converts a curried function which results in a boolean value in to a predicate.
   *
   * @param curriedFunction A curried function to be converted.
   * @param orderArgs       An array to specify the order in which values in the context are applied to the function.
   * @return A predicate converted from the given curried function.
   */
  public static Predicate<Context> toContextPredicate(CurriedFunction<Object, Object> curriedFunction, int... orderArgs) {
    return Experimentals.Def.applyAndTest(curriedFunction, predicate("contextPredicate", isTrue()), Boolean.class, orderArgs);
  }

  static class PrintableContextPredicate extends PrintablePredicate<Context> implements Evaluable.ContextPred {
    final Evaluable<?> enclosed;
    final Object       identity;
    final int          argIndex;

    protected <T> PrintableContextPredicate(Predicate<T> predicate_, int argIndex) {
      super(
          () -> format("contextPredicate[%s,%s]", predicate_, argIndex),
          context -> predicate_.test(context.<T>valueAt(argIndex)));
      this.enclosed = InternalUtils.toEvaluableIfNecessary(predicate_);
      this.argIndex = argIndex;
      this.identity = asList(predicate_, argIndex);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <TT> Evaluable<? super TT> enclosed() {
      return (Evaluable<? super TT>) enclosed;
    }

    @Override
    public int argIndex() {
      return argIndex;
    }

    @Override
    public int hashCode() {
      return this.identity.hashCode();
    }

    @Override
    public boolean equals(Object anotherObject) {
      if (!(anotherObject instanceof PrintableContextPredicate))
        return false;
      PrintableContextPredicate another = (PrintableContextPredicate) anotherObject;
      return this.identity.equals(another.identity);
    }
  }

  enum Def {
    ;

    public static final  Function<Class<?>, Predicate<?>>             IS_INSTANCE_OF$2                 = function(() -> "isInstanceOf", (Class<?> c) -> c::isInstance);
    private static final Predicate<?>                                 ALWAYS_TRUE                      = predicate("alwaysTrue", t -> true);
    private static final Predicate<Boolean>                           IS_TRUE                          = predicate("isTrue", (Boolean v) -> v);
    private static final Predicate<Boolean>                           IS_FALSE                         = predicate("isFalse", (Boolean v) -> !v);
    private static final Predicate<?>                                 IS_NULL                          = predicate("isNull", Objects::isNull);
    private static final Predicate<?>                                 IS_NOT_NULL                      = predicate("isNotNull", Objects::nonNull);
    private static final Predicate<String>                            IS_EMPTY_STRING                  = predicate("isEmpty", String::isEmpty);
    private static final Predicate<String>                            IS_NULL_OR_EMPTY_STRING          = predicate("isNullOrEmptyString", s -> Objects.isNull(s) || isEmptyString().test(s)
    );
    private static final Predicate<Object[]>                          IS_EMPTY_ARRAY                   = predicate("isEmptyArray", objects -> objects.length == 0);
    private static final Predicate<Collection<?>>                     IS_EMPTY_COLLECTION              = predicate("isEmpty", Collection::isEmpty);
    private static final BasePredUtils.Factory<Object, Object>        IS_EQUAL_TO_FACTORY              = Printables.predicateFactory(
        (arg) -> format("isEqualTo[%s]", formatObject(arg)),
        arg -> v -> Objects.equals(v, arg));
    private static final BasePredUtils.Factory<Collection<?>, Object> CONTAINS_FACTORY                 = Printables.predicateFactory(
        (Object arg) -> format("contains[%s]", formatObject(arg)),
        (Object arg) -> (Collection<?> c) -> c.contains(arg));
    private static final BasePredUtils.Factory<Object, Object>        OBJECT_IS_SAME_AS_FACTORY        = Printables.predicateFactory(
        arg -> format("==[%s]", formatObject(arg)),
        arg -> v -> v == arg);
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final BasePredUtils.Factory<?, Comparable<?>>      GT_FACTORY                       = Printables.predicateFactory(
        (arg) -> format(">[%s]", formatObject(arg)),
        arg -> v -> ((Comparable) v).compareTo(arg) > 0);
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final BasePredUtils.Factory<?, Comparable<?>>      GE_FACTORY                       = Printables.predicateFactory(
        (arg) -> format(">=[%s]", formatObject(arg)),
        arg -> v -> ((Comparable) v).compareTo(arg) >= 0);
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final BasePredUtils.Factory<?, Comparable<?>>      LE_FACTORY                       = Printables.predicateFactory(
        (arg) -> format("<=[%s]", formatObject(arg)),
        arg -> v -> ((Comparable) v).compareTo(arg) <= 0);
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final BasePredUtils.Factory<?, Comparable<?>>      LT_FACTORY                       = Printables.predicateFactory(
        (arg) -> format("<[%s]", formatObject(arg)),
        arg -> v -> ((Comparable) v).compareTo(arg) < 0);
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final BasePredUtils.Factory<?, Comparable<?>>      EQ_FACTORY                       = Printables.predicateFactory(
        (arg) -> format("=[%s]", formatObject(arg)),
        arg -> v -> ((Comparable) v).compareTo(arg) == 0);
    private static final BasePredUtils.Factory<String, String>        STRING_MATCHES_REGEX_FACTORY     = Printables.predicateFactory(
        (arg) -> format("matchesRegex[%s]", formatObject(arg)),
        arg -> (String s) -> s.matches(arg));
    private static final BasePredUtils.Factory<String, String>        STRING_CONTAINS_FACTORY          = Printables.predicateFactory(
        (arg) -> format("containsString[%s]", formatObject(arg)),
        arg -> (String s) -> s.contains(arg));
    private static final BasePredUtils.Factory<String, String>        STRING_STARTS_WITH_FACTORY       = Printables.predicateFactory(
        (arg) -> format("startsWith[%s]", formatObject(arg)),
        (arg) -> (String s) -> s.startsWith(arg));
    private static final BasePredUtils.Factory<String, String>        STRING_ENDS_WITH_FACTORY         = Printables.predicateFactory(
        (arg) -> format("endsWith[%s]", formatObject(arg)),
        arg -> (String s) -> s.endsWith(arg));
    private static final BasePredUtils.Factory<String, String>        STRING_EQUALS_IGNORECASE_FACTORY = Printables.predicateFactory(
        (arg) -> format("equalsIgnoreCase[%s]", formatObject(arg)),
        arg -> (String s) -> s.equalsIgnoreCase(arg));
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final StreamUtils.Factory<Stream<?>, Predicate<?>> STREAM_ALL_MATCH_FACTORY         = StreamUtils.factory(
        (arg) -> format("allMatch[%s]", requireNonNull(arg)),
        arg -> (Stream<?> stream) -> stream.allMatch((Predicate) arg)
    );
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final StreamUtils.Factory<Stream<?>, Predicate<?>> STREAM_NONE_MATCH_FACTORY        = StreamUtils.factory(
        (arg) -> format("noneMatch[%s]", requireNonNull(arg)),
        arg -> (Stream<?> stream) -> stream.noneMatch((Predicate) arg)
    );
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final StreamUtils.Factory<Stream<?>, Predicate<?>> STREAM_ANY_MATCH_FACTORY         = StreamUtils.factory(
        (arg) -> format("anyMatch[%s]", requireNonNull(arg)),
        arg -> (Stream<?> stream) -> stream.anyMatch((Predicate) arg)
    );
  }
}
