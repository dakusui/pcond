package com.github.dakusui.pcond.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public enum Functions {
  ;

  @SuppressWarnings("unchecked")
  public static <E> Function<E, E> identity() {
    return (Function<E, E>) Def.IDENTITY;
  }

  @SuppressWarnings("unchecked")
  public static <E> Function<? super E, String> stringify() {
    return (Function<? super E, String>) Def.STRINGIFY;
  }

  public static Function<? super String, Integer> length() {
    return Def.LENGTH;
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <E> Function<List<? extends E>, ? extends E> elementAt(int i) {
    return Function.class.cast(Def.ELEMENT_AT_FACTORY.create(i));
  }

  public static Function<? super Collection<?>, Integer> size() {
    return Def.SIZE;
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <E> Function<Collection<? extends E>, Stream<? extends E>> stream() {
    return Function.class.cast(Def.STREAM);
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <E> Function<? super Object, ? extends E> cast(Class<E> type) {
    return Function.class.cast(Def.CAST_FACTORY.create(type));
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <I extends Collection<? extends E>, E> Function<I, List<E>> collectionToList() {
    return Function.class.cast(Def.COLLECTION_TO_LIST);
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <E> Function<E[], List<E>> arrayToList() {
    return Function.class.cast(Def.ARRAY_TO_LIST);
  }

  public static Function<String, Integer> countLines() {
    return Def.COUNT_LINES;
  }

  enum Def {
    ;
    private static final Function<?, ?>                                 IDENTITY           = Printables.function("identity", Function.identity());
    private static final Function<?, String>                            STRINGIFY          = Printables.function("stringify", Object::toString);
    private static final Function<String, Integer>                      LENGTH             = Printables.function("length", String::length);
    private static final Function<Collection<?>, Integer>               SIZE               = Printables.function("size", Collection::size);
    private static final Function<Collection<?>, Stream<?>>             STREAM             = Printables.function("stream", Collection::stream);
    private static final Function<Object[], List<?>>                    ARRAY_TO_LIST      = Printables.function("arrayToList", Arrays::asList);
    private static final Function<String, Integer>                      COUNT_LINES        = Printables.function("countLines", (String s) -> s.split("\n").length);
    private static final Function<Collection<?>, List<?>>               COLLECTION_TO_LIST = Printables.function("collectionToList", (Collection<?> c) -> new ArrayList<Object>() {
      {
        addAll(c);
      }
    });
    private static final PrintableFunction.Factory<List<?>, ?, Integer> ELEMENT_AT_FACTORY =
        Printables.functionFactory((v) -> String.format("at[%s]", v), arg -> es -> es.get((Integer) arg));
    private static final PrintableFunction.Factory<Object, ?, Class<?>> CAST_FACTORY       = Printables.functionFactory(
        (v) -> String.format("castTo[%s]", requireNonNull(v).getSimpleName()), arg -> arg::cast);
  }
}
