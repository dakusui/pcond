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

  default Matcher.ForObject<OIN, OIN> asObject() {
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
      IStringTransformer<OIN>,
      IIntegerTransformer<OIN>,
      IBooleanTransformer<OIN>,
      OIN> {
    @Override
    default <E> IObjectTransformer<OIN, E> as(E value) {
      return asValueOf(value);
    }

    @Override
    default IObjectTransformer<OIN, OIN> asObject() {
      return asValueOf(value());
    }

    @Override
    default <E> IObjectTransformer<OIN, E> asValueOfClass(Class<E> klass) {
      return asValueOf(value());
    }

    @Override
    default <E> IListTransformer<OIN, E> asListOfClass(Class<E> klass) {
      return asListOf(value());
    }

    @Override
    default <E> IStreamTransformer<OIN, E> asStreamOfClass(Class<E> klass) {
      return asStreamOf(value());
    }

    @Override
    <E> IObjectTransformer<OIN, E> asValueOf(E value);

    @Override
    <E> IListTransformer<OIN, E> asListOf(E value);

    @Override
    <E> IStreamTransformer<OIN, E> asStreamOf(E value);

    default <E> IObjectTransformer<OIN, E> valueAt(int i, E value) {
      return asListOf(value).elementAt(i);
    }

    @SuppressWarnings("unchecked")
    default <E> IObjectTransformer<OIN, E> valueAt(int i) {
      return asListOf((E)value()).elementAt(i);
    }
  }

  interface ForFluent<OIN> extends ForTransformer<OIN> {

  }

  interface ForVerifier<OIN> extends AsPhraseFactory<
      IStringVerifier<OIN>,
      IIntegerVerifier<OIN>,
      IBooleanVerifier<OIN>,
      OIN
      > {
    @Override
    default <E> IObjectVerifier<OIN, E> as(E value) {
      return asValueOf(value);
    }

    @Override
    default IObjectVerifier<OIN, OIN> asObject() {
      return asValueOf(value());
    }

    @Override
    default <E> IObjectVerifier<OIN, E> asValueOfClass(Class<E> value) {
      return asValueOf(value());
    }

    @Override
    default <E> IListVerifier<OIN, E> asListOfClass(Class<E> value) {
      return asListOf(value());
    }

    @Override
    default <E> IStreamVerifier<OIN, E> asStreamOfClass(Class<E> value) {
      return asStreamOf(value());
    }

    @Override
    <E> IObjectVerifier<OIN, E> asValueOf(E value);

    @Override
    <E> IListVerifier<OIN, E> asListOf(E value);

    @Override
    <E> IStreamVerifier<OIN, E> asStreamOf(E value);

  }
}
