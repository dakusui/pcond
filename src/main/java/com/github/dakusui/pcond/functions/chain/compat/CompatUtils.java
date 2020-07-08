package com.github.dakusui.pcond.functions.chain.compat;

import com.github.dakusui.pcond.functions.Printables;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.dakusui.pcond.functions.chain.ChainUtils.findMethod;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public class CompatUtils {
  /*
  public static <I, E> Function<? super I, ? extends E> invokeOn(Object self, String methodName, Object... args) {
    return Printables.function(
        self == CompatCall.THIS
            ? () -> String.format("%s%s", methodName, summarize(args))
            : () -> String.format("->%s.%s%s", self, methodName, summarize(args)),
        (I target) -> ChainUtils.invokeMethod(
            replaceTarget(self, target),
            methodName,
            args
        ));
  }
   */

  @SuppressWarnings("unchecked")
  public static <R> R invokeStaticMethod(Class<?> klass, Object target, String methodName, Object[] args) {
    try {
      Method m = findMethod(requireNonNull(klass), methodName, replaceTargetInArray(target, args));
      boolean accessible = m.isAccessible();
      try {
        m.setAccessible(true);
        return (R) m.invoke(null, replaceTargetInArray(target, args));
      } finally {
        m.setAccessible(accessible);
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e.getTargetException());
    }
  }

  public static <I, E> Function<? super I, ? extends E> invokeStatic(Class<?> klass, String methodName, Object... args) {
    return Printables.function(
        () -> String.format("->%s.%s%s", klass.getSimpleName(), methodName, summarize(args)),
        (I target) -> invokeStaticMethod(
            klass,
            target,
            methodName,
            args
        ));
  }

  public static <I> Object replaceTarget(Object on, I target) {
    return on == CompatCall.THIS ?
        target :
        on instanceof Object[] ?
            replaceTargetInArray(target, (Object[]) on) :
            on;
  }

  public static String summarize(Object value) {
    if (value == null)
      return "null";
    if (value instanceof Collection) {
      Collection<?> collection = (Collection<?>) value;
      if (collection.size() < 4)
        return format("(%s)",
            collection.stream().map(CompatUtils::summarize).collect(Collectors.joining(",")));
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

  public static Object[] replaceTargetInArray(Object target, Object[] args) {
    return Arrays.stream(args)
        .map(e -> replaceTarget(e, target)).toArray();
  }
}
