package com.github.dakusui.pcond.functions.currying;

import com.github.dakusui.pcond.functions.PrintableFunction;
import com.github.dakusui.pcond.functions.Printables;
import com.github.dakusui.pcond.internals.InternalChecks;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.functions.currying.CurryingUtils.Reflections.wrapperClassOf;
import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.*;

public enum CurryingUtils {
  ;

  public static CurriedFunction<Object, Object> curry(String functionName, MultiParameterFunction<Object> function) {
    return curry(functionName, function, emptyList());
  }

  private static CurriedFunction<Object, Object> curry(String functionName, MultiParameterFunction<Object> function, List<? super Object> ongoingContext) {
    return Printables.functionFactory(
        Formatters.functionNameFormatter(functionName, ongoingContext),
        (MultiParameterFunction<Object> arg) -> new CurriedFunction<Object, Object>() {
          @Override
          public Class<?> parameterType() {
            return function.parameterType(ongoingContext.size());
          }

          @Override
          public Class<?> returnType() {
            if (ongoingContext.size() == function.arity() - 1)
              return function.returnType();
            else
              return CurriedFunction.class;
          }

          @Override
          public Object applyFunction(Object p) {
            if (ongoingContext.size() == function.arity() - 1)
              return function.apply(InternalUtils.append(ongoingContext, p));
            return curry(functionName, function, InternalUtils.append(ongoingContext, p));
          }
        }).create(function);
  }

  public enum Reflections {
    ;

    private static final ThreadLocal<Map<Method, MultiParameterFunction<?>>> METHOD_BASED_FUNCTION_POOL = new ThreadLocal<>();

    private static final Map<Class<?>, Set<Class<?>>> WIDER_TYPES = new HashMap<Class<?>, Set<Class<?>>>() {
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
        return collect.stream().map(Reflections::wrapperClassOf).collect(toSet());
      }

      private Set<Class<?>> asSet(Class<?>... classes) {
        return new HashSet<Class<?>>() {{
          addAll(asList(classes));
        }};
      }
    };

    static Class<?> wrapperClassOf(Class<?> clazz) {
      assert clazz != null;
      assert clazz.isPrimitive();

      if (clazz == Integer.TYPE)
        return Integer.class;
      if (clazz == Long.TYPE)
        return Long.class;
      if (clazz == Boolean.TYPE)
        return Boolean.class;
      if (clazz == Byte.TYPE)
        return Byte.class;
      if (clazz == Character.TYPE)
        return Character.class;
      if (clazz == Float.TYPE)
        return Float.class;
      if (clazz == Double.TYPE)
        return Double.class;
      if (clazz == Short.TYPE)
        return Short.class;
      if (clazz == Void.TYPE)
        return Void.class;

      return clazz;
    }

    public static <R> MultiParameterFunction<R> createFunctionFromStaticMethod(Class<?> aClass, String methodName, Class<?>... parameterTypes) {
      return createFunctionFromStaticMethod(IntStream.range(0, parameterTypes.length).toArray(), aClass, methodName, parameterTypes);
    }

