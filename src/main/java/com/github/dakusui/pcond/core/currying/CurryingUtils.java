package com.github.dakusui.pcond.core.currying;

import com.github.dakusui.pcond.core.context.Context;
import com.github.dakusui.pcond.core.identifieable.IdentifiableFunctionFactory;
import com.github.dakusui.pcond.core.multi.MultiFunction;
import com.github.dakusui.pcond.core.preds.BaseFuncUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * Intended for internal use only.
 */
public enum CurryingUtils {
  ;
  private static final ThreadLocal<Function<List<Object>, CurriedFunction<Object, Object>>> CURRIED_FUNCTION_FACTORY_POOL = new ThreadLocal<>();

  public static CurriedFunction<Object, Object> curry(MultiFunction<Object> function) {
    return curry(function, emptyList());
  }

  public static Function<List<Object>, CurriedFunction<Object, Object>> currier() {
    if (CURRIED_FUNCTION_FACTORY_POOL.get() == null)
      CURRIED_FUNCTION_FACTORY_POOL.set((List<Object> args) ->
          IdentifiableFunctionFactory.create(
              CurryingUtils.class,
              args,
              (args_) -> () -> FormattingUtils.functionNameFormatter(functionName(args_), ongoingContext(args_)).apply(function(args_)),
              (args_) -> new CurriedFunction.Impl(function(args_), ongoingContext(args_))));
    return CURRIED_FUNCTION_FACTORY_POOL.get();
  }

  public static <R> Function<Context, R> applyCurriedFunction(CurriedFunction<Object, Object> curriedFunction, int... orderArgs) {
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

  static CurriedFunction<Object, Object> curry(MultiFunction<Object> function, List<? super Object> ongoingContext) {
    return currier().apply(asList(function.name(), function, ongoingContext));
  }

  private static String functionName(List<Object> args) {
    return (String) args.get(0);
  }

  @SuppressWarnings("unchecked")
  private static MultiFunction<Object> function(List<Object> args) {
    return (MultiFunction<Object>) args.get(1);
  }

  @SuppressWarnings("unchecked")
  private static List<? super Object> ongoingContext(List<Object> args) {
    return (List<? super Object>) args.get((2));
  }
}
