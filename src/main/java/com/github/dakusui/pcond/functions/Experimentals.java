package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.core.context.Context;
import com.github.dakusui.pcond.core.preds.ContextUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;
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

  public static <R> Function<Context, R> apply(CurriedFunction<Object, Object> curriedFunction, int... orderArgs) {
    return context -> {
      CurriedFunction<?, ?> cur = curriedFunction;
      int[] normalizedOrderArgs = normalizeOrderArgs(context, orderArgs);
      for (int i = 0; i < normalizedOrderArgs.length - 1; i++)
        cur = cur.applyNext(context.valueAt(normalizedOrderArgs[i]));
      return cur.applyLast(context.valueAt(normalizedOrderArgs[context.size() - 1]));
    };
  }

  public static int[] normalizeOrderArgs(Context context, int[] orderArgs) {
    int[] order;
    if (orderArgs.length == 0)
      order = IntStream.range(0, context.size()).toArray();
    else
      order = orderArgs;
    return order;
  }

}