    @SuppressWarnings("unchecked")
    public static <R> MultiParameterFunction<R> createFunctionFromStaticMethod(int[] order, Class<?> aClass, String methodName, Class<?>... parameterTypes) {
      final List<Integer> paramOrder = Arrays.stream(order).boxed().distinct().collect(toList());
      InternalChecks.requireArgument(order, o -> o.length == paramOrder.size(), () -> "Duplicated elements are found in the 'order' argument:" + Arrays.toString(order) + " " + paramOrder);
      InternalChecks.requireArgument(order, o -> o.length == parameterTypes.length, () -> "Inconsistent number of parameter numbers are specified by 'order'. Expected:" + parameterTypes.length + ", Actual: " + order.length);
      Map<Method, MultiParameterFunction<?>> methodBasedMultiParameterFunctionPool = methodBasedMultiParameterFunctionPool();
      Method method = Checks.validateMethod(getMethod(aClass, methodName, parameterTypes));
      methodBasedMultiParameterFunctionPool.computeIfAbsent(
          method,
          m -> {
            class PrintableMultiParameterFunction<RR> extends PrintableFunction<List<? super Object>, RR> implements MultiParameterFunction<RR> {
              final List<Object> identity = asList(m, paramOrder);

              PrintableMultiParameterFunction() {
                super(
                    () -> Formatters.formatMethodName(m) + Formatters.formatParameterOrder(paramOrder),
                    objects -> {
                      try {
                        return (RR) m.invoke(null, paramOrder.stream().map(objects::get).toArray());
                      } catch (IllegalAccessException | InvocationTargetException e) {
                        throw InternalUtils.wrapIfNecessary(e);
                      }
                    });
              }

              @Override
              public int arity() {
                return m.getParameterCount();
              }

              @Override
              public Class<?> parameterType(int i) {
                return m.getParameterTypes()[order[i]];
              }

              @Override
              public Class<? extends RR> returnType() {
                return (Class<? extends RR>) m.getReturnType();
              }

              @Override
              public int hashCode() {
                return m.hashCode();
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

              List<Object> identity() {
                return this.identity;
              }
            }
            return new PrintableMultiParameterFunction<>();
          });
      return (MultiParameterFunction<R>) methodBasedMultiParameterFunctionPool.get(method);
    }

    private static Map<Method, MultiParameterFunction<?>> methodBasedMultiParameterFunctionPool() {
      if (METHOD_BASED_FUNCTION_POOL.get() == null)
        METHOD_BASED_FUNCTION_POOL.set(new HashMap<>());
      return METHOD_BASED_FUNCTION_POOL.get();
    }

    private static Method getMethod(Class<?> aClass, String methodName, Class<?>[] parameterTypes) {
      try {
        return aClass.getMethod(methodName, parameterTypes);
      } catch (NoSuchMethodException e) {
        throw InternalUtils.wrapIfNecessary(e);
      }
    }
  }

  enum Formatters {
    ;

    private static Function<MultiParameterFunction<Object>, String> functionNameFormatter(String functionName, List<? super Object> ongoingContext) {
      return (MultiParameterFunction<Object> function) -> functionName +
          (!ongoingContext.isEmpty() ? IntStream.range(0, ongoingContext.size())
              .mapToObj(i -> function.parameterType(i).getSimpleName() + ":" + ongoingContext.get(i))
              .collect(joining(",", "(", ")")) : "") +
          IntStream.range(ongoingContext.size(), function.arity())
              .mapToObj(i -> "(" + function.parameterType(i).getSimpleName() + ")")
              .collect(joining());
    }

    private static String formatMethodName(Method method) {
      return String.format("%s.%s[%s]",
          method.getDeclaringClass().getSimpleName(),
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

  enum Checks {
    ;

    static <T> T validateArgumentType(T arg, Class<?> paramType, Supplier<String> messageFormatter) {
      InternalChecks.checkArgument(isValidArgument(paramType, arg), messageFormatter);
      return arg;
    }

    static boolean isValidArgument(Class<?> paramType, Object arg) {
      if (paramType.isPrimitive()) {
        if (arg == null)
          return false;
        Class<?> wrapperClass = wrapperClassOf(paramType);
        if (wrapperClass.equals(arg.getClass()))
          return true;
        return isWiderThan(wrapperClass, arg.getClass());
      } else {
        if (arg == null)
          return true;
        return paramType.isAssignableFrom(arg.getClass());
      }
    }

    private static boolean isWiderThan(Class<?> aClass, Class<?> bClass) {
      assert !bClass.isPrimitive();
      assert !aClass.isPrimitive();
      return Reflections.WIDER_TYPES.get(bClass).contains(aClass);
    }

    private static Method validateMethod(Method method) {
      if (!Modifier.isStatic(method.getModifiers()))
        throw new IllegalArgumentException(String.format("The specified method '%s' is not static", method));
      return method;
    }

    @SuppressWarnings("unchecked")
    static <T> T ensureReturnedValueType(Object value, Class<?> returnType) {
      if (value == null || returnType.isInstance(value) || wrapperClassOf(returnType).isInstance(value))
        return (T) value;
      throw new IllegalStateException("Returned value:" + formatObject(value) + " is neither null nor an instance of " + returnType.getName() + "(" + value.getClass().getName() + "). ");
    }

    public static <T extends CurriedFunction<?, ?>> T requireLast(T value) {
      if (value.hasNext())
        throw new IllegalStateException();
      return value;
    }
  }
}
