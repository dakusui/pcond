package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.*;
import com.github.dakusui.pcond.forms.Functions;

import static com.github.dakusui.pcond.internals.InternalUtils.dummyFunction;

public class Fluent<OIN> implements AsPhraseFactory.ForFluent<OIN> {
  final         String transformerName;
  private final OIN    originalInputValue;

  public Fluent(String transformerName) {
    this(transformerName, null);
  }

  public Fluent(String transformerName, OIN originalInputValue) {
    this.transformerName = transformerName;
    this.originalInputValue = originalInputValue;
  }

  public static <T> T value() {
    return Functions.value();
  }

  @Override
  public IStringTransformer<OIN> asString() {
    return new StringTransformer<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public IntegerTransformer<OIN> asInteger() {
    return new IntegerTransformer<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public BooleanTransformer<OIN> asBoolean() {
    return new BooleanTransformer<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public <E> ObjectTransformer<OIN, E> asValueOf(E value) {
    return new ObjectTransformer<>(this.transformerName, null, dummyFunction(), originalInputValue);
  }

  @Override
  public <E> ListTransformer<OIN, E> asListOf(E value) {
    return new ListTransformer<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public <E> StreamTransformer<OIN, E> asStreamOf(E value) {
    return new StreamTransformer<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }
}
