package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.checkers.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Provides methods to convert a matcher into a checker.
 *
 * @param <OIN> A type of original input value.
 * @param <IN> A type of current input value.
 */
public interface IntoPhraseFactory<OIN, IN> {
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

  interface ForChecker<OIN, IN> extends IntoPhraseFactory<OIN, IN> {

  }
}
