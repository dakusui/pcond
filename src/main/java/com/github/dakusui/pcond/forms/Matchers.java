package com.github.dakusui.pcond.forms;

import com.github.dakusui.pcond.TestAssertions;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory.TransformingPredicate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.github.dakusui.pcond.forms.Predicates.greaterThan;

public enum Matchers {
  ;

  public static <P> TransformingPredicate.Factory<P, String> matcherForString(Function<String, P> function) {
    return Predicates.transform(function).castTo(String.class);
  }

  public static <IN, IM> TransformingPredicate.Builder<IN, IM> matcher(String name) {
    return new TransformingPredicate.Builder<>(name);
  }

  public static <IN, IM> TransformingPredicate.Builder<IN, IM> matcher() {
    return new TransformingPredicate.Builder<>();
  }

  public static <IN, IM> TransformingPredicate.Builder<IN, IM> matcherFor(Class<IN> inType) {
    return Matchers.<IN, IM>matcher().forValueOf(inType);
  }

  public static <E, IM> TransformingPredicate.Builder<E[], IM> matcherForArrayOf(Class<E> elementType) {
    return Matchers.matcher();
  }

  public static <IM> TransformingPredicate.Builder<String, IM> matcherForString() {
    return Matchers.<String, IM>matcher().forString();
  }

  public static <E, IM> TransformingPredicate.Builder<List<E>, IM> matcherForListOf(Class<E> elementType) {
    return Matchers.<List<E>, IM>matcher().forListOf(elementType);
  }

  public static <E, IM> TransformingPredicate.Builder<List<E>, IM> matcherForCollectionOf(Class<E> elementType) {
    return Matchers.<Collection<E>, IM>matcher().forListOf(elementType);
  }

  public static <K, V, IM> TransformingPredicate.Builder<Map<K, V>, IM> matcherForMapOf(Class<K> keyType, Class<V> valueType) {
    return Matchers.matcher();
  }

  public static void main(String... args) {
    String out = "hello, world";
    TestAssertions.assertThat(out, matcherForString(Integer::parseInt).check(greaterThan(0)));
    matcher("parseInt").forValueOf(String.class)
        .transformBy(Integer::parseInt).into(int.class)
        .thenVerifyWith(greaterThan(0));
  }
}
