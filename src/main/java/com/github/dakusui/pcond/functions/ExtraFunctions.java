package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.functions.currying.CurriedFunction;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class ExtraFunctions {
  public static Function<Stream<?>, Stream<Context>> nest(Collection<?> inner) {
    return Printables.function(() -> "nest" + formatObject(inner), (Stream<?> stream) -> Def.nest(stream, inner));
  }

  public static Function<Stream<?>, Stream<Context>> toContextStream() {
    return Printables.function(() -> "toContextStream", Def::toContextStream);
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

  public static Predicate<Context> test(CurriedFunction<Object, Object> curriedFunction, int... orderArgs) {
    return Def.applyAndTest(curriedFunction, Predicates.isTrue(), Boolean.class, orderArgs);
  }

  public static int[] normalizeOrderArgs(Context context, int[] orderArgs) {
    int[] order;
    if (orderArgs.length == 0)
      order = IntStream.range(0, context.size()).toArray();
    else
      order = orderArgs;
    return order;
  }

  public interface Context {
    default int size() {
      return values().size();
    }

    @SuppressWarnings("unchecked")
    default <T> T valueAt(int i) {
      return (T) values().get(i);
    }

    default Context append(Object o) {
      return () -> InternalUtils.append(values(), o);
    }

    List<Object> values();

    static Context from(Object o) {
      return () -> singletonList(o);
    }
  }

  enum Def {
    ;

    public static Stream<Context> nest(Stream<?> stream, Collection<?> inner) {
      return toContextStream(stream).flatMap(context -> inner.stream().map((Function<Object, Context>) context::append));
    }

    public static Stream<Context> toContextStream(Stream<?> stream) {
      return stream.map(o -> (Context) (o instanceof Context ? o : Context.from(o)));
    }

    static <R> Predicate<Context> applyAndTest(CurriedFunction<Object, Object> curriedFunction, Predicate<? super R> pred, @SuppressWarnings("SameParameterValue") Class<? extends R> type, int... orderArgs) {
      List<Object> spec = asList(curriedFunction, pred, asList(Arrays.stream(orderArgs).boxed().toArray()));
      return Printables.predicateFactory(
          (List<Object> e) -> String.format("%s(%s%s)", e.get(1), e.get(0), e.get(2)),
          (List<Object> e) -> (Context context) -> (pred).test(type.cast(apply(curriedFunction, orderArgs).apply(context)))).create(spec);
    }
  }

}
