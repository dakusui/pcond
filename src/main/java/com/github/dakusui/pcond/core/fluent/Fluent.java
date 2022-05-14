package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.ListTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.ObjectTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.StringTransformer;

import java.util.Map;

public class Fluent<OIN, OUT> {
  public Fluent() {
  }

  public StringTransformer<OIN> string() {
    return new StringTransformer<>(null);
  }

  public ObjectTransformer<OIN, OUT> object() {
    return new ObjectTransformer<>(null);
  }

  public ObjectTransformer<OIN, OUT> objectOf(OUT value) {
    return new ObjectTransformer<>(null);
  }

  public <E> ListTransformer<OIN, E> listOf(E value) {
    return new ListTransformer(null);
  }

  public <K, V> ObjectTransformer<OIN, Map<K, V>> mapOf(K key, V value) {
    return new ObjectTransformer<>(null);
  }
}
