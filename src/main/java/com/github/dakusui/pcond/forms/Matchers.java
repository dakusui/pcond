package com.github.dakusui.pcond.forms;

import com.github.dakusui.pcond.core.printable.Matcher;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public enum Matchers {
  ;


  public static Matcher.Builder.Builder0<String> matcherForString() {
    return matcher().forType(String.class);
  }


  public static <IN> Matcher.Builder.Builder0<IN> matcher() {
    return new Matcher.Builder.Builder0<>();
  }


  public static <IN> Matcher.Builder.Builder0<IN> matcherFor(Class<IN> inType) {
    return Matchers.matcher().forType(inType);
  }

  public static <E> Matcher.Builder.Builder0<E[]> matcherForArrayOf(Class<E> elementType) {
    return Matchers.matcher();
  }

  public static <E> Matcher.Builder.Builder0<List<E>> matcherForListOf(Class<E> elementType) {
    return Matchers.matcher();
  }

  public static <E> Matcher.Builder.Builder0<Collection<E>> matcherForCollectionOf(
      @SuppressWarnings("unused") Class<E> elementType) {
    return Matchers.matcher();
  }

  public static <K, V> Matcher.Builder.Builder0<Map<K, V>> matcherForMapOf(Class<K> keyType, Class<V> valueType) {
    return Matchers.matcher();
  }
}
