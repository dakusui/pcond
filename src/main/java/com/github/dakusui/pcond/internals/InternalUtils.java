package com.github.dakusui.pcond.internals;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.pcond.provider.AssertionProviderBase;

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
    return formatObject(value, summarizedStringLength());
  }

  public static String formatObject(Object value, int maxLength) {
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
      s = summarizeString(s, maxLength);
      return format("\"%s\"", s);
    }
    if (value instanceof Throwable) {
      Throwable throwable = (Throwable) value;
      String simpleName = summarizeString(throwable.getClass().getSimpleName() + ":", maxLength);
      return simpleName +
          (simpleName.length() < Math.max(12, maxLength) ?
              formatObject(throwable.getMessage(), toNextEven(Math.max(12, maxLength - simpleName.length()))) :
              "");
    }
    if (isToStringOverridden(value))
      return summarizeString(
          value.toString(),
          maxLength + 2 /* 2 for margin for single quotes not necessary for non-strings */
      );
    return value.toString().substring(value.getClass().getPackage().getName().length() + 1);
  }

  private static int toNextEven(int value) {
    if ((value & 1) == 0)
      return value;
    return value + 1;
  }

  private static String summarizeString(String s, int length) {
    assert (length & 1) == 0 : "The length must be an even int, but was <" + length + ">";
    assert length >= 12 : "The length must be greater than or equal to 12. Less than 20 is not recommended. But was <" + length + ">";
    if (s.length() > length) {
      int pre = length / 2 - 2;
      int post = length / 2 - 5;
      s = s.substring(0, length - pre) + "..." + s.substring(s.length() - post);
    } else {
      s = s;
    }
    return s;
  }

  private static int summarizedStringLength() {
    return 40;
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
        throw executionFailure("The requested class:'" + requestedClassName +
                "' was found but not an instance of " + expectedClass.getCanonicalName() + ".: " +
                "It was '" + loadedClass.getCanonicalName() + "'.",
            e);
      } catch (NoSuchMethodException e) {
        throw executionFailure("Matching public constructor for " + Arrays.toString(args) + " was not found in " + requestedClassName, e);
      } catch (InvocationTargetException e) {
        throw executionFailure("Matching public constructor was found in " + requestedClassName + " but threw an exception", e.getCause());
      }
    } catch (InstantiationException | IllegalAccessException |
             ClassNotFoundException e) {
      throw executionFailure("The requested class was not found or not accessible.: " + requestedClassName, e);
    }
  }

  public static InternalException executionFailure(String message, Throwable cause) {
    throw executionFailure(AssertionProviderBase.Explanation.fromMessage(message), cause);
  }

  public static InternalException executionFailure(AssertionProviderBase.Explanation explanation, Throwable cause) {
    throw new InternalException(explanation.toString(), cause);
  }

  public static InternalException wrapIfNecessary(Throwable cause) {
    if (cause instanceof Error)
      throw (Error) cause;
    if (cause instanceof RuntimeException)
      throw (RuntimeException) cause;
    throw executionFailure(cause.getMessage(), cause);
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
      throw executionFailure(format("Requested method: %s(%s) was not found in %s", methodName, Arrays.stream(parameterTypes).map(Class::getName).collect(joining(",")), aClass.getName()), e);
    }
  }
}
