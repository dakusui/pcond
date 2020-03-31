package com.github.dakusui.pcond.internals;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

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
  public static <T> T createInstanceFromClassName(Class<? super T> expectedClass, String requestedClassName) {
    try {
      Class<?> loadedClass = Class.forName(requestedClassName);
      try {
        return (T) expectedClass.cast(loadedClass.getDeclaredConstructor().newInstance());
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
}
