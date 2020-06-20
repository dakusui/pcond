package com.github.dakusui.pcond.core.currying;

import com.github.dakusui.pcond.core.MultiParameterFunction;
import com.github.dakusui.pcond.functions.PrintableFunction;
import com.github.dakusui.pcond.internals.InternalChecks;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.github.dakusui.pcond.internals.InternalUtils.wrapperClassOf;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public enum ReflectionsUtils {
  ;

  private static final ThreadLocal<Map<List<Object>, MultiParameterFunction<?>>> METHOD_BASED_FUNCTION_POOL = new ThreadLocal<>();

  static final Map<Class<?>, Set<Class<?>>> WIDER_TYPES = new HashMap<Class<?>, Set<Class<?>>>() {
    {
      // https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.2
      put(wrapperClassOf(byte.class), wrapperClassesOf(asSet(short.class, int.class, long.class, float.class, double.class)));
      put(wrapperClassOf(short.class), wrapperClassesOf(asSet(int.class, long.class, float.class, double.class)));
      put(wrapperClassOf(char.class), wrapperClassesOf(asSet(int.class, long.class, float.class, double.class)));
      put(wrapperClassOf(int.class), wrapperClassesOf(asSet(long.class, float.class, double.class)));
      put(wrapperClassOf(long.class), wrapperClassesOf(asSet(float.class, double.class)));
      put(wrapperClassOf(float.class), wrapperClassesOf(asSet(double.class)));
    }

    private Set<Class<?>> wrapperClassesOf(Set<Class<?>> collect) {
      return collect.stream().map(InternalUtils::wrapperClassOf).collect(toSet());
    }

    private Set<Class<?>> asSet(Class<?>... classes) {
      return new HashSet<Class<?>>() {{
        addAll(asList(classes));
      }};
    }
  };

  @SuppressWarnings("unchecked")
  public static <R> MultiParameterFunction<R> lookupFunctionForStaticMethod(int[] order, Class<?> aClass, String methodName, Class<?>... parameterTypes) {
    Map<List<Object>, MultiParameterFunction<?>> methodBasedMultiParameterFunctionPool = methodBasedMultiParameterFunctionPool();
    List<Object> multiParamFuncDef = composeFuncDef(order, aClass, methodName, parameterTypes);
    methodBasedMultiParameterFunctionPool.computeIfAbsent(
        multiParamFuncDef,
        ReflectionsUtils::createMultiParameterFunctionForStaticMethod);
    return (MultiParameterFunction<R>) methodBasedMultiParameterFunctionPool.get(multiParamFuncDef);
  }

  private static List<Object> composeFuncDef(int[] order, Class<?> aClass, String methodName, Class<?>[] parameterTypes) {
    final List<Integer> paramOrder = checkOrderArrayAndConvertToList(order, parameterTypes);
    Method method = Checks.validateMethod(InternalUtils.getMethod(aClass, methodName, parameterTypes));
    return asList(method, paramOrder);
  }

  private static List<Integer> checkOrderArrayAndConvertToList(int[] order, Class<?>[] parameterTypes) {
    final List<Integer> paramOrder = unmodifiableList(Arrays.stream(order).boxed().distinct().collect(toList()));
    InternalChecks.requireArgument(order, o -> o.length == paramOrder.size(), () -> "Duplicated elements are found in the 'order' argument:" + Arrays.toString(order) + " " + paramOrder);
    InternalChecks.requireArgument(order, o -> o.length == parameterTypes.length, () -> "Inconsistent number of parameters are supplied by 'order'. Expected:" + parameterTypes.length + ", Actual: " + order.length);
    return paramOrder;
  }

  public static <R> MultiParameterFunction<R> createMultiParameterFunctionForStaticMethod(List<Object> multiParamFuncDef) {
    final Method method = (Method) multiParamFuncDef.get(0);
    @SuppressWarnings("unchecked") final List<Integer> paramOrder = (List<Integer>) multiParamFuncDef.get(1);
    return createMultiParameterFunctionForStaticMethod(method, paramOrder);
  }

  @SuppressWarnings("unchecked")
  public static <R> MultiParameterFunction<R> createMultiParameterFunctionForStaticMethod(Method method, List<Integer> paramOrder) {
    class PrintableMultiParameterFunction<RR> extends PrintableFunction<List<? super Object>, RR> implements MultiParameterFunction<RR> {
      final Object identity = asList(method, paramOrder);

      PrintableMultiParameterFunction() {
        super(
            () -> FormattingUtils.formatMethodName(method) + FormattingUtils.formatParameterOrder(paramOrder),
            objects -> {
              try {
                return (RR) method.invoke(null, (paramOrder).stream().map(objects::get).toArray());
              } catch (IllegalAccessException | InvocationTargetException e) {
                throw InternalUtils.wrap(
                    String.format("Invoked method:%s threw an exception", FormattingUtils.formatMethodName(method)),
                    e instanceof InvocationTargetException ? e.getCause() : e);
              }
            });
      }

      @Override
      public int arity() {
        return method.getParameterCount();
      }

      @Override
      public Class<?> parameterType(int i) {
        return method.getParameterTypes()[(paramOrder).get(i)];
      }

      @SuppressWarnings("unchecked")
      @Override
      public Class<? extends RR> returnType() {
        return (Class<? extends RR>) (method).getReturnType();
      }

      @Override
      public int hashCode() {
        return identity.hashCode();
      }

      @Override
      public boolean equals(Object anotherObject) {
        if (anotherObject == this)
          return true;
        if (anotherObject instanceof PrintableMultiParameterFunction) {
          PrintableMultiParameterFunction<?> another = (PrintableMultiParameterFunction<?>) anotherObject;
          return this.identity().equals(another.identity());
        }
        return false;
      }

      Object identity() {
        return this.identity;
      }
    }
    return new PrintableMultiParameterFunction<>();
  }

  private static Map<List<Object>, MultiParameterFunction<?>> methodBasedMultiParameterFunctionPool() {
    if (METHOD_BASED_FUNCTION_POOL.get() == null)
      METHOD_BASED_FUNCTION_POOL.set(new HashMap<>());
    return METHOD_BASED_FUNCTION_POOL.get();
  }
}
