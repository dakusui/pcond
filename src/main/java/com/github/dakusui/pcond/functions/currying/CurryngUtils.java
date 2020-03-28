package com.github.dakusui.pcond.functions.currying;

import com.github.dakusui.pcond.functions.PrintableFunction;
import com.github.dakusui.pcond.functions.Printables;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

public class CurryngUtils {

  public static final Map<Class<?>, Set<Class<?>>> WIDER_CLASSES = new HashMap<Class<?>, Set<Class<?>>>() {
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
      return collect.stream().map(CurryngUtils::wrapperClassOf).collect(toSet());
    }

    private Set<Class<?>> asSet(Class<?>... classes) {
      return new HashSet<Class<?>>() {{
        addAll(asList(classes));
      }};
    }
  };

  public static CurriedFunction<Object, Object> curry(MultiParameterFunction<Object> function) {
    return curry(function, emptyList());
  }

  private static CurriedFunction<Object, Object> curry(MultiParameterFunction<Object> function, List<? super Object> ongoingContext) {
    return Printables.functionFactory((MultiParameterFunction<Object> arg) -> arg + formatObject(ongoingContext),
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
            return curry(function, append(ongoingContext, p));
          }

        }).create(function);
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
      return isWiderThan(paramType, wrapperClass);
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
    return WIDER_CLASSES.get(aClass).contains(bClass);
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

  @SuppressWarnings("unchecked")
  public static <R> MultiParameterFunction<R> createFunctionFromStaticMethod(Class<?> aClass, String methodName, Class<?>... parameterTypes) {
    try {
      Method m = validateMethod(aClass.getMethod(methodName, parameterTypes));
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
      }
      return new PrintableMultiParameterFunction<R>();
    } catch (
        NoSuchMethodException e) {
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
    return () -> "Given argument:" + formatObject(value) + " cannot be assigned to parameter:" + aClass.getCanonicalName();
  }

  @SuppressWarnings("unchecked")
  static <T> T ensureReturnedValueType(Object value, Class<?> returnType) {
    if (value == null || returnType.isInstance(value))
      return (T) value;
    throw new IllegalStateException("Returned value:" + formatObject(value) + " is neither null nor an instance of " + returnType.getName() + "(" + value.getClass().getName() + "). ");
  }

  public static <T extends CurriedFunction<?, ?>> T requireLast(T value) {
    if (!value.hasNext())
      throw new IllegalStateException();
    return value;
  }
}
