package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.*;
import com.github.dakusui.pcond.core.fluent.verifiers.*;

import static com.github.dakusui.pcond.core.fluent.Fluent.value;

public interface AsPhraseFactory<
    MS extends Matcher.ForString<OIN>,
    MI extends Matcher.ForInteger<OIN>,
    MB extends Matcher.ForBoolean<OIN>,
    OIN> {
  MS asString();

  MI asInteger();

  MB asBoolean();

  default <T> Matcher.ForObject<OIN, T> asObject() {
    return asValueOf(value());
  }

  default <E> Matcher.ForObject<OIN, E> as(E value) {
    return asValueOf(value);
  }

  default <E> Matcher.ForObject<OIN, E> asValueOfClass(Class<E> klass) {
    return asValueOf(value());
  }

  default <E> Matcher.ForList<OIN, E> asListOfClass(Class<E> klass) {
    return asListOf(value());
  }

  default <E> Matcher.ForStream<OIN, E> asStreamOfClass(Class<E> klass) {
    return asStreamOf(value());
  }

  <E> Matcher.ForObject<OIN, E> asValueOf(E value);

  <E> Matcher.ForList<OIN, E> asListOf(E value);

  <E> Matcher.ForStream<OIN, E> asStreamOf(E value);

  interface ForTransformer<OIN> extends AsPhraseFactory<
      StringTransformer<OIN>,
      IntegerTransformer<OIN>,
      BooleanTransformer<OIN>,
      OIN> {
    @Override
    default <E> ObjectTransformer<OIN, E> as(E value) {
      return asValueOf(value);
    }

    @Override
    default <T> ObjectTransformer<OIN, T> asObject() {
      return asValueOf(value());
    }

    @Override
    default <E> ObjectTransformer<OIN, E> asValueOfClass(Class<E> klass) {
      return asValueOf(value());
    }

    @Override
    default <E> ListTransformer<OIN, E> asListOfClass(Class<E> klass) {
      return asListOf(value());
    }

    @Override
    default <E> StreamTransformer<OIN, E> asStreamOfClass(Class<E> klass) {
      return asStreamOf(value());
    }

    @Override
    <E> ObjectTransformer<OIN, E> asValueOf(E value);

    @Override
    <E> ListTransformer<OIN, E> asListOf(E value);

    @Override
    <E> StreamTransformer<OIN, E> asStreamOf(E value);
  }

  interface ForFluent<OIN> extends ForTransformer<OIN> {

  }

  interface ForVerifier<OIN> extends AsPhraseFactory<
      StringVerifier<OIN>,
      IntegerVerifier<OIN>,
      BooleanVerifier<OIN>,
      OIN
      > {
    @Override
    default <E> ObjectVerifier<OIN, E> as(E value) {
      return asValueOf(value);
    }

    @Override
    default <T> ObjectVerifier<OIN, T> asObject() {
      return asValueOf(value());
    }

    @Override
    default <E> ObjectVerifier<OIN, E> asValueOfClass(Class<E> value) {
      return asValueOf(value());
    }

    @Override
    default <E> ListVerifier<OIN, E> asListOfClass(Class<E> value) {
      return asListOf(value());
    }

    @Override
    default <E> StreamVerifier<OIN, E> asStreamOfClass(Class<E> value) {
      return asStreamOf(value());
    }

    @Override
    <E> ObjectVerifier<OIN, E> asValueOf(E value);

    @Override
    <E> ListVerifier<OIN, E> asListOf(E value);

    @Override
    <E> StreamVerifier<OIN, E> asStreamOf(E value);

  }
}
