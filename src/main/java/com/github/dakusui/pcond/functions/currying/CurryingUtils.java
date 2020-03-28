package com.github.dakusui.pcond.functions.currying;

import com.github.dakusui.pcond.functions.PrintableFunction;
import com.github.dakusui.pcond.functions.Printables;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

public class CurryingUtils {
  public static final Map<Class<?>, Set<Class<?>>> WIDER_TYPES = new HashMap<Class<?>, Set<Class<?>>>() {
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
      return collect.stream().map(CurryingUtils::wrapperClassOf).collect(toSet());
    }

    private Set<Class<?>> asSet(Class<?>... classes) {
      return new HashSet<Class<?>>() {{
        addAll(asList(classes));
      }};
    }
  };

  public static CurriedFunction<Object, Object> curry(String functionName, MultiParameterFunction<Object> function) {
    return curry(functionName, function, emptyList());
  }

  private static CurriedFunction<Object, Object> curry(String functionName, MultiParameterFunction<Object> function, List<? super Object> ongoingContext) {
    return Printables.functionFactory(
        functionNameFormatter(functionName, ongoingContext),
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
              return function.apply(append(ongoingContext, p));
            return curry(functionName, function, append(ongoingContext, p));
          }
        }).create(function);
  }

  private static Function<MultiParameterFunction<Object>, String> functionNameFormatter(String functionName, List<? super Object> ongoingContext) {
    return (MultiParameterFunction<Object> function) -> functionName +
        (!ongoingContext.isEmpty() ? IntStream.range(0, ongoingContext.size())
            .mapToObj(i -> function.parameterType(i).getSimpleName() + ":" + ongoingContext.get(i))
            .collect(joining(",", "(", ")")) : "") +
        IntStream.range(ongoingContext.size(), function.arity())
            .mapToObj(i -> "(" + function.parameterType(i).getSimpleName() + ")")
            .collect(joining());
  }

  static <T> T validateArgumentType(T arg, Class<?> paramType, Supplier<String> messageFormatter) {
    checkArgument(isValidArgument(paramType, arg), messageFormatter);
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

  private static void checkArgument(boolean b, Supplier<String> messageFormatter) {
    if (!b)
      throw new IllegalArgumentException(messageFormatter.get());
  }

  private static boolean isWiderThan(Class<?> aClass, Class<?> bClass) {
    assert !bClass.isPrimitive();
    assert !aClass.isPrimitive();
    return WIDER_TYPES.get(bClass).contains(aClass);
  }

  private static Class<?> wrapperClassOf(Class<?> clazz) {
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

  private static List<? super Object> append(List<? super Object> list, Object p) {
    return unmodifiableList(new ArrayList<Object>(list) {{
      add(p);
    }});
  }

  private static final ThreadLocal<Map<Method, MultiParameterFunction<?>>> METHOD_BASED_FUNCTION_POOL = new ThreadLocal<>();

  @SuppressWarnings("unchecked")
  public static <R> MultiParameterFunction<R> createFunctionFromStaticMethod(Class<?> aClass, String methodName, Class<?>... parameterTypes) {
    Map<Method, MultiParameterFunction<?>> methodBasedMultiParameterFunctionPool = methodBasedMultiParameterFunctionPool();

    Method method = validateMethod(getMethod(aClass, methodName, parameterTypes));
    methodBasedMultiParameterFunctionPool.computeIfAbsent(method, m -> {
      class PrintableMultiParameterFunction<RR> extends PrintableFunction<List<? super Object>, RR> implements MultiParameterFunction<RR> {
        PrintableMultiParameterFunction() {
          super(() -> formatMethodName(m),
              objects -> {
                try {
                  return (RR) m.invoke(null, objects.toArray());
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
          return m.getParameterTypes()[i];
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
            return this.method().equals(another.method());
          }
          return false;
        }

        Method method() {
          return m;
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

  public static Method getMethod(Class<?> aClass, String methodName, Class<?>[] parameterTypes) {
    try {
      return aClass.getMethod(methodName, parameterTypes);
    } catch (NoSuchMethodException e) {
      throw InternalUtils.wrapIfNecessary(e);
    }
  }

  private static String formatMethodName(Method method) {
    return String.format("%s.%s[%s]",
        method.getDeclaringClass().getSimpleName(),
        method.getName(),
        Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(joining(",")));
  }

  private static Method validateMethod(Method method) {
    if (!Modifier.isStatic(method.getModifiers()))
      throw new IllegalArgumentException(String.format("The specified method '%s' is not static", method));
    return method;
  }

  static Supplier<String> messageInvalidTypeArgument(Object value, Class<?> aClass) {
    return () -> "Given argument:" + formatObject(value) +
        (value == null ?
            "" :
            "(" + value.getClass() + ")") +
        " cannot be assigned to parameter:" + aClass.getCanonicalName();
  }

  @SuppressWarnings("unchecked")
  static <T> T ensureReturnedValueType(Object value, Class<?> returnType) {
    if (value == null || returnType.isInstance(value))
      return (T) value;
    throw new IllegalStateException("Returned value:" + formatObject(value) + " is neither null nor an instance of " + returnType.getName() + "(" + value.getClass().getName() + "). ");
  }

  public static <T extends CurriedFunction<?, ?>> T requireLast(T value) {
    if (value.hasNext())
      throw new IllegalStateException();
    return value;
  }

  public static <V> V requireArgument(V arg, Predicate<? super V> predicate, Supplier<String> messageFormatter) {
    if (predicate.test(arg))
      throw new IllegalArgumentException(messageFormatter.get());
    return arg;
  }
}
