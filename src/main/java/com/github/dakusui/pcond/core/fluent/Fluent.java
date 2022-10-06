package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.*;
import com.github.dakusui.pcond.core.fluent.transformers.LongTransformer;
import com.github.dakusui.pcond.fluent.Fluents;

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
    return Fluents.value();
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
  public LongTransformer<OIN> asLong() {
    return new LongTransformer.Impl<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public ShortTransformer<OIN> asShort() {
    return new ShortTransformer.Impl<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public DoubleTransformer<OIN> asDouble() {
    return new DoubleTransformer.Impl<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public FloatTransformer<OIN> asFloat() {
    return new FloatTransformer.Impl<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }

  @Override
  public BooleanTransformer<OIN> asBoolean() {
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
  public <E> ListTransformer<OIN, E> asListOfClass(Class<E> klass) {
    return AsPhraseFactory.ForFluent.super.asListOfClass(klass);
  }

  @Override
  public <E> StreamTransformer<OIN, E> asStreamOf(E value) {
    return new StreamTransformer.Impl<>(this.transformerName, null, dummyFunction(), this.originalInputValue);
  }


  @Override
  public <E> StreamTransformer<OIN, E> asStreamOfClass(Class<E> klass) {
    return AsPhraseFactory.ForFluent.super.asStreamOfClass(klass);
  }
}
