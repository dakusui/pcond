package com.github.dakusui.pcond.core.multi;

import com.github.dakusui.pcond.core.currying.FormattingUtils;
import com.github.dakusui.pcond.core.identifieable.PrintableMultiFunction;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

import static com.github.dakusui.pcond.core.currying.Checks.validateParamOrderList;
import static com.github.dakusui.pcond.core.currying.FormattingUtils.formatMethodName;
import static com.github.dakusui.pcond.core.currying.ReflectionsUtils.invokeStaticMethod;
import static com.github.dakusui.pcond.internals.InternalChecks.requireStaticMethod;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * An interface that represents a function that can have more than one parameters.
 * This interface is often used in combination with {@link com.github.dakusui.pcond.functions.Functions#curry(MultiFunction)} method.
 *
 * @param <R>
 */
public interface MultiFunction<R> extends Function<List<? super Object>, R> {
  static <R> PrintableMultiFunction<R> createFromStaticMethod(Method method, List<Integer> paramOrder) {
    validateParamOrderList(paramOrder, method.getParameterCount());
    requireStaticMethod(method);
    return new PrintableMultiFunction.Builder<R>(args -> invokeStaticMethod(method, (paramOrder).stream().map(args::get).toArray()))
        .name(method.getName())
        .formatter(() -> formatMethodName(method) + FormattingUtils.formatParameterOrder(paramOrder))
        .addParameters(paramOrder.stream().map(i -> method.getParameterTypes()[i]).collect(toList()))
        .identityArgs(asList(method, validateParamOrderList(paramOrder, method.getParameterCount())))
        .$();
  }

  /**
   * Returns a name of this function.
   *
   * @return The name of this function.
   */
  String name();

  /**
   * Returns the number of parameters that this function can take.
   *
   * @return the number of parameters
   */
  int arity();

  /**
   * The expected type of the {@code i}th parameter.
   *
   * @param i The parameter index.
   * @return The type of {@code i}th parameter.
   */
  Class<?> parameterType(int i);

  /**
   * The type of the value returned by this function.
   *
   * @return The type of the returned value.
   */
  Class<? extends R> returnType();
}
