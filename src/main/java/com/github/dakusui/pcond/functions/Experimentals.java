package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.core.context.Context;
import com.github.dakusui.pcond.core.context.ContextUtils;
import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.core.printable.ParameterizedFunctionFactory;
import com.github.dakusui.pcond.core.printable.ParameterizedPredicateFactory;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;

/**
 * A class that collects methods to create functions and predicates in experimental stage.
 */
public enum Experimentals {
  ;

  /**
   * This function is used to construct a function which replaces 'nested loop' in a usual programming.
   *
   * @param inner A collection for the "inner loop".
   * @return A function to construct a nested structure.
   */
  public static Function<Stream<?>, Stream<Context>> nest(Collection<?> inner) {
    return Printables.function(() -> "nest" + formatObject(inner), (Stream<?> stream) -> ContextUtils.nest(stream, inner));
  }

  /**
   * Returns a converter function for a stream.
   * The function converts a stream of objects into and returns a stream of contexts each of which hols a value
   * from the original stream.
   *
   * @return A function to convert an object stream into context stream.
   * @see Context
   */
  public static Function<Stream<?>, Stream<Context>> toContextStream() {
    return Printables.function(() -> "toContextStream", ContextUtils::toContextStream);
  }

  /**
   * Returns a function to convert a value into a context that holds the original value.
   *
   * @param <T> The type of the original value.
   * @return A function to convert a value into a context.
   */
  public static <T> Function<T, Context> toContext() {
    return Printables.function(() -> "toContext", ContextUtils::toContext);
  }

  /**
   * Creates a context function that tests the value at the specified index using the given predicate.
   *
   * @param predicate_ A predicate with which the value is tested.
   * @param argIndex   An index to specify a value in the context.
   * @param <T>        An expected type of value to be tested.
   * @return A new predicate to test a value in a context.
   */
  public static <T> Predicate<Context> toContextPredicate(Predicate<T> predicate_, int argIndex) {
    return PrintablePredicateFactory.contextPredicate(predicate_, argIndex);
  }

  /**
   * Converts a predicate to a context predicate which tests the first value in a context
   * using the `predicate`.
   *
   * @param predicate A predicate to be converted
   * @param <T>       An expected type of the input value.
   * @return A context predicate.
   */
  public static <T> Predicate<Context> toContextPredicate(Predicate<T> predicate) {
    return toContextPredicate(predicate, 0);
  }

  /**
   * Converts a curried function which results in a boolean value into a predicate.
   *
   * @param curriedFunction A curried function to be converted.
   * @param orderArgs       An array to specify the order in which values in the context are applied to the function.
   * @return A predicate converted from the given curried function.
   */
  public static Predicate<Context> toContextPredicate(CurriedFunction<Object, Object> curriedFunction, int... orderArgs) {
    return ContextUtils.toContextPredicate(curriedFunction, orderArgs);
  }

  /**
   * Returns a builder for a factory to create a predicate.
   * The factory accepts an argument to create a new predicate.
   * With this method, you can create predicates with different values.
   *
   * Suppose, you are about to create a predicate that tests a given string starts with `"hello"`.
   * At the same time, you also want to create a predicate that checks the value using `"こんにちは"`.
   * You can do it with the factory built by the returned value of this method.
   *
   * @param name The name of the predicate. It will be followed by the value passed to the factory in the `pcond`'s output.
   * @param <T>  The expected type of the value to be tested by the final predicate.
   * @return A builder to create a predicate factory.
   */
  public static <T> ParameterizedPredicateFactory.Builder<T> parameterizedPredicate(String name) {
    return new ParameterizedPredicateFactory.Builder<T>().name(name);
  }

  /**
   * Returns a builder for a factory to create a function.
   *
   * @param name The name of the function. It will be followed by the value passed to the factory in the `pcond`'s output.
   * @param <T>  The expected type of the input value to the final function.
   * @param <R>  The expected type of the output value of the final function.
   * @return A builder to create a function factory.
   * @see Experimentals#parameterizedPredicate(String)
   */
  public static <T, R> ParameterizedFunctionFactory.Builder<T, R> parameterizedFunction(String name) {
    return new ParameterizedFunctionFactory.Builder<T, R>().name(name);
  }
}
