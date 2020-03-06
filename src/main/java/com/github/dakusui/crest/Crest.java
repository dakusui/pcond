package com.github.dakusui.crest;

import com.github.dakusui.crest.core.*;
import com.github.dakusui.crest.core.Call.Arg;
import com.github.dakusui.crest.core.Executable;
import com.github.dakusui.crest.matcherbuilders.*;
import com.github.dakusui.crest.matcherbuilders.primitives.*;
import com.github.dakusui.pcond.functions.Functions;
import com.github.dakusui.pcond.functions.Printable;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.crest.utils.InternalUtils.*;
import static com.github.dakusui.pcond.functions.Functions.trivial;
import static com.github.dakusui.pcond.functions.Predicates.equalTo;
import static com.github.dakusui.pcond.functions.Predicates.isEmptyArray;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * A facade class of 'thincrest'.
 */
public enum Crest {
  ;

  /**
   * A bit better version of CoreMatchers.allOf.
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   *
   * @param matchers Child matchers.
   * @param <T>      Type of the value to be matched with the returned matcher.
   * @return A matcher that matches when all of given {@code matchers} match.
   */
  @SafeVarargs
  public static <T> Matcher<T> allOf(Matcher<? super T>... matchers) {
    requireArgument(matchers, isEmptyArray().negate());
    return Matcher.Conjunctive.create(asList(matchers));
  }

  /**
   * A bit better version of CoreMatchers.anyOf.
   * For example:
   * <pre>assertThat("myValue", anyOf(startsWith("my"), containsString("Val")))</pre>
   *
   * @param matchers Child matchers.
   * @param <T>      Type of the value to be matched with the returned matcher.
   * @return A matcher that matches when any of given {@code matchers} matches.
   */
  @SafeVarargs
  public static <T> Matcher<T> anyOf(Matcher<? super T>... matchers) {
    requireArgument(matchers, isEmptyArray().negate());
    return Matcher.Disjunctive.create(asList(matchers));
  }

  public static <T> Matcher<T> not(Matcher<? super T> matcher) {
    return Matcher.Negative.create(matcher);
  }


  @SuppressWarnings("unchecked")
  public static <T> Matcher<T> noneOf(@SuppressWarnings("rawtypes") Matcher... matcher) {
    return new Matcher.Composite.Base<T>(Arrays.asList(matcher)) {
      @Override
      public String name() {
        return "noneOf";
      }

      @Override
      protected boolean first() {
        return true;
      }

      @Override
      protected boolean op(boolean current, boolean next) {
        return current && !next;
      }
    };
  }

  public static <I> AsObject<I, I> asObject() {
    return new AsObject<>(trivial(Functions.identity()));
  }

  public static <I, O> AsObject<I, O> asObject(String methodName, Object... args) {
    return new AsObject<>(Functions.<I, O>invoke(methodName, args));
  }

  public static <I, O> AsObject<I, O> asObject(Function<? super I, ? extends O> function) {
    return new AsObject<>(function);
  }

  public static AsBoolean<Boolean> asBoolean() {
    return asBoolean(trivial(Functions.identity()));
  }

  public static <I> AsBoolean<I> asBoolean(String methodName, Object... args) {
    return asBoolean(Functions.invoke(methodName, args).andThen(Functions.cast(Boolean.class)));
  }

  public static <I> AsBoolean<I> asBoolean(Predicate<? super I> predicate) {
    requireNonNull(predicate);
    return asBoolean(function(predicate.toString(), predicate::test));
  }

  public static <I> AsBoolean<I> asBoolean(Function<? super I, Boolean> function) {
    return new AsBoolean<>(function);
  }

  public static <I> AsByte<I> asByte(Function<? super I, Byte> function) {
    return new AsByte<>(function);
  }

  public static <I> AsByte<I> asByte(String methodName, Object... args) {
    return asByte(Functions.invoke(methodName, args).andThen(Functions.cast(Byte.class)));
  }

  public static AsByte<Byte> asByte() {
    return asByte(trivial(Functions.identity()));
  }

  public static <I> AsChar<I> asChar(Function<? super I, Character> function) {
    return new AsChar<>(function);
  }

