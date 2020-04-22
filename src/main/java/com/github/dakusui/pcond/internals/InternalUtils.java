package com.github.dakusui.pcond.internals;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.functions.Printables;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

public enum InternalUtils {
  ;

  public static String formatObject(Object value) {
    if (value == null)
      return "null";
    if (value instanceof Collection) {
      Collection<?> collection = (Collection<?>) value;
      if (collection.size() < 4)
        return format("[%s]",
            collection.stream().map(InternalUtils::formatObject).collect(Collectors.joining(",")));
      Iterator<?> i = collection.iterator();
      return format("[%s,%s,%s...;%s]",
          formatObject(i.next()),
          formatObject(i.next()),
          formatObject(i.next()),
          collection.size()
      );
    }
    if (value instanceof Object[])
      return formatObject(asList((Object[]) value));
    if (value instanceof Formattable)
      return String.format("%s", value);
    if (value instanceof String) {
      String s = (String) value;
      if (s.length() > 20)
        s = s.substring(0, 12) + "..." + s.substring(s.length() - 5);
      return format("\"%s\"", s);
    }
    if (isToStringOverridden(value))
      return value.toString();
    return value.toString().substring(value.getClass().getPackage().getName().length() + 1);
  }

  private static boolean isToStringOverridden(Object object) {
    return getMethod(object.getClass(), "toString").getDeclaringClass() != Object.class;
  }

  /**
   * A method to check if assertion is enabled or not.
   *
   * @param v A boolean value to test.
   * @return {@code true} - assertion failed with the given value {@code v} / {@code false} - otherwise.
   */
  public static boolean assertFailsWith(boolean v) {
    boolean ret = false;
    try {
      assert v;
    } catch (AssertionError e) {
      ret = true;
    }
    return ret;
  }

  @SuppressWarnings("unchecked")
  public static <T> T createInstanceFromClassName(Class<? super T> expectedClass, String requestedClassName, Object... args) {
    try {
      Class<?> loadedClass = Class.forName(requestedClassName);
      try {
        return (T) expectedClass.cast(loadedClass.getDeclaredConstructor(Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new)).newInstance(args));
      } catch (ClassCastException e) {
        throw wrap("The requested class:'" + requestedClassName +
                "' was found but not an instance of " + expectedClass.getCanonicalName() + ".: " +
                "It was '" + loadedClass.getCanonicalName() + "'.",
            e);
      } catch (NoSuchMethodException e) {
        throw wrap("Public constructor without parameters was not found in " + requestedClassName, e);
      } catch (InvocationTargetException e) {
        throw wrap("Public constructor without parameters was found in " + requestedClassName + " but threw an exception", e.getCause());
      }
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw wrap("The requested class was not found or not accessible.: " + requestedClassName, e);
    }
  }

  public static InternalException wrap(String message, Throwable cause) {
    throw new InternalException(message, cause);
  }

  public static InternalException wrapIfNecessary(Throwable cause) {
    if (cause instanceof Error)
      throw (Error) cause;
    if (cause instanceof RuntimeException)
      throw (RuntimeException) cause;
    throw wrap(cause.getMessage(), cause);
  }

  public static List<? super Object> append(List<? super Object> list, Object p) {
    return unmodifiableList(new ArrayList<Object>(list) {{
      add(p);
    }});
  }

  @SuppressWarnings("unchecked")
  public static <T> Evaluable<T> toEvaluableIfNecessary(Predicate<? super T> p) {
    Objects.requireNonNull(p);
    if (p instanceof Evaluable)
      return (Evaluable<T>) p;
    // We know that Printable.predicate returns a PrintablePredicate object, which is an Evaluable.
    return (Evaluable<T>) Printables.predicate(p::toString, p);
  }

  @SuppressWarnings("unchecked")
  public static <T> Evaluable<T> toEvaluableIfNecessary(Function<? super T, ?> f) {
    Objects.requireNonNull(f);
    if (f instanceof Evaluable)
      return (Evaluable<T>) f;
    // We know that Printable.predicate returns a PrintableFunction object, which is an Evaluable.
    return (Evaluable<T>) Printables.function(f::toString, f);
  }

  public static String spaces(int num) {
    char[] buf = new char[num];
    Arrays.fill(buf, ' ');
    return String.valueOf(buf);
  }

  public static Class<?> wrapperClassOf(Class<?> clazz) {
    assert clazz != null;
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
    throw new IllegalArgumentException("Unsupported type:" + clazz.getName() + " was given.");
  }

  public static Method getMethod(Class<?> aClass, String methodName, Class<?>... parameterTypes) {
    try {
      return aClass.getMethod(methodName, parameterTypes);
    } catch (NoSuchMethodException e) {
      throw wrap(format("Requested method: %s(%s) was not found in %s", methodName, Arrays.stream(parameterTypes).map(Class::getName).collect(joining(",")), aClass.getName()), e);
    }
  }
}
