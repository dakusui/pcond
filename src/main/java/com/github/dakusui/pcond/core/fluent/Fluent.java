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
    return new IStringTransformer.StringTransformer<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public IIntegerTransformer<OIN> asInteger() {
    return new IIntegerTransformer.IntegerTransformer<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public IBooleanTransformer.BooleanTransformer<OIN> asBoolean() {
    return new IBooleanTransformer.BooleanTransformer<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public <E> IObjectTransformer<OIN, E> asValueOf(E value) {
    return new IObjectTransformer.ObjectTransformer<>(this.transformerName, null, dummyFunction(), originalInputValue);
  }

  @Override
  public <E> IListTransformer<OIN, E> asListOf(E value) {
    return new IListTransformer.ListTransformer<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public <E> IStreamTransformer<OIN, E> asStreamOf(E value) {
    return new IStreamTransformer.StreamTransformer<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }
}
