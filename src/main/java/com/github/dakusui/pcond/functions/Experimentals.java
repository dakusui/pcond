package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.core.context.Context;
import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;
import com.github.dakusui.pcond.core.context.ContextUtils;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;

public enum Experimentals {
  ;

  public static Function<Stream<?>, Stream<Context>> nest(Collection<?> inner) {
    return Printables.function(() -> "nest" + formatObject(inner), (Stream<?> stream) -> ContextUtils.nest(stream, inner));
  }

  public static Function<Stream<?>, Stream<Context>> toContextStream() {
    return Printables.function(() -> "toContextStream", ContextUtils::toContextStream);
  }

  public static <T> Function<T, Context> toContext() {
    return Printables.function(() -> "toContext", ContextUtils::toContext);
  }

  public static <T> Predicate<Context> toContextPredicate(Predicate<T> predicate_, int argIndex) {
    return PrintablePredicateFactory.contextPredicate(predicate_, argIndex);
  }

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
}
