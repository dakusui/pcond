package com.github.dakusui.pcond.core.identifieable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public enum IdentifiableFunctionFactory {
  COMPOSE {
    public void hello() {

    }
  },
  ELEMENT_AT;

  public static <T, R, S> PrintableFunction<T, S> compose(Function<? super T, ? extends R> before, Function<? super R, ? extends S> after) {
    return new PrintableFunction<>(
        COMPOSE,
        asList(toPrintableFunction(before), toPrintableFunction(after)),
        () -> format("%s->%s", before, after),
        (T v) -> PrintableFunction.unwrap(after).apply(PrintableFunction.unwrap(before).apply(v)));
  }

  public static <T, R> Function<T, R> function(Supplier<String> formatter, Function<T, R> function) {
    return new PrintableFunction<>(
        creatorOf(function).orElse(Identifiable.class),
        argsOf(function),
        formatter,
        function);
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

  private static <T, R> Optional<Object> creatorOf(Function<T, R> function) {
    Optional<Object> ret = Optional.empty();
    if (function instanceof PrintableFunction)
      ret = Optional.of(((PrintableFunction<T, R>) function).creator());
    return ret;
  }

  private static <T, R> List<Object> argsOf(Function<T, R> function) {
    List<Object> ret = emptyList();
    if (function instanceof PrintableFunction)
      ret = ((PrintableFunction<T, R>) function).args();
    return ret;
  }
}
