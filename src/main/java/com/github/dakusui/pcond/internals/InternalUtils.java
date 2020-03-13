package com.github.dakusui.pcond.internals;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public enum InternalUtils {
  ;

  public static String formatObject(Object value) {
    if (value == null)
      return "null";
    if (value instanceof Collection) {
      Collection<?> collection = (Collection<?>) value;
      if (collection.size() < 4)
        return format("(%s)",
            collection.stream().map(InternalUtils::formatObject).collect(Collectors.joining(",")));
      Iterator<?> i = collection.iterator();
      return format("(%s,%s,%s...;%s)",
          formatObject(i.next()),
          formatObject(i.next()),
          formatObject(i.next()),
          collection.size()
      );
    }
    if (value instanceof Object[])
      return formatObject(asList((Object[]) value));
    if (value instanceof String) {
      String s = (String) value;
      if (s.length() > 20)
        s = s.substring(0, 12) + "..." + s.substring(s.length() - 5);
      return format("\"%s\"", s);
    }
    String ret = value.toString();
    ret = ret.contains("$")
        ? ret.substring(ret.lastIndexOf("$") + 1)
        : ret;
    return ret;
  }

  public static <T, E extends Throwable> T check(
      T value,
      Predicate<? super T> cond,
      BiFunction<T, Predicate<? super T>, ? extends E> exceptionFactory) throws E {
    if (!cond.test(value))
      throw exceptionFactory.apply(value, cond);
    return value;
  }

  public static boolean isAssertionEnabled() {
    try {
      assert false;
      return false;
    } catch (AssertionError e) {
      return true;
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T createInstanceFromClassName(String className, Class<T> expectedClass) {
    try {
      Class<?> loadedClass = Class.forName(className);
      try {
        return (T) loadedClass.getDeclaredConstructor().newInstance();
      } catch (ClassCastException e) {
        throw new Error("The requested class:'" + className +
            "' was found but not an instance of " + expectedClass.getCanonicalName() + ".: " +
            "It was '" + loadedClass.getCanonicalName() + "'."
        );
      } catch (NoSuchMethodException e) {
        throw new Error("Public constructor without parameters was not found in " + className, e);
      } catch (InvocationTargetException e) {
        throw new Error(
            "Public constructor without parameters was found in " + className + " but thrown an exception",
            e.getCause());
      }
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new Error("The requested class was not found or not accessible.", e);
    }
  }
}
