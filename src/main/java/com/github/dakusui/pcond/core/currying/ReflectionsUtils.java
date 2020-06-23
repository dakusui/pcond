package com.github.dakusui.pcond.core.currying;

import com.github.dakusui.pcond.functions.MultiParameterFunction;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.github.dakusui.pcond.core.currying.FormattingUtils.formatMethodName;
import static com.github.dakusui.pcond.internals.InternalChecks.requireArgument;
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

    private Set<Class<?>> wrapperClassesOf(Set<Class<?>> primitiveClasses) {
      return primitiveClasses.stream().map(InternalUtils::wrapperClassOf).collect(toSet());
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
    return asList(InternalUtils.getMethod(aClass, methodName, parameterTypes), Arrays.stream(order).boxed().collect(toList()));
  }

  private static <R> MultiParameterFunction<R> createMultiParameterFunctionForStaticMethod(List<Object> multiParamFuncDef) {
    final Method method = (Method) multiParamFuncDef.get(0);
    @SuppressWarnings("unchecked") final List<Integer> paramOrder = (List<Integer>) multiParamFuncDef.get(1);
    return MultiParameterFunction.createFromStaticMethod(method, paramOrder);
  }

  private static Map<List<Object>, MultiParameterFunction<?>> methodBasedMultiParameterFunctionPool() {
    if (METHOD_BASED_FUNCTION_POOL.get() == null)
      METHOD_BASED_FUNCTION_POOL.set(new HashMap<>());
    return METHOD_BASED_FUNCTION_POOL.get();
  }

  @SuppressWarnings("unchecked")
  public static <R> R invokeStaticMethod(Method method, Object[] args) {
    try {
      return (R) method.invoke(null, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw InternalUtils.wrap(
          String.format("Invoked method:%s threw an exception", formatMethodName(method)),
          e instanceof InvocationTargetException ? e.getCause() : e);
    }
  }
}
