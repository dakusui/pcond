package com.github.dakusui.pcond.core.currying;

import com.github.dakusui.pcond.functions.MultiParameterFunction;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.util.stream.Collectors.joining;

public enum FormattingUtils {
  ;

  static Function<MultiParameterFunction<Object>, String> functionNameFormatter(String functionName, List<? super Object> ongoingContext) {
    return (MultiParameterFunction<Object> function) -> functionName +
        (!ongoingContext.isEmpty() ? IntStream.range(0, ongoingContext.size())
            .mapToObj(i -> function.parameterType(i).getSimpleName() + ":" + ongoingContext.get(i))
            .collect(joining(",", "(", ")")) : "") +
        IntStream.range(ongoingContext.size(), function.arity())
            .mapToObj(i -> "(" + function.parameterType(i).getSimpleName() + ")")
            .collect(joining());
  }

  public static String formatMethodName(Method method) {
    return String.format("%s.%s(%s)",
        method.getDeclaringClass().getName(),
        method.getName(),
        Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(joining(",")));
  }

  public static String formatParameterOrder(List<Integer> paramOrder) {
    String formatted = formatParamOrder(paramOrder.stream());
    String uncustomized = formatParamOrder(IntStream.range(0, paramOrder.size()).boxed());
    return formatted.equals(uncustomized) ?
        "" :
        formatted;
  }

  public static String formatParamOrder(Stream<Integer> paramOrderStream) {
    return paramOrderStream.map(Object::toString).collect(joining(",", "(", ")"));
  }

  static Supplier<String> messageInvalidTypeArgument(Object value, Class<?> aClass) {
    return () -> "Given argument:" + formatObject(value) +
        (value == null ?
            "" :
            "(" + value.getClass() + ")") +
        " cannot be assigned to parameter:" + aClass.getCanonicalName();
  }
}
