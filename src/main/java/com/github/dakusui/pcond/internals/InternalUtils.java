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
    boolean ret = false;
    try {
      assert false;
    } catch (AssertionError e) {
      ret = true;
    }
    return ret;
  }

  @SuppressWarnings("unchecked")
  public static <T> T createInstanceFromClassName(Class<? super T> expectedClass, String requestedClassName) {
    try {
      Class<?> loadedClass = Class.forName(requestedClassName);
      try {
        return (T) expectedClass.cast(loadedClass.getDeclaredConstructor().newInstance());
      } catch (ClassCastException e) {
        throw Exceptions.wrap("The requested class:'" + requestedClassName +
                "' was found but not an instance of " + expectedClass.getCanonicalName() + ".: " +
                "It was '" + loadedClass.getCanonicalName() + "'.",
            e
        );
      } catch (NoSuchMethodException e) {
        throw Exceptions.wrap("Public constructor without parameters was not found in " + requestedClassName, e);
      } catch (InvocationTargetException e) {
        throw Exceptions.wrap(
            "Public constructor without parameters was found in " + requestedClassName + " but threw an exception",
            e.getCause());
      }
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw Exceptions.wrap("The requested class was not found or not accessible.: " + requestedClassName, e);
    }
  }
}
