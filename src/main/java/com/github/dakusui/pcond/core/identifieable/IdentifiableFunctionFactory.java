package com.github.dakusui.pcond.core.identifieable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

public enum IdentifiableFunctionFactory {
  COMPOSE,
  ;

  public enum Simple {
    IDENTITY("identity", Function.identity()),
    STRINGIFY("stringify", Object::toString),
    LENGTH("length", (Function<String, Integer>) String::length),
    SIZE("size", (Function<Collection<?>, Integer>) Collection::size),
    STREAM("stream", (Function<Collection<?>, Stream<?>>) Collection::stream),
    STREAM_OF("streamOf", Stream::of),
    ARRAY_TO_LIST("arrayToList", (Function<Object[], List<Object>>) Arrays::asList),
    COUNT_LINES("countLines", (String v) -> v.split(String.format("%n")).length),
    COLLECTION_TO_LIST("collectionToList", (Collection<?> c) -> new ArrayList<Object>() {
      {
        addAll(c);
      }
    }),
    ;
    private final Function<?, ?> instance;

    Simple(String name, Function<?, ?> function) {
      instance = IdentifiableFunctionFactory.function(() -> name, function, this);
    }

    @SuppressWarnings("unchecked")
    public <T, R> Function<T, R> instance() {
      return (Function<T, R>) this.instance;
    }
  }

  public enum Parameterized {
    ELEMENT_AT((args) -> () -> format("at[%s]", args.get(0)), (args) -> (List<?> v) -> v.get((int) args.get(0))),
    CAST((args) -> () -> format("castTo[%s]", requireNonNull((Class<?>) args.get(0)).getSimpleName()), (args) -> (Object v) -> ((Class<?>) args.get(0)).cast(v)),
    ;
    final Function<List<Object>, Supplier<String>> formatterFactory;
    final Function<List<Object>, Function<?, ?>>   functionFactory;

    Parameterized(Function<List<Object>, Supplier<String>> formatterFactory, Function<List<Object>, Function<?, ?>> functionFactory) {
      this.formatterFactory = formatterFactory;
      this.functionFactory = functionFactory;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, R> Function<T, R> create(List<Object> args) {
      return IdentifiableFunctionFactory.create(this, args, this.formatterFactory, (Function) this.functionFactory);
    }
  }

  public static <T, R, S> PrintableFunction<T, S> compose(Function<? super T, ? extends R> before, Function<? super R, ? extends S> after) {
    PrintableFunction<? super T, ? extends R> before_ = toPrintableFunction(before);
    PrintableFunction<? super R, ? extends S> after_ = toPrintableFunction(after);
    return new PrintableFunction<>(
        COMPOSE,
        asList(before_, after_),
        () -> format("%s->%s", before, after),
        (T v) -> PrintableFunction.unwrap(after).apply(PrintableFunction.unwrap(before).apply(v)),
        before_,
        after_);
  }

  public static <T, R> Function<T, R> function(Supplier<String> formatter, Function<T, R> function) {
    return function(formatter, function, IdentifiableFunctionFactory.class);
  }

  private static <T, R> Function<T, R> function(Supplier<String> formatter, Function<T, R> function, Object fallBackCreator) {
    return new PrintableFunction<>(
        creatorOf(function).orElse(fallBackCreator),
        argsOf(function),
        formatter,
        function);
  }

  public static <T, R> PrintableFunction<T, R> create(
      Object creator,
      List<Object> args,
      Function<List<Object>, Supplier<String>> formatterFactory,
      Function<List<Object>, Function<T, R>> functionFactory) {
    return new PrintableFunction<>(creator, args, formatterFactory.apply(args), functionFactory.apply(args));
  }

  private static <T, R> PrintableFunction<T, R> toPrintableFunction(Function<T, R> function) {
    if (function instanceof PrintableFunction)
      return (PrintableFunction<T, R>) function;
    return new PrintableFunction<>(IdentifiableFunctionFactory.class, singletonList(function), () -> "noname:" + function, function);
  }

  public static Optional<Object> creatorOf(Object object) {
    Optional<Object> ret = Optional.empty();
    if (object instanceof Identifiable)
      ret = Optional.of(((Identifiable) object).creator());
    return ret;
  }

  public static List<Object> argsOf(Object object) {
    List<Object> ret = emptyList();
    if (object instanceof Identifiable)
      ret = ((Identifiable) object).args();
    return ret;
  }
}
