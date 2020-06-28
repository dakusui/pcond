package com.github.dakusui.pcond.functions.chain;

import com.github.dakusui.pcond.functions.Printables;
import com.github.dakusui.pcond.functions.chain.Call.Arg;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public enum ChainUtils {
  ;
  public static final Object THIS = new Object() {
    public String toString() {
      return "(THIS)";
    }
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

  public static String summarize(Object value) {
    if (value == null)
      return "null";
    if (value instanceof Collection) {
      Collection<?> collection = (Collection<?>) value;
      if (collection.size() < 4)
        return format("(%s)",
            collection.stream().map(ChainUtils::summarize).collect(Collectors.joining(",")));
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
      return format("\"%s\"", s);
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
                ChainUtils::addMethodIfNecessary,
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
  public static boolean withBoxingIsAssignableFrom(Class<?> a, Class<?> b) {
    if (a.isAssignableFrom(b))
      return true;
    return toWrapperIfPrimitive(a).isAssignableFrom(toWrapperIfPrimitive(b));
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

  public static Object[] replaceTargetInArray(Object target, Object[] args) {
    return Arrays.stream(args)
        .map(e -> replaceTarget(e, target)).toArray();
  }

  public static <I> Object replaceTarget(Object on, I target) {
    return on == THIS ?
        target :
        on instanceof Object[] ?
            replaceTargetInArray(target, (Object[]) on) :
            on;
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
  private static Method[] getMethods(Class<?> aClass) {
    return aClass.getMethods();
  }
  public static Object[] replaceArgInArray(Object[] args) {
    return Arrays.stream(args)
        .map(e -> e instanceof Arg
            ? ((Arg<?>) e).value()
            : e)
        .toArray();
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

  private static Class<?> toClass(Object value) {
    if (value == null)
      return null;
    if (value instanceof Arg)
      return ((Arg<?>) value).type();
    return value.getClass();
  }

  public static <I, E> Function<? super I, ? extends E> invokeOn(Object on, String methodName, Object... args) {
    return Printables.function(
        on == THIS
            ? () -> String.format("%s%s", methodName, summarize(args))
            : () -> String.format("->%s.%s%s", on, methodName, summarize(args)),
        (I target) -> ChainUtils.invokeMethod(
            ChainUtils.replaceTarget(on, target),
            methodName,
            args
        ));
  }

  public static <I, E> Function<? super I, ? extends E> invokeStatic(Class<?> klass, String methodName, Object... args) {
    return Printables.function(
        () -> String.format("->%s.%s%s", klass.getSimpleName(), methodName, summarize(args)),
        (I target) -> ChainUtils.invokeStaticMethod(
            klass,
            target,
            methodName,
            args
        ));
  }
}