  public static <I> AsChar<I> asChar(String methodName, Object... args) {
    return asChar(Functions.invoke(methodName, args).andThen(Functions.cast(Character.class)));
  }

  public static AsChar<Character> asChar() {
    return asChar(trivial(Functions.identity()));
  }

  public static <I> AsShort<I> asShort(Function<? super I, Short> function) {
    return new AsShort<>(function);
  }

  public static <I> AsShort<I> asShort(String methodName, Object... args) {
    return asShort(Functions.invoke(methodName, args).andThen(Functions.cast(Short.class)));
  }

  public static AsShort<Short> asShort() {
    return asShort(trivial(Functions.identity()));
  }

  public static <I> AsInteger<I> asInteger(Function<? super I, Integer> function) {
    return new AsInteger<>(function);
  }

  public static <I> AsInteger<I> asInteger(String methodName, Object... args) {
    return asInteger(Functions.invoke(methodName, args).andThen(Functions.cast(Integer.class)));
  }

  public static AsInteger<Integer> asInteger() {
    return asInteger(trivial(Functions.identity()));
  }

  public static <I> AsLong<I> asLong(Function<? super I, Long> function) {
    return new AsLong<>(function);
  }

  public static <I> AsLong<I> asLong(String methodName, Object... args) {
    return asLong(Functions.invoke(methodName, args).andThen(Functions.cast(Long.class)));
  }

  public static AsLong<Long> asLong() {
    return asLong(trivial(Functions.identity()));
  }

  public static <I> AsFloat<I> asFloat(Function<? super I, Float> function) {
    return new AsFloat<>(function);
  }

  public static <I> AsFloat<I> asFloat(String methodName, Object... args) {
    return asFloat(Functions.invoke(methodName, args).andThen(Functions.cast(Float.class)));
  }

  public static AsFloat<Float> asFloat() {
    return asFloat(trivial(Functions.identity()));
  }

  public static <I> AsDouble<I> asDouble(Function<? super I, Double> function) {
    return new AsDouble<>(function);
  }

  public static <I> AsDouble<I> asDouble(String methodName, Object... args) {
    return asDouble(Functions.invoke(methodName, args).andThen(Functions.cast(Double.class)));
  }

  public static AsDouble<Double> asDouble() {
    return asDouble(trivial(Functions.identity()));
  }

  /*
   * Casts a given object into the given comparable type
   */
  public static <I extends Comparable<? super I>, S extends AsComparable<I, I, S>>
  S asComparableOf(Class<I> type) {
    return asComparable((Function<? super I, ? extends I>) Functions.cast(type));
  }

  @SuppressWarnings("unchecked")
  public static <I, T extends Comparable<? super T>, S extends AsComparable<I, T, S>>
  S asComparable(Function<? super I, ? extends T> function) {
    return (S) new AsComparable<>(function);
  }

  public static <I, T extends Comparable<? super T>, S extends AsComparable<I, T, S>>
  S asComparableOf(Class<T> type, String methodName, Object... args) {
    return asComparable(Functions.invoke(methodName, args).<T>andThen(Functions.cast(type)));
  }

  public static <I> AsString<I> asString() {
    return asString(trivial(Functions.stringify()));
  }

  public static <I> AsString<I> asString(Function<? super I, ? extends String> function) {
    return new AsString<>(requireNonNull(function));
  }

  @SuppressWarnings({ "RedundantCast", "unchecked" })
  public static <I> AsString<I> asString(String methodName, Object... args) {
    return asString((Function<? super I, ? extends String>) Functions.invoke(methodName, args));
  }

  public static <I extends Collection<?>> AsList<? super I, ?> asObjectList() {
    return asListOf(Object.class, trivial(Functions.collectionToList()));
  }

  public static <I> AsList<? super I, ?> asObjectList(Function<? super I, ? extends List<Object>> function) {
    return asListOf(Object.class, function);
  }

  public static <I extends Collection<E>, E> AsList<I, E> asListOf(Class<E> type) {
    return asListOf(type, trivial(Functions.collectionToList()));
  }

