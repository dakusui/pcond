package com.github.dakusui.pcond.functions;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public enum Functions {
  ;

  private static final Function<?, ?>                        IDENTITY           = Printable.function("identity", Function.identity());
  private static final Function<?, String>                   STRINGIFY          = Printable.function("stringify", Object::toString);
  private static final Function<String, Integer>             LENGTH             = Printable.function("length", String::length);
  private static final Function<Collection<?>, Integer>      SIZE               = Printable.function("size", Collection::size);
  private static final Function<Collection<?>, Stream<?>>    STREAM             = Printable.function("stream", Collection::stream);
  private static final Function<Object[], List<?>>           ARRAY_TO_LIST      = Printable.function("arrayToList", Arrays::asList);
  private static final Function<String, Integer>             COUNT_LINES        = Printable.function("countLines", (String s) -> s.split("\n").length);
  private static final Function<Collection<?>, List<?>>      COLLECTION_TO_LIST = Printable.function("collectionToList", (Collection<?> c) -> new LinkedList<Object>() {
    {
      addAll(c);
    }
  });
  private static final PrintableFunction.Factory<List<?>, ?> ELEMENT_AT_FACTORY =
      Printable.printableFunctionFactory((v) -> String.format("at[%s]", v), arg -> es -> es.get((Integer) arg));
  private static final PrintableFunction.Factory<Object, ?>  CAST_FACTORY       = Printable.printableFunctionFactory(
      (v) -> String.format("castTo[%s]", ((Class<?>) requireNonNull(v)).getSimpleName()),
      arg -> ((Class<?>) arg)::cast);

  @SuppressWarnings("unchecked")
  public static <E> Function<E, E> identity() {
    return (Function<E, E>) IDENTITY;
  }

  @SuppressWarnings("unchecked")
  public static <E> Function<? super E, String> stringify() {
    return (Function<? super E, String>) STRINGIFY;
  }

  public static Function<? super String, Integer> length() {
    return LENGTH;
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <E> Function<List<? extends E>, ? extends E> elementAt(int i) {
    return Function.class.cast(ELEMENT_AT_FACTORY.create(i));
  }

  public static Function<? super Collection<?>, Integer> size() {
    return SIZE;
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <E> Function<Collection<? extends E>, Stream<? extends E>> stream() {
    return Function.class.cast(STREAM);
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <E> Function<? super Object, ? extends E> cast(Class<E> type) {
    return Function.class.cast(CAST_FACTORY.create(type));
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <I extends Collection<? extends E>, E> Function<I, List<E>> collectionToList() {
    return Function.class.cast(COLLECTION_TO_LIST);
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <E> Function<E[], List<E>> arrayToList() {
    return Function.class.cast(ARRAY_TO_LIST);
  }

  public static Function<String, Integer> countLines() {
    return COUNT_LINES;
  }
}
