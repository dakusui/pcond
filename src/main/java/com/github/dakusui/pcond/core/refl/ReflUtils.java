package com.github.dakusui.pcond.core.refl;

import com.github.dakusui.pcond.Assertions;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.internals.MethodAccessException;
import com.github.dakusui.pcond.internals.MethodAmbiguous;
import com.github.dakusui.pcond.internals.MethodInvocationException;
import com.github.dakusui.pcond.internals.MethodNotFound;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * This class consists of {@code static} utility methods for creating printable functions and predicate
 * on objects.
 */
public enum ReflUtils {
  ;

  /**
   * Invokes a method found by {@code methodQuery}.
   * All parameters in the query needs to be bound before calling this method.
   * When a query matches no or more than one methods, an exception will be thrown.
   * <p>
   * If an exception is thrown by the method, it will be wrapped by {@link RuntimeException} and re-thrown.
   *
   * @param methodQuery A query that speficies the method to be executed.
   * @param <R>         Type of the returned value.
   * @return The value returned from the method found by the query.
   * @see MethodQuery
   * @see ReflUtils#findMethod(Class, String, Object[])
   */
  @SuppressWarnings("unchecked")
  public static <R> R invokeMethod(MethodQuery methodQuery) {
    assert Assertions.precondition(methodQuery, Predicates.isNotNull());
    Method method = findMethod(methodQuery.targetClass(), methodQuery.methodName(), methodQuery.arguments());
    return invokeMethod(method, methodQuery.targetObject(), methodQuery.arguments());
  }

  @SuppressWarnings("unchecked")
  public static <R> R invokeMethod(Method method, Object obj, Object[] arguments) {
    try {
      return (R) method.invoke(obj, arguments);
    } catch (IllegalAccessException e) {
      throw new MethodAccessException(format("Method access to '%s' was failed", method), e);
    } catch (InvocationTargetException e) {
      throw new MethodInvocationException(format("Method invocation of '%s' was failed", method), e.getCause());
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
        methodSelector.select(
            Arrays.stream(
                getMethodsOf(aClass))
                .filter((Method m) -> m.getName().equals(methodName))
                .collect(toMethodList()),
            args),
        () -> exceptionOnAmbiguity(aClass, methodName, args, methodSelector),
        () -> exceptionOnMethodNotFound(aClass, methodName, args, methodSelector));
  }

  private static RuntimeException exceptionOnMethodNotFound(Class<?> aClass, String methodName, Object[] args, MethodSelector methodSelector) {
    return new MethodNotFound(format(
        "Method matching '%s%s' was not found by selector=%s in %s.",
        methodName,
        asList(args),
        methodSelector,
        aClass.getCanonicalName()
    ));
  }

  private static RuntimeException exceptionOnAmbiguity(Class<?> aClass, String methodName, Object[] args, MethodSelector methodSelector) {
    return new MethodAmbiguous(format(
        "Methods matching '%s%s' were found more than one in %s by selector=%s.: %s ",
        methodName,
        asList(args),
        aClass.getCanonicalName(),
        methodSelector,
        summarizeMethods(methodSelector.select(
            Arrays.stream(
                getMethodsOf(aClass))
                .filter((Method m) -> m.getName().equals(methodName))
                .collect(toMethodList()),
            args))));
  }

  static Class<?> targetTypeOf(Object targetObject) {
    requireNonNull(targetObject);
    return targetObject instanceof Parameter ?
        Object.class :
        targetObject.getClass();
  }

  /**
   * A collector to gather methods which have narrowest possible signatures.
   *
   * @return A collector.
   */
  private static Collector<Method, List<Method>, List<Method>> toMethodList() {
    return Collector.of(
        LinkedList::new,
        ReflUtils::addMethodIfNecessary,
        new BinaryOperator<List<Method>>() {
          @Override
          public List<Method> apply(List<Method> methods, List<Method> methods2) {
            return new LinkedList<Method>() {{
              addAll(methods);
              methods2.forEach(each -> addMethodIfNecessary(this, each));
            }};
          }
        });
  }

  private static Method getIfOnlyOneElseThrow(List<Method> foundMethods, Supplier<RuntimeException> exceptionSupplierOnAmbiguity, Supplier<RuntimeException> exceptionSupplierOnNotFound) {
    if (foundMethods.isEmpty())
      throw exceptionSupplierOnNotFound.get();
    if (foundMethods.size() == 1)
      return foundMethods.get(0);
    throw exceptionSupplierOnAmbiguity.get();
  }

  private static List<String> summarizeMethods(List<Method> methods) {
    return methods
        .stream()
        .map(ReflUtils::summarizeMethodName)
        .collect(toList());
  }

  private static String summarizeMethodName(Method method) {
    return method.toString().replace(
        method.getDeclaringClass().getCanonicalName() + "." + method.getName(),
        method.getName());
  }

  private static void addMethodIfNecessary(List<Method> methods, Method method) {
    Optional<Method> found = methods
        .stream()
        .filter(each -> Arrays.equals(each.getParameterTypes(), method.getParameterTypes()))
        .findAny();
    if (found.isPresent()) {
      if (found.get().getDeclaringClass().isAssignableFrom(method.getDeclaringClass()))
        methods.remove(found.get());
    }
    methods.add(method);
  }

  private static Method[] getMethodsOf(Class<?> aClass) {
    return aClass.getMethods();
  }

  static Object replacePlaceHolderWithActualArgument(Object object, Predicate<Object> isPlaceHolder, Function<Object, Object> replace) {
    if (isPlaceHolder.test(object)) {
      return replace.apply(object);
    }
    return object;
  }
}
