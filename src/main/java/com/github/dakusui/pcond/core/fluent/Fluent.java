package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.ObjectMatcherBuilderBuilder0;
import com.github.dakusui.pcond.core.fluent.transformers.StringMatcherBuilderBuilder0;

import java.util.List;
import java.util.Map;

public class Fluent<OIN, OUT> {
  public Fluent() {
  }

  public StringMatcherBuilderBuilder0<OIN> string() {
    return new StringMatcherBuilderBuilder0<>(null);
  }

  public ObjectMatcherBuilderBuilder0<OIN, OUT> object() {
    return new ObjectMatcherBuilderBuilder0<>(null);
  }

  public ObjectMatcherBuilderBuilder0<OIN, OUT> objectOf(OUT value) {
    return new ObjectMatcherBuilderBuilder0<>(null);
  }

  public <E> ObjectMatcherBuilderBuilder0<OIN, List<E>> listOf(E value) {
    return new ObjectMatcherBuilderBuilder0<>(null);
  }

  public <K, V> ObjectMatcherBuilderBuilder0<OIN, Map<K, V>> mapOf(K key, V value) {
    return new ObjectMatcherBuilderBuilder0<>(null);
  }
}
