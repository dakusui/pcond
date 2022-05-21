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

  default <E> Matcher.ForObject<OIN, E> as(E value) {
    return asValueOf(value);
  }

  default <T> Matcher.ForObject<OIN, T> asObject() {
    return asValueOf(value());
  }

  default <E> Matcher.ForObject<OIN, E> asValueOfClass() {
    return asValueOf(value());
  }

  default <E> Matcher.ForList<OIN, E> asListOfClass() {
    return asListOf(value());
  }

  default <E> Matcher.ForStream<OIN, E> asStreamOfClass() {
    return asStreamOf(value());
  }

  <E> Matcher.ForObject<OIN, E> asValueOf(E value);

  <E> Matcher.ForList<OIN, E> asListOf(E value);

  <E> Matcher.ForStream<OIN, E> asStreamOf(E value);

  interface ForTransformer<OIN, OUT, TX extends Transformer<TX, OIN, OUT>> extends AsPhraseFactory<
      StringTransformer<OIN>,
      IntegerTransformer<OIN>,
      BooleanTransformer<OIN>,
      OIN
      > {
    @Override
    default <E> ObjectTransformer<OIN, E> as(E value) {
      return asValueOf(value);
    }

    @Override
    default <T> ObjectTransformer<OIN, T> asObject() {
      return asValueOf(value());
    }

    @Override
    default <E> ObjectTransformer<OIN, E> asValueOfClass() {
      return asValueOf(value());
    }

    @Override
    default <E> ListTransformer<OIN, E> asListOfClass() {
      return asListOf(value());
    }

    @Override
    default <E> StreamTransformer<OIN, E> asStreamOfClass() {
      return asStreamOf(value());
    }

    @Override
    <E> ObjectTransformer<OIN, E> asValueOf(E value);

    @Override
    <E> ListTransformer<OIN, E> asListOf(E value);

    @Override
    <E> StreamTransformer<OIN, E> asStreamOf(E value);

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
    default <E> ObjectVerifier<OIN, E> asValueOfClass() {
      return asValueOf(value());
    }

    @Override
    default <E> ListVerifier<OIN, E> asListOfClass() {
      return asListOf(value());
    }

    @Override
    default <E> StreamVerifier<OIN, E> asStreamOfClass() {
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
