package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.verifiers.*;

import java.util.function.Function;

public interface IntoPhraseFactory<OIN, IN> {
  default StringVerifier<OIN> intoString() {
    return intoStringWith((IN v) -> (String)v);
  }

  default IntegerVerifier.Impl<OIN> intoInteger() {
    return intoIntegerWith((IN v) -> (Integer) v);
  }

  default BooleanVerifier.Impl<OIN> intoBoolean() {
    return intoBooleanWith((IN v) -> (Boolean) v);
  }

  @SuppressWarnings("unchecked")
  default <NOUT> ObjectVerifier.Impl<OIN, NOUT> intoObject() {
    return intoObjectWith((IN v) -> (NOUT)v);
  }

  StringVerifier<OIN> intoStringWith(Function<IN, String> function);

  IntegerVerifier.Impl<OIN> intoIntegerWith(Function<IN, Integer> function);

  BooleanVerifier.Impl<OIN> intoBooleanWith(Function<IN, Boolean> function);

  <OUT> ObjectVerifier.Impl<OIN, OUT> intoObjectWith(Function<IN, OUT> function);

  interface ForVerifier<OIN, IN> extends IntoPhraseFactory<OIN, IN> {

  }
}
