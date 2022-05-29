package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.verifiers.*;

import java.util.function.Function;

public interface IntoPhraseFactory<OIN, IN> {
  default IStringVerifier<OIN> intoString() {
    return intoStringWith((IN v) -> (String)v);
  }

  default IIntegerVerifier.IntegerVerifier<OIN> intoInteger() {
    return intoIntegerWith((IN v) -> (Integer) v);
  }

  default IBooleanVerifier.BooleanVerifier<OIN> intoBoolean() {
    return intoBooleanWith((IN v) -> (Boolean) v);
  }

  @SuppressWarnings("unchecked")
  default <NOUT> IObjectVerifier.ObjectVerifier<OIN, NOUT> intoObject() {
    return intoObjectWith((IN v) -> (NOUT)v);
  }

  IStringVerifier<OIN> intoStringWith(Function<IN, String> function);

  IIntegerVerifier.IntegerVerifier<OIN> intoIntegerWith(Function<IN, Integer> function);

  IBooleanVerifier.BooleanVerifier<OIN> intoBooleanWith(Function<IN, Boolean> function);

  <OUT> IObjectVerifier.ObjectVerifier<OIN, OUT> intoObjectWith(Function<IN, OUT> function);

  interface ForVerifier<OIN, IN> extends IntoPhraseFactory<OIN, IN> {

  }
}
