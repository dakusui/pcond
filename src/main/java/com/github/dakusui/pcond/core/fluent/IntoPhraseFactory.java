package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.verifiers.*;

import java.util.function.Function;

public interface IntoPhraseFactory<OIN, IN> {
  default IStringVerifier<OIN> intoString() {
    return intoStringWith((IN v) -> (String)v);
  }

  default IIntegerVerifier.Impl<OIN> intoInteger() {
    return intoIntegerWith((IN v) -> (Integer) v);
  }

  default IBooleanVerifier.Impl<OIN> intoBoolean() {
    return intoBooleanWith((IN v) -> (Boolean) v);
  }

  @SuppressWarnings("unchecked")
  default <NOUT> IObjectVerifier.Impl<OIN, NOUT> intoObject() {
    return intoObjectWith((IN v) -> (NOUT)v);
  }

  IStringVerifier<OIN> intoStringWith(Function<IN, String> function);

  IIntegerVerifier.Impl<OIN> intoIntegerWith(Function<IN, Integer> function);

  IBooleanVerifier.Impl<OIN> intoBooleanWith(Function<IN, Boolean> function);

  <OUT> IObjectVerifier.Impl<OIN, OUT> intoObjectWith(Function<IN, OUT> function);

  interface ForVerifier<OIN, IN> extends IntoPhraseFactory<OIN, IN> {

  }
}
