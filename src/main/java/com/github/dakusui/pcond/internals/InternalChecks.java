package com.github.dakusui.pcond.internals;

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

  public static <V> V requireState(V arg, Predicate<? super V> predicate, Supplier<String> messageFormatter) {
    if (!predicate.test(arg))
      throw new IllegalStateException(messageFormatter.get());
    return arg;
  }
}
