package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.ListTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.ObjectTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.StringTransformer;
import com.github.dakusui.pcond.forms.Functions;

public class Fluent<OIN, OUT> {
  public Fluent() {
  }

  public StringTransformer<OIN> string(String transformerName) {
    return new StringTransformer<>(transformerName, null, value());
  }

  public ObjectTransformer<OIN, OUT> object(String transformerName) {
    return new ObjectTransformer<>(transformerName, null, value());
  }

  public ObjectTransformer<OIN, OUT> objectOf(String transformerName, OUT value) {
    return new ObjectTransformer<>(transformerName, null, value());
  }

  public <E> ListTransformer<OIN, E> listOf(String transformerName, E value) {
    return new ListTransformer<>(transformerName, null, value());
  }

  public static <T> T value() {
    return Functions.value();
  }
}
