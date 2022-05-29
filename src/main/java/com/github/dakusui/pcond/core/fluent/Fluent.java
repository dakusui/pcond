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
  public StringTransformer<OIN> asString() {
    return new StringTransformer.Impl<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public IntegerTransformer<OIN> asInteger() {
    return new IntegerTransformer.Impl<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public BooleanTransformer.Impl<OIN> asBoolean() {
    return new BooleanTransformer.Impl<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public <E> ObjectTransformer<OIN, E> asValueOf(E value) {
    return new ObjectTransformer.Impl<>(this.transformerName, null, dummyFunction(), originalInputValue);
  }

  @Override
  public <E> ListTransformer<OIN, E> asListOf(E value) {
    return new ListTransformer.Impl<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public <E> StreamTransformer<OIN, E> asStreamOf(E value) {
    return new StreamTransformer.Impl<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }
}
