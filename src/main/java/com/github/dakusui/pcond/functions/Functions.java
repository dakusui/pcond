package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.core.currying.CurryingUtils;
import com.github.dakusui.pcond.core.currying.ReflectionsUtils;
import com.github.dakusui.pcond.core.multi.MultiFunction;
import com.github.dakusui.pcond.core.preds.BaseFuncUtils;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.core.refl.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.refl.ReflUtils.invokeMethod;
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
    return curry(functionForStaticMethod(aClass, methodName, parameterTypes));
  }

  /**
   * Curries a given multi-function.
   *
   * @param function A multi-function to be curried
   * @return A curried function
   * @see Functions#curry(Class, String, Class[])
   */
  public static CurriedFunction<Object, Object> curry(MultiFunction<Object> function) {
    return CurryingUtils.curry(function);
  }

  public static <R> MultiFunction<R> functionForStaticMethod(Class<?> aClass, String methodName, Class<?>... parameterTypes) {
    return ReflectionsUtils.lookupFunctionForStaticMethod(IntStream.range(0, parameterTypes.length).toArray(), aClass, methodName, parameterTypes);
  }

  /**
   * Returns a {@link Function} created from a method specified by a {@code methodQuery}.
   * If the {@code methodQuery} matches none or more than one methods, a {@code RuntimeException} will be thrown.
   *
   * @param methodQuery A query object that specifies a method to be invoked by the returned function.
   * @param <T>         the type of the input to the returned function
   * @return Created function.
   * @see Functions#classMethod(Class, String, Object[])
   * @see Functions#instanceMethod(Object, String, Object[])
   */
  public static <T, R> Function<T, R> call(MethodQuery methodQuery) {
    return Printables.function(methodQuery.describe(), t -> invokeMethod(methodQuery.bindActualArguments((o) -> o instanceof Parameter, o -> t)));
  }


  /**
   * // @formatter:off
   * Creates a {@link MethodQuery} object from given arguments to search for {@code static} methods.
   * Note that {@code arguments} are actual argument values, not the formal parameter types.
   * The pcond library searches for the "best" matching method for you.
   * In case no matching method is found or more than one methods are found, a {@link RuntimeException}
   * will be thrown.
   *
   * In order to specify a parameter which should be passed to the returned function at applying,
   * you can use an object returned by {@link Functions#parameter} method.
   * This is useful to construct a function from an existing method.
   *
   * That is, in order to create a function which computes sin using query a method {@link Math#sin(double)},
   * you can do following
   * [source, java]
   * ----
   * public void buildSinFunction() {
   *   MethodQuery mq = classMethod(Math.class, "sin", parameter());
   *   Function<Double, Double> sin = call(mq);
   *   System.out.println(sin(Math.PI/2));
   * }
   * ----
   * This prints {@code 1.0}.
   *
   * In case your arguments do not contain any {@link Parameter} object, the input
   * argument passed to the built function will be simply ignored.
   *
   * // @formatter:on
   *
   *
   * @param targetClass A class
   * @param methodName  A method name
   * @param arguments   Arguments
   * @return A method query for static methods specified by arguments.
   *
   * @see com.github.dakusui.pcond.core.refl.ReflUtils#findMethod(Class, String, Object[])
   */
  public static MethodQuery classMethod(Class<?> targetClass, String methodName, Object... arguments) {
    return MethodQuery.classMethod(targetClass, methodName, arguments);
  }

  /**
   * // @formatter:off
   * Creates a {@link MethodQuery} object from given arguments to search for {@code static} methods.
   * Excepting that this method returns a query for instance methods, it is quite
   * similar to {@link Functions#classMethod( Class, String, Object[])}.
   *
   * This method is useful to build a function from an instance method.
   * That is, you can create a function which returns the length of a given string
   * from a method {@link String#length()} with a following code snippet.
   *
   * [source, java]
   * ----
   * public void buildLengthFunction() {
   *   Function<String, Integer> length = call(instanceMethod(parameter(), "length"));
   * }
   * ----
   *
   * In case the {@code targetObject} is not an instance of {@link Parameter} and {@code arguments}
   * contain no {@code Parameter} object, the function will simply ignore the input passed to it.
   *
   * // @formatter:on
   *
   * @param targetObject An object on which methods matching returned query should be invoked.
   * @param methodName A name of method.
   * @param arguments Arguments passed to the method.
   * @return A method query for instance methods specified by arguments.
   *
   * @see Functions#classMethod(Class, String, Object[])
   */
  public static MethodQuery instanceMethod(Object targetObject, String methodName, Object... arguments) {
    return MethodQuery.instanceMethod(targetObject, methodName, arguments);
  }

  /**
   * Returns a {@link Parameter} object, which is used in combination with {@link Functions#instanceMethod(Object, String, Object[])}
   * or {@link Functions#classMethod(Class, String, Object[])}.
   * This object is replaced with the actual input value passed to a function built
   * through {@link Functions#call(MethodQuery)} or {@link Predicates#callp(MethodQuery)}
   * when it is applied.
   *
   * @return a {@code Parameter} object
   */
  public static Parameter parameter() {
    return Parameter.INSTANCE;
  }

  /**
   * // @formatter:off
   * A short hand method to call
   * [source, java]
   * ---
   * call(instanceMethod(object, methodName, args))
   * ---
   * // @formatter:on
   *
   * @param targetObject An object on which methods matching returned query should be invoked.
   * @param methodName A name of method.
   * @param arguments Arguments passed to the method.
   * @param <T> The type of the input to the returned function.
   * @param <R> The type of the output from the returned function.
   * @return The function that calls a method matching a query built from the given arguments.
   *
   * @see Functions#call(MethodQuery)
   * @see Functions#instanceMethod(Object, String, Object[])
   */
  private static <T, R> Function<T, R> callInstanceMethod(Object targetObject, String methodName, Object... arguments) {
    return call(instanceMethod(targetObject, methodName, arguments));
  }

  /**
   * Returns a function that calls a method which matches the given {@code methodName}
   * and {@code args} on the object given as input to it.
   * <p>
   * Note that method look up is done when the function is applied.
   * <p>
   * Note that method look up is done when the predicate is applied.
   * This means this method does not throw any exception by itself and in case
   * you give wrong {@code methodName} or {@code arguments}, an exception will be
   * thrown when the returned function is applied.
   *
   * @param methodName The method name
   * @param arguments  Arguments passed to the method.
   * @param <T>        The type of input to the returned function
   * @param <R>        The type of output from the returned function
   * @return A function that invokes the method matching the {@code methodName} and {@code args}
   */
  public static <T, R> Function<T, R> chain(String methodName, Object... arguments) {
    return callInstanceMethod(parameter(), methodName, arguments);
  }

  /**
   * Returns a predicate that calls a method which matches the given {@code methodName}
   * and {@code args} on the object given as input to it.
   * <p>
   * Note that method look up is done when the predicate is applied.
   * This means this method does not throw any exception by itself and in case
   * you give wrong {@code methodName} or {@code arguments}, an exception will be
   * thrown when the returned function is applied.
   *
   * @param methodName The method name
   * @param arguments  Arguments passed to the method.
   * @param <T>        The type of input to the returned predicate
   * @return A predicate that invokes the method matching the {@code methodName} and {@code args}
   */
  public static <T> Predicate<T> chainp(String methodName, Object... arguments) {
    return Predicates.callp(instanceMethod(parameter(), methodName, arguments));
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
