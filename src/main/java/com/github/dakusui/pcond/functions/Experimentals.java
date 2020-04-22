package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

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

  /**
   * Converts a curried function which results in a boolean value in to a predicate.
   *
   * @param curriedFunction A curried function to be converted.
   * @param orderArgs       An array to specify the order in which values in the context are applied to the function.
   * @return A predicate converted from the given curried function.
   */
  public static Predicate<Context> toContextPredicate(CurriedFunction<Object, Object> curriedFunction, int... orderArgs) {
    return Def.applyAndTest(curriedFunction, Printables.predicate("contextPredicate", Predicates.isTrue()), Boolean.class, orderArgs);
  }

  public static <T> Predicate<Context> toContextPredicate(Predicate<T> predicate_, int argIndex) {
    Evaluable<?> enclosed = InternalUtils.toEvaluableIfNecessary(predicate_);
    class ContextPrintablePredicate extends PrintablePredicate<Context> implements Evaluable.ContextPred {
      protected ContextPrintablePredicate() {
        super(
            () -> String.format("contextPredicate[%s,%s]", predicate_, argIndex),
            context -> predicate_.test(context.<T>valueAt(argIndex)));
      }

      @SuppressWarnings("unchecked")
      @Override
      public <TT> Evaluable<? super TT> enclosed() {
        return (Evaluable<? super TT>) enclosed;
      }

      @Override
      public int argIndex() {
        return argIndex;
      }
    }
    return new ContextPrintablePredicate();
  }

  public static <T> Predicate<Context> toContextPredicate(Predicate<T> predicate) {
    return toContextPredicate(predicate, 0);
  }

  public static int[] normalizeOrderArgs(Context context, int[] orderArgs) {
    int[] order;
    if (orderArgs.length == 0)
      order = IntStream.range(0, context.size()).toArray();
    else
      order = orderArgs;
    return order;
  }

  public interface Context extends Formattable {
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

    @Override
    default void formatTo(Formatter formatter, int flags, int width, int precision) {
      formatter.format("context:%s", this.values());
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