  public static <I, E> AsList<I, E> asListOf(@SuppressWarnings("unused") Class<E> type, Function<? super I, ? extends List<E>> function) {
    return new AsList<>(function);
  }

  public static <I extends Map<?, ?>, SELF extends AsMap<I, Object, Object, SELF>> SELF asObjectMap() {
    return asMapOf(Object.class, Object.class, trivial(function("mapToMap", HashMap::new)));
  }

  public static <I, SELF extends AsMap<I, Object, Object, SELF>> SELF asObjectMap(Function<? super I, ? extends Map<Object, Object>> function) {
    return asMapOf(Object.class, Object.class, function);
  }

  public static <I extends Map<K, V>, K, V, SELF extends AsMap<I, K, V, SELF>> SELF asMapOf(Class<K> keyType, Class<V> valueType) {
    return asMapOf(keyType, valueType, Functions.identity());
  }

  @SuppressWarnings("unchecked")
  public static <I, K, V, SELF extends AsMap<I, K, V, SELF>> SELF asMapOf(Class<K> keyType, Class<V> valueType, Function<? super I, ? extends Map<K, V>> function) {
    requireNonNull(keyType);
    requireNonNull(valueType);
    return (SELF) new AsMap<I, K, V, SELF>(function);
  }

  public static Call call(String methodName, Object... args) {
    return Call.create(methodName, args);
  }

  public static Call call(Class<?> klass, String methodName, Object... args) {
    return callOn(klass, methodName, args);
  }

  public static Call callOn(Object object, String methodName, Object... args) {
    return Call.createOn(object, methodName, args);
  }

  public static <T> Arg<T> arg(Class<T> type, T value) {
    return Arg.of(type, value);
  }

  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <T> Arg<T[]> args(Class<T> type, T... values) {
    return Arg.of((Class<T[]>) Array.newInstance(type, 1).getClass(), values);
  }


  public static Arg<Object[]> args(Object... values) {
    return Crest.args(Object.class, values);
  }

  public static <T> void assertThat(T actual, Matcher<? super T> matcher) {
    assertThat("", actual, matcher);
  }

  public static <T> void assumeThat(T actual, Matcher<? super T> matcher) {
    assumeThat("", actual, matcher);
  }

  public static <T> void requireThat(T actual, Matcher<? super T> matcher) {
    requireThat("", actual, matcher);
  }

  public static <T> void assertThat(String message, T value, Matcher<? super T> matcher) {
    Session.perform(
        message, value, matcher,
        (msg, r, causes) -> new AssertionFailedError(msg, r.expectation(), r.mismatch())
    );
  }

  public static <T> void assumeThat(String message, T value, Matcher<? super T> matcher) {
    Session.perform(
        message, value, matcher,
        (msg, r, causes) -> new TestAbortedException(composeComparisonText(msg, r))
    );
  }

  public static <T> void requireThat(String message, T value, Matcher<? super T> matcher) {
    Session.perform(
        message, value, matcher,
        (msg, r, causes) -> new ExecutionFailure(msg, r.expectation(), r.mismatch(), causes)
    );
  }

  @SuppressWarnings("unchecked")
  public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
    try {
      executable.execute();
    } catch (Throwable e) {
      throwIfBlacklisted(e);
      if (expectedType.isInstance(e))
        return (T) e;
      throw new AssertionFailedError("An exception of unexpected type was thrown.", expectedType, e);
    }
    throw new AssertionFailedError("An exception was expected to be thrown, but not.", expectedType, null);
  }

  public static <T, R> Function<T, R> function(String ss, Function<T, R> function) {
    return Printable.function(String.format("->%s", requireNonNull(ss)), requireNonNull(function));
  }

  public static <T> Predicate<T> predicate(String s, Predicate<T> predicate) {
    return Printable.predicate(s, predicate);
  }

  public static Eater.RegexEater substringAfterRegex(String regex) {
    return new Eater.RegexEater(null, regex);
  }

  public static <T> Eater.ListEater<T> sublistAfterElement(T element) {
    return sublistAfter(equalTo(element));
  }

  public static <T> Eater.ListEater<T> sublistAfter(Predicate<T> predicate) {
    return new Eater.ListEater<>(null, predicate);
  }

}

