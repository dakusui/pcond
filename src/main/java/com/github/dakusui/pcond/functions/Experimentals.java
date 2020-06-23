package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.core.currying.CurriedFunction;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.util.Arrays.asList;

public enum Experimentals {
  ;

  public static Function<Stream<?>, Stream<Context>> nest(Collection<?> inner) {
    return Printables.function(() -> "nest" + formatObject(inner), (Stream<?> stream) -> Def.nest(stream, inner));
  }

  public static Function<Stream<?>, Stream<Context>> toContextStream() {
    return Printables.function(() -> "toContextStream", Def::toContextStream);
  }

  public static <T> Function<T, Context> toContext() {
    return Printables.function(() -> "toContext", Def::toContext);
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

  enum Def {
    ;

    public static Stream<Context> nest(Stream<?> stream, Collection<?> inner) {
      return toContextStream(stream).flatMap(context -> inner.stream().map((Function<Object, Context>) context::append));
    }

    public static Stream<Context> toContextStream(Stream<?> stream) {
      return stream.map(Def::toContext);
    }

    public static <T> Context toContext(T t) {
      return t instanceof Context ? (Context) t : Context.from(t);
    }

    static <R> Predicate<Context> applyAndTest(CurriedFunction<Object, Object> curriedFunction, Predicate<? super R> pred, @SuppressWarnings("SameParameterValue") Class<? extends R> type, int... orderArgs) {
      List<Object> spec = asList(curriedFunction, pred, asList(Arrays.stream(orderArgs).boxed().toArray()));
      return Printables.predicateFactory(
          (List<Object> e) -> String.format("%s(%s%s)", e.get(1), e.get(0), e.get(2)),
          (List<Object> e) -> (Context context) -> (pred).test(type.cast(apply(curriedFunction, orderArgs).apply(context)))).create(spec);
    }
  }

}
