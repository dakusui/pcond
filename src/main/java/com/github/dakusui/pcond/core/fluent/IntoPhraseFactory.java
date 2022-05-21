package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.verifiers.BooleanVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.IntegerVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.ObjectVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.StringVerifier;

import java.util.function.Function;

public interface IntoPhraseFactory<OIN, IN> {
  default StringVerifier<OIN> intoString() {
    return intoStringWith((IN v) -> (String)v);
  }

  default IntegerVerifier<OIN> intoInteger() {
    return intoIntegerWith((IN v) -> (Integer) v);
  }

  default BooleanVerifier<OIN> intoBoolean() {
    return intoBooleanWith((IN v) -> (Boolean) v);
  }

  @SuppressWarnings("unchecked")
  default <NOUT> ObjectVerifier<OIN, NOUT> intoObject() {
    return intoObjectWith((IN v) -> (NOUT)v);
  }

  StringVerifier<OIN> intoStringWith(Function<IN, String> function);

  IntegerVerifier<OIN> intoIntegerWith(Function<IN, Integer> function);

  BooleanVerifier<OIN> intoBooleanWith(Function<IN, Boolean> function);

  <OUT> ObjectVerifier<OIN, OUT> intoObjectWith(Function<IN, OUT> function);

  interface ForVerifier<OIN, IN> extends IntoPhraseFactory<OIN, IN> {

  }
}
