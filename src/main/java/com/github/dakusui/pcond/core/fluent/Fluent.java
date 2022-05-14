package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.ToObjectTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.ToStringTransformer;

import java.util.List;
import java.util.Map;

public class Fluent<OIN, OUT> {
  public Fluent() {
  }

  public ToStringTransformer<OIN> string() {
    return new ToStringTransformer<>(null);
  }

  public ToObjectTransformer<OIN, OUT> object() {
    return new ToObjectTransformer<>(null);
  }

  public ToObjectTransformer<OIN, OUT> objectOf(OUT value) {
    return new ToObjectTransformer<>(null);
  }

  public <E> ToObjectTransformer<OIN, List<E>> listOf(E value) {
    return new ToObjectTransformer<>(null);
  }

  public <K, V> ToObjectTransformer<OIN, Map<K, V>> mapOf(K key, V value) {
    return new ToObjectTransformer<>(null);
  }
}
