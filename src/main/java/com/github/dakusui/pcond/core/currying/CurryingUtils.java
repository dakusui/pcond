package com.github.dakusui.pcond.core.currying;

import com.github.dakusui.pcond.core.multi.MultiFunction;
import com.github.dakusui.pcond.core.preds.BaseFuncUtils;
import com.github.dakusui.pcond.functions.Printables;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * Intended for internal use only.
 */
public enum CurryingUtils {
	;
	private static final ThreadLocal<BaseFuncUtils.Factory<Object, Object, List<Object>>> CURRIED_FUNCTION_FACTORY_POOL = new ThreadLocal<>();

	public static CurriedFunction<Object, Object> curry(MultiFunction<Object> function) {
		return curry(function, emptyList());
	}

	static CurriedFunction<Object, Object> curry(MultiFunction<Object> function, List<? super Object> ongoingContext) {
		return curriedFunctionFactory().create(asList(function.name(), function, ongoingContext));
	}

	private static BaseFuncUtils.Factory<Object, Object, List<Object>> curriedFunctionFactory() {
		if (CURRIED_FUNCTION_FACTORY_POOL.get() == null)
			CURRIED_FUNCTION_FACTORY_POOL.set(createCurriedFunctionFactory());
		return CURRIED_FUNCTION_FACTORY_POOL.get();
	}

	private static BaseFuncUtils.Factory<Object, Object, List<Object>> createCurriedFunctionFactory() {
		return Printables.functionFactory(
				(args) -> FormattingUtils.functionNameFormatter(functionName(args), ongoingContext(args)).apply(function(args)),
				(args) -> new CurriedFunction.Impl(function(args), ongoingContext(args)));
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
