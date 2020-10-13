package com.github.dakusui.pcond.core.identifieable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public enum IdentifiableFunctionFactory {
  COMPOSE,
  ELEMENT_AT;

  public static <T, R, S> PrintableFunction<T, S> compose(Function<? super T, ? extends R> before, Function<? super R, ? extends S> after) {
    return new PrintableFunction<>(
        COMPOSE,
        asList(toPrintableFunction(before), toPrintableFunction(after)),
        () -> format("%s->%s", before, after),
        (T v) -> unwrapIfPrintableFunction(after).apply(unwrapIfPrintableFunction(before).apply(v)));
  }

  public static <T, R> PrintableFunction<T, R> create(Object creator, List<Object> args, Function<List<Object>, Supplier<String>> formatterFactory, Function<List<Object>, Function<T, R>> functionFactory) {
    return new PrintableFunction<>(creator, args, formatterFactory.apply(args), functionFactory.apply(args));
  }

  public static <E> PrintableFunction<List<? extends E>, ? extends E> elementAt(int index) {
    return create(ELEMENT_AT,
        singletonList(index),
        args -> () -> String.format("elementAt[%s]", args.get(0)),
        args -> v -> v.get((int) args.get(0)));
  }

  private static <T, R> PrintableFunction<T, R> toPrintableFunction(Function<T, R> function) {
    if (function instanceof PrintableFunction)
      return (PrintableFunction<T, R>) function;
    return new PrintableFunction<>(IdentifiableFunctionFactory.class, singletonList(function), () -> "noname:" + function, function);
  }

  @SuppressWarnings("unchecked")
  private static <T, R> Function<T, R> unwrapIfPrintableFunction(Function<T, R> function) {
    Function<T, R> ret = function;
    if (function instanceof PrintableFunction)
      ret = unwrapIfPrintableFunction((Function<T, R>) ((PrintableFunction<T, R>) function).function);
    return ret;
  }
}
