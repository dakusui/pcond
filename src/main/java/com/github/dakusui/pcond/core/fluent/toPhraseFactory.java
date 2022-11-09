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
public interface toPhraseFactory<OIN, IN> {
  StringChecker<OIN> toStringWith(Function<IN, String> function);

  IntegerChecker<OIN> toIntegerWith(Function<IN, Integer> function);

  LongChecker<OIN> toLongWith(Function<IN, Long> function);

  ShortChecker<OIN> toShortWith(Function<IN, Short> function);

  DoubleChecker<OIN> toDoubleWith(Function<IN, Double> function);

  FloatChecker<OIN> toFloatWith(Function<IN, Float> function);

  BooleanChecker<OIN> toBooleanWith(Function<IN, Boolean> function);

  <OUT> ObjectChecker<OIN, OUT> toObjectWith(Function<IN, OUT> function);

  <E> ListChecker<OIN, E> toListWith(Function<IN, List<E>> function);

  <E> StreamChecker<OIN, E> toStreamWith(Function<IN, Stream<E>> function);

  interface ForChecker<OIN, IN> extends toPhraseFactory<OIN, IN> {

  }
}
