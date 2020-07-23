package com.github.dakusui.pcond.internals;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum InternalChecks {
  ;

  public static void checkArgument(boolean b, Supplier<String> messageFormatter) {
    if (!b)
      throw new IllegalArgumentException(messageFormatter.get());
  }

  public static <V> V requireArgument(V arg, Predicate<? super V> predicate, Supplier<String> messageFormatter) {
    if (!predicate.test(arg))
      throw new IllegalArgumentException(messageFormatter.get());
    return arg;
  }

  public static <V> V ensureValue(V value, Predicate<? super V> predicate, Function<V, String> messageFormatter) {
    if (!predicate.test(value))
      throw new IllegalStateException(messageFormatter.apply(value));
    return value;
  }

  public static <V> V requireState(V arg, Predicate<? super V> predicate, Supplier<String> messageFormatter) {
    if (!predicate.test(arg))
      throw new IllegalStateException(messageFormatter.get());
    return arg;
  }

  public static Method requireStaticMethod(Method method) {
    if (!Modifier.isStatic(method.getModifiers()))
      throw new IllegalArgumentException(String.format("The specified method '%s' is not static", method));
    return method;
  }

  public static List<Object> requireArgumentListSize(List<Object> args, int requiredSize) {
    if (requiredSize != args.size())
      throw new IllegalArgumentException(String.format("Wrong number of arguments are given: required: %s, actual: %s", requiredSize, args.size()));
    return args;
  }
}
