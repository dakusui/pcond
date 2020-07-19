package com.github.dakusui.pcond.core.preds;

import com.github.dakusui.pcond.core.context.Context;
import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.functions.Experimentals;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.functions.Printables;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.functions.Printables.predicate;
import static java.util.Arrays.asList;

public enum ContextUtils {
  ;

  public static Stream<Context> nest(Stream<?> stream, Collection<?> inner) {
    return toContextStream(stream).flatMap(context -> inner.stream().map((Function<Object, Context>) context::append));
  }

  public static Stream<Context> toContextStream(Stream<?> stream) {
    return stream.map(ContextUtils::toContext);
  }

  public static <T> Context toContext(T t) {
    return t instanceof Context ? (Context) t : Context.from(t);
  }

  public static <R> Predicate<Context> applyAndTest(CurriedFunction<Object, Object> curriedFunction, Predicate<? super R> pred, @SuppressWarnings("SameParameterValue") Class<? extends R> type, int... orderArgs) {
    List<Object> spec = asList(curriedFunction, pred, asList(Arrays.stream(orderArgs).boxed().toArray()));
    return Printables.predicateFactory(
        (List<Object> e) -> String.format("%s(%s%s)", e.get(1), e.get(0), e.get(2)),
        (List<Object> e) -> (Context context) -> (pred).test(type.cast(Experimentals.apply(curriedFunction, orderArgs).apply(context)))).create(spec);
  }

  public static Predicate<Context> toContextPredicate(CurriedFunction<Object, Object> curriedFunction, int... orderArgs) {
    return ContextUtils.applyAndTest(curriedFunction, Printables.predicate("contextPredicate", Predicates.isTrue()), Boolean.class, orderArgs);
  }
}
