package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.checkers.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface IntoPhraseFactory<OIN, IN> {
  default StringChecker<OIN> intoString() {
    return intoStringWith((IN v) -> (String) v);
  }

  default IntegerChecker<OIN> intoInteger() {
    return intoIntegerWith((IN v) -> (Integer) v);
  }

  default LongChecker<OIN> intoLong() {
    return intoLongWith((IN v) -> (Long) v);
  }

  default FloatChecker<OIN> intoFloat() {
    return intoFloatWith((IN v) -> (Float) v);
  }

  default ShortChecker<OIN> intoShort() {
    return intoShortWith((IN v) -> (Short) v);
  }

  default DoubleChecker<OIN> intoDouble() {
    return intoDoubleWith((IN v) -> (Double) v);
  }

  default BooleanChecker<OIN> intoBoolean() {
    return intoBooleanWith((IN v) -> (Boolean) v);
  }

  @SuppressWarnings("unchecked")
  default <NOUT> ObjectChecker<OIN, NOUT> intoObject() {
    return intoObjectWith((IN v) -> (NOUT) v);
  }

  @SuppressWarnings("unchecked")
  default <E> ListChecker<OIN, E> intoList() {
    return intoListWith((IN v) -> (List<E>) v);
  }

  @SuppressWarnings("unchecked")
  default <E> StreamChecker<OIN, E> intoStream() {
    return intoStreamWith((IN v) -> (Stream<E>) v);
  }

  StringChecker<OIN> intoStringWith(Function<IN, String> function);

  IntegerChecker<OIN> intoIntegerWith(Function<IN, Integer> function);

  LongChecker<OIN> intoLongWith(Function<IN, Long> function);

  ShortChecker<OIN> intoShortWith(Function<IN, Short> function);

  DoubleChecker<OIN> intoDoubleWith(Function<IN, Double> function);

  FloatChecker<OIN> intoFloatWith(Function<IN, Float> function);

  BooleanChecker<OIN> intoBooleanWith(Function<IN, Boolean> function);

  <OUT> ObjectChecker<OIN, OUT> intoObjectWith(Function<IN, OUT> function);

  <E> ListChecker<OIN, E> intoListWith(Function<IN, List<E>> function);

  <E> StreamChecker<OIN, E> intoStreamWith(Function<IN, Stream<E>> function);

  interface ForVerifier<OIN, IN> extends IntoPhraseFactory<OIN, IN> {

  }
}
