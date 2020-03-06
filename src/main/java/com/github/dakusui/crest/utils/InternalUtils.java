package com.github.dakusui.crest.utils;

import com.github.dakusui.crest.core.Call.Arg;
import com.github.dakusui.crest.core.MethodSelector;
import com.github.dakusui.crest.core.Report;
import com.github.dakusui.pcond.functions.Functions;
import org.opentest4j.AssertionFailedError;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public enum InternalUtils {
  ;

  public static final Consumer<?>  NOP                     = e -> {
  };
  public static final Class<?>[][] PRIMITIVE_WRAPPER_TABLE = {
      { boolean.class, Boolean.class },
      { byte.class, Byte.class },
      { char.class, Character.class },
      { short.class, Short.class },
      { int.class, Integer.class },
      { long.class, Long.class },
      { float.class, Float.class },
      { double.class, Double.class },
  };

  private static final List<Class<? extends Error>> BLACKLISTED_ERROR_TYPES = singletonList(OutOfMemoryError.class);

  /**
   * Tries to find a method whose name is {@code methodName} from a given class {@code aClass}
   * and that can be invoked with parameter values {@code args}.
   * <p>
   * Unless one and only one method is found appropriate, an exception will be
   * thrown.
   * <p>
   * In this version, boxing/unboxing and casting are not attempted to determine
   * the methodto be returned during the search. This means, if there are overloaded
   * methods of the {@code methodName} that can be invoked with {@code args}, this
   * method will fail. Also even if there is a method of the {@code methodName}
   * that can be invoked if boxing/unboxing happens, this method will fail.
   *
   * @param aClass     A class from which the method is searched.
   * @param methodName A name of the method
   * @param args       Arguments which should be given to the method
   * @return A method for given class {@code aClass}, {@code method}, and {@code args}.
   */
  public static Method findMethod(Class<?> aClass, String methodName, Object[] args) {
    MethodSelector methodSelector = new MethodSelector.Default()
        .andThen(new MethodSelector.PreferNarrower())
        .andThen(new MethodSelector.PreferExact());
    return getIfOnlyOneElseThrow(
        methodSelector,
        methodSelector.select(
            Arrays.stream(
                getMethods(aClass)
            ).filter(
                (Method m) -> m.getName().equals(methodName)
            ).collect(
                LinkedList::new,
                InternalUtils::addMethodIfNecessary,
                (List<Method> methods, List<Method> methods2) -> methods2.forEach(
                    method -> {
                      addMethodIfNecessary(methods, method);
                    })),
            args
        ),
        aClass,
        methodName,
        args
    );
  }

  /*
   * Based on BaseDescription#appendValue() of Hamcrest
   *
   * http://hamcrest.org/JavaHamcrest/
   */
  public static String summarizeValue(Object value) {
    if (value == null)
      return "null";
    if (value instanceof String)
      return String.format("\"%s\"", toJavaSyntax((String) value));
    if (value instanceof Character)
      return String.format("\"%s\"", toJavaSyntax((Character) value));
    if (value instanceof Short)
      return String.format("<%ss>", value);
    if (value instanceof Long)
      return String.format("<%sL>", value);
    if (value instanceof Float)
      return String.format("<%sF>", value);
    if (value.getClass().isArray())
      return arrayToString(value);
    if (value instanceof Throwable)
      return formatThrowable(getRootCause((Throwable) value));
    return format("<%s>", summarize(value));
  }

  private static Throwable getRootCause(Throwable throwable) {
    if (throwable.getCause() == null)
      return throwable;
    return getRootCause(throwable.getCause());
  }

  private static String formatThrowable(Throwable throwable) {
    return String.format("%s(%s)", throwable.getClass().getCanonicalName(), throwable.getMessage());
  }

  public static String summarize(Object value) {
    if (value == null)
      return "null";
    if (value instanceof Collection) {
      Collection<?> collection = (Collection<?>) value;
      if (collection.size() < 4)
        return String.format("(%s)",
            String.join(
                ",",
                (List<String>) collection.stream().map(InternalUtils::summarize).collect(toList())
            ));
      Iterator<?> i = collection.iterator();
      return format("(%s,%s,%s...;%s)",
          summarize(i.next()),
          summarize(i.next()),
          summarize(i.next()),
          collection.size()
      );
    }
    if (value instanceof Object[])
      return summarize(asList((Object[]) value));
    if (value instanceof String) {
      String s = (String) value;
      if (s.length() > 20)
        s = s.substring(0, 12) + "..." + s.substring(s.length() - 5);
      return String.format("\"%s\"", s);
    }
    String ret = value.toString();
    ret = ret.contains("$")
        ? ret.substring(ret.lastIndexOf("$") + 1)
        : ret;
    return ret;
  }

  @SuppressWarnings("unchecked")
  public static <R> R invokeMethod(Object target, String methodName, Object[] args) {
    try {
      Method m = findMethod(Objects.requireNonNull(target).getClass(), methodName, replaceTargetInArray(target, args));
      boolean accessible = m.isAccessible();
      try {
        m.setAccessible(true);
        return (R) m.invoke(target, replaceTargetInArray(target, replaceArgInArray(args)));
      } finally {
        m.setAccessible(accessible);
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e.getCause());
    }
  }

  @SuppressWarnings("unchecked")
  public static <R> R invokeStaticMethod(Class<?> klass, Object target, String methodName, Object[] args) {
    try {
      Method m = findMethod(Objects.requireNonNull(klass), methodName, replaceTargetInArray(target, args));
      boolean accessible = m.isAccessible();
      try {
        m.setAccessible(true);
        return (R) m.invoke(null, replaceTargetInArray(target, replaceArgInArray(args)));
      } finally {
        m.setAccessible(accessible);
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e.getTargetException());
    }
  }

  public static String formatFunction(Function<?, ?> function, @SuppressWarnings("SameParameterValue") String variableName) {
    return format("%s%s", variableName, function.toString());
  }

  public static boolean areArgsCompatible(Class<?>[] formalParameters, Object[] args) {
    if (formalParameters.length != args.length)
      return false;
    for (int i = 0; i < args.length; i++) {
      if (args[i] == null)
        if (formalParameters[i].isPrimitive())
          return false;
        else
          continue;
      if (!withBoxingIsAssignableFrom(formalParameters[i], toClass(args[i])))
        return false;
    }
    return true;
  }

  public static boolean withBoxingIsAssignableFrom(Class<?> a, Class<?> b) {
    if (a.isAssignableFrom(b))
      return true;
    return toWrapperIfPrimitive(a).isAssignableFrom(toWrapperIfPrimitive(b));
  }


  private static Method[] getMethods(Class<?> aClass) {
    return aClass.getMethods();
  }

  private static void addMethodIfNecessary(List<Method> methods, Method method) {
    Optional<Method> found = methods.stream().filter(
        each -> Arrays.equals(each.getParameterTypes(), method.getParameterTypes())
    ).findAny();
    if (found.isPresent()) {
      if (found.get().getDeclaringClass().isAssignableFrom(method.getDeclaringClass()))
        methods.remove(found.get());
    }
    methods.add(method);
  }

  private static Class<?> toClass(Object value) {
    if (value == null)
      return null;
    if (value instanceof Arg)
      return ((Arg<?>) value).type();
    return value.getClass();
  }

  public static String toSimpleClassName(Object value) {
    Class<?> klass = toClass(value);
    return klass == null
        ? null
        : klass.getSimpleName();
  }

  public static void throwIfBlacklisted(Throwable t) {
    if (BLACKLISTED_ERROR_TYPES.stream().anyMatch(i -> i.isInstance(t)))
      throw (Error) t;
  }

  private static Class<?> toWrapperIfPrimitive(Class<?> in) {
    for (Class<?>[] pair : PRIMITIVE_WRAPPER_TABLE) {
      if (Objects.equals(in, pair[0]))
        return pair[1];
    }
    return in;
  }

  private static Method getIfOnlyOneElseThrow(MethodSelector selector, List<Method> foundMethods, Class<?> aClass, String methodName, Object[] args) {
    if (foundMethods.isEmpty())
      throw new RuntimeException(String.format(
          "Method matching '%s%s' was not found by selector=%s in %s.",
          methodName,
          Arrays.asList(args),
          selector,
          aClass.getCanonicalName()
      ));
    if (foundMethods.size() == 1)
      return foundMethods.get(0);
    throw new RuntimeException(String.format(
        "Methods matching '%s%s' were found more than one in %s by selector=%s.: %s ",
        methodName,
        summarize(args),
        aClass.getCanonicalName(),
        selector,
        summarizeMethods(foundMethods)
    ));
  }

  private static List<String> summarizeMethods(List<Method> methods) {
    return methods.stream().map(
        method -> method.toString().replace(
            method.getDeclaringClass().getCanonicalName() + "." + method.getName(),
            method.getName()
        )
    ).collect(toList());
  }

  private static String arrayToString(Object arr) {
    StringBuilder b = new StringBuilder();
    b.append("[");
    int length = Array.getLength(arr);
    if (length > 0) {
      for (int i = 0; i < length - 1; i++) {
        b.append(Array.get(arr, i));
        b.append(",");
      }
      b.append(Array.get(arr, length - 1));
    }
    b.append("]");
    return b.toString();
  }

  private static String toJavaSyntax(String unformatted) {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < unformatted.length(); i++) {
      b.append(toJavaSyntax(unformatted.charAt(i)));
    }
    return b.toString();
  }

  private static String toJavaSyntax(char ch) {
    switch (ch) {
    case '"':
      return "\\\"";
    case '\n':
      return ("\\n");
    case '\r':
      return ("\\r");
    case '\t':
      return ("\\t");
    default:
      return Character.toString(ch);
    }
  }

  public static String composeComparisonText(String message, Report report) {
    return new AssertionFailedError(message, report.expectation(), report.mismatch()).getMessage();
  }

  public static RuntimeException rethrow(Throwable e) {
    if (e instanceof RuntimeException)
      throw (RuntimeException) e;
    if (e instanceof Error)
      throw (Error) e;
    return new RuntimeException(e);
  }

  public static Object[] replaceArgInArray(Object[] args) {
    return Arrays.stream(args)
        .map(e -> e instanceof Arg
            ? ((Arg<?>) e).value()
            : e)
        .toArray();
  }

  public static Object[] replaceTargetInArray(Object target, Object[] args) {
    return Arrays.stream(args)
        .map(e -> replaceTarget(e, target)).toArray();
  }

  public static <I> Object replaceTarget(Object on, I target) {
    return on == Functions.THIS ?
        target :
        on instanceof Object[] ?
            replaceTargetInArray(target, (Object[]) on) :
            on;
  }

  public static <T> T require(T value, Predicate<T> condition, Function<String, RuntimeException> exceptionFactory) {
    if (condition.test(value))
      return value;
    throw exceptionFactory.apply(String.format("Value <%s> did not meet the requirement <%s>", value, condition));
  }

  public static <T> T requireArgument(T value, Predicate<T> condition) {
    return require(value, condition, IllegalArgumentException::new);
  }

  public static String spaces(int size) {
    return times(' ', size);
  }

  public static String times(char c, int size) {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < size; i++)
      b.append(c);
    return b.toString();
  }

  public static String formatObject(Object value) {
    if (value instanceof String)
      return format("\"%s\"", value);
    if (value instanceof Character)
      return format("'%s'", value);
    return format("%s", value);
  }
}
