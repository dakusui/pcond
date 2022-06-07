package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.verifiers.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface IntoPhraseFactory<OIN, IN> {
  default StringVerifier<OIN> intoString() {
    return intoStringWith((IN v) -> (String) v);
  }

  default IntegerVerifier<OIN> intoInteger() {
    return intoIntegerWith((IN v) -> (Integer) v);
  }

  default DoubleVerifier<OIN> intoDouble() {
    return intoDoubleWith((IN v) -> (Double) v);
  }

  default BooleanVerifier<OIN> intoBoolean() {
    return intoBooleanWith((IN v) -> (Boolean) v);
  }

  @SuppressWarnings("unchecked")
  default <NOUT> ObjectVerifier<OIN, NOUT> intoObject() {
    return intoObjectWith((IN v) -> (NOUT) v);
  }

  @SuppressWarnings("unchecked")
  default <E> ListVerifier<OIN, E> intoList() {
    return intoListWith((IN v) -> (List<E>) v);
  }

  @SuppressWarnings("unchecked")
  default <E> StreamVerifier<OIN, E> intoStream() {
    return intoStreamWith((IN v) -> (Stream<E>) v);
  }

  StringVerifier<OIN> intoStringWith(Function<IN, String> function);

  IntegerVerifier<OIN> intoIntegerWith(Function<IN, Integer> function);

  DoubleVerifier<OIN> intoDoubleWith(Function<IN, Double> function);

  BooleanVerifier<OIN> intoBooleanWith(Function<IN, Boolean> function);

  <OUT> ObjectVerifier<OIN, OUT> intoObjectWith(Function<IN, OUT> function);

  <E> ListVerifier<OIN, E> intoListWith(Function<IN, List<E>> function);

  <E> StreamVerifier<OIN, E> intoStreamWith(Function<IN, Stream<E>> function);

  interface ForVerifier<OIN, IN> extends IntoPhraseFactory<OIN, IN> {

  }
}
