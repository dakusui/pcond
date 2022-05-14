package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.ListTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.ObjectTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.StringTransformer;

import java.util.Map;

public class Fluent<OIN, OUT> {
  public Fluent() {
  }

  public StringTransformer<OIN> string() {
    return new StringTransformer<>(value());
  }

  public ObjectTransformer<OIN, OUT> object() {
    return new ObjectTransformer<>(value());
  }

  public ObjectTransformer<OIN, OUT> objectOf(OUT value) {
    return new ObjectTransformer<>(value());
  }

  public <E> ListTransformer<OIN, E> listOf(E value) {
    return new ListTransformer<>(value());
  }

  public <K, V> ObjectTransformer<OIN, Map<K, V>> mapOf(K key, V value) {
    return new ObjectTransformer<>(value());
  }

  public <T> ObjectTransformer<OIN, OUT> instance() {
    return this.objectOf(value());
  }

  public static <T> T value() {
    return null;
  }
}
