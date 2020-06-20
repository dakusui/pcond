package com.github.dakusui.pcond.core.currying;

import com.github.dakusui.pcond.core.MultiParameterFunction;
import com.github.dakusui.pcond.functions.Printables;
import com.github.dakusui.pcond.functions.preds.BaseFuncUtils;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

/**
 * Intended for internal use only.
 */
public enum CurryingUtils {
  ;
  private static final ThreadLocal<BaseFuncUtils.Factory<Object, Object, List<Object>>> CURRIED_FUNCTION_FACTORY_POOL = new ThreadLocal<>();

  public static CurriedFunction<Object, Object> curry(String functionName, MultiParameterFunction<Object> function) {
    return curry(functionName, function, emptyList());
  }

  static CurriedFunction<Object, Object> curry(String functionName, MultiParameterFunction<Object> function, List<? super Object> ongoingContext) {
    requireNonNull(functionName);
    return curriedFunctionFactory().create(asList(functionName, function, ongoingContext));
  }

  private static BaseFuncUtils.Factory<Object, Object, List<Object>> curriedFunctionFactory() {
    if (CURRIED_FUNCTION_FACTORY_POOL.get() == null)
      CURRIED_FUNCTION_FACTORY_POOL.set(createCurriedFunctionFactory());
    return CURRIED_FUNCTION_FACTORY_POOL.get();
  }

  private static BaseFuncUtils.Factory<Object, Object, List<Object>> createCurriedFunctionFactory() {
    return Printables.functionFactory(
        (args) -> FormattingUtils.functionNameFormatter(functionName(args), ongoingContext(args)).apply(function(args)),
        (args) -> new CurriedFunction.Base(function(args), ongoingContext(args), functionName(args)));
  }

  private static String functionName(List<Object> args) {
    return (String) args.get(0);
  }

  @SuppressWarnings("unchecked")
  private static MultiParameterFunction<Object> function(List<Object> args) {
    return (MultiParameterFunction<Object>) args.get(1);
  }

  @SuppressWarnings("unchecked")
  private static List<? super Object> ongoingContext(List<Object> args) {
    return (List<? super Object>) args.get((2));
  }
}
