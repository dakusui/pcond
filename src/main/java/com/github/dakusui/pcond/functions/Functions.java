package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.core.MultiParameterFunction;
import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.core.currying.CurryingUtils;
import com.github.dakusui.pcond.core.currying.ReflectionsUtils;
import com.github.dakusui.pcond.functions.preds.BaseFuncUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * An entry point for acquiring function objects.
 * Functions retrieved by method in this class are all "printable".
 */
public enum Functions {
  ;

  /**
   * Returns a printable function that returns a given object itself.
   *
   * @param <E> The type of the object.
   * @return The function.
   */
  @SuppressWarnings("unchecked")
  public static <E> Function<E, E> identity() {
    return (Function<E, E>) Def.IDENTITY;
  }

  /**
   * Returns a function that gives a string representation of a object given to it.
   *
   * @param <E> The type of the object
   * @return The function.
   */
  @SuppressWarnings("unchecked")
  public static <E> Function<? super E, String> stringify() {
    return (Function<? super E, String>) Def.STRINGIFY;
  }

  /**
   * Returns a function that gives a length of a string passed as an argument.
   *
   * @return The function.
   */
  public static Function<? super String, Integer> length() {
    return Def.LENGTH;
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <E> Function<List<? extends E>, ? extends E> elementAt(int i) {
    return Function.class.cast(Def.ELEMENT_AT_FACTORY.create(i));
  }

  /**
   * Returns a function that that returns a size of a given list.
   *
   * @return The function.
   */
  public static Function<? super Collection<?>, Integer> size() {
    return Def.SIZE;
  }

  /**
   * Returns a function that returns a stream for a given given collection.
   *
   * @param <E> Type of elements in the given collection.
   * @return The function.
   */
  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <E> Function<Collection<? extends E>, Stream<? extends E>> stream() {
    return Function.class.cast(Def.STREAM);
  }

  /**
   * Returns a function that returns a stream for a given given collection.
   *
   * @param <E> Type of elements in the given collection.
   * @return The function.
   */
  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <E> Function<E, Stream<? extends E>> streamOf() {
    return Function.class.cast(Def.STREAM_OF);
  }

  /**
   * Returns a function that casts an object into a given class.
   *
   * @param type The type to which the given object is cast
   * @param <E>  The type to which the object is case.
   * @return The function.
   */
  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <E> Function<? super Object, ? extends E> cast(Class<E> type) {
    return Function.class.cast(Def.CAST_FACTORY.create(type));
  }

  /**
   * Returns a function that creates and returns a list that contains all the elements in the given list.
   *
   * @param <I> The type of the input collection.
   * @param <E> Type of the elements in the collection
   * @return The function.
   */
  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <I extends Collection<? extends E>, E> Function<I, List<E>> collectionToList() {
    return Function.class.cast(Def.COLLECTION_TO_LIST);
  }

  /**
   * Returns a function that converts a given array into a list.
   *
   * @param <E> Type of elements in a given array.
   * @return The function.
   */
  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <E> Function<E[], List<E>> arrayToList() {
    return Function.class.cast(Def.ARRAY_TO_LIST);
  }

  /**
   * Returns a function the counts lines in a given string.
   *
   * @return The function.
   */
  public static Function<String, Integer> countLines() {
    return Def.COUNT_LINES;
  }

  /**
   * https://en.wikipedia.org/wiki/Currying[Curries] a static method specified by the given arguments.
   *
   * @param aClass         A class to which the method to be curried belongs to.
   * @param methodName     A name of the method to be curried.
   * @param parameterTypes Parameters types of the method.
   * @return A printable and curried function of the target method.
   */
  public static CurriedFunction<Object, Object> curry(Class<?> aClass, String methodName, Class<?>... parameterTypes) {
    return curry(methodName, functionForStaticMethod(aClass, methodName, parameterTypes));
  }

  public static CurriedFunction<Object, Object> curry(String functionName, MultiParameterFunction<Object> function) {
    return CurryingUtils.curry(functionName, function);
  }

  public static <R> MultiParameterFunction<R> functionForStaticMethod(Class<?> aClass, String methodName, Class<?>... parameterTypes) {
    return ReflectionsUtils.lookupFunctionForStaticMethod(IntStream.range(0, parameterTypes.length).toArray(), aClass, methodName, parameterTypes);
  }

  enum Def {
    ;
    private static final Function<?, ?>                             IDENTITY           = Printables.function("identity", Function.identity());
    private static final Function<?, String>                        STRINGIFY          = Printables.function("stringify", Object::toString);
    private static final Function<String, Integer>                  LENGTH             = Printables.function("length", String::length);
    private static final Function<Collection<?>, Integer>           SIZE               = Printables.function("size", Collection::size);
    private static final Function<Collection<?>, Stream<?>>         STREAM             = Printables.function("stream", Collection::stream);
    private static final Function<?, Stream<?>>                     STREAM_OF          = Printables.function("streamOf", Stream::of);
    private static final Function<Object[], List<?>>                ARRAY_TO_LIST      = Printables.function("arrayToList", Arrays::asList);
    private static final Function<String, Integer>                  COUNT_LINES        = Printables.function("countLines", (String s) -> s.split(String.format("%n")).length);
    private static final Function<Collection<?>, List<?>>           COLLECTION_TO_LIST = Printables.function("collectionToList", (Collection<?> c) -> new ArrayList<Object>() {
      {
        addAll(c);
      }
    });
    private static final BaseFuncUtils.Factory<List<?>, ?, Integer> ELEMENT_AT_FACTORY =
        Printables.functionFactory((Integer v) -> String.format("at[%s]", v), (Integer arg) -> (List<?> es) -> es.get((Integer) arg));
    private static final BaseFuncUtils.Factory<Object, ?, Class<?>> CAST_FACTORY       = Printables.functionFactory(
        (v) -> String.format("castTo[%s]", requireNonNull(v).getSimpleName()), arg -> arg::cast);
  }
}
