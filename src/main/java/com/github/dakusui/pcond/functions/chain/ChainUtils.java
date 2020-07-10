package com.github.dakusui.pcond.functions.chain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalChecks.requireArgument;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparingInt;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public enum ChainUtils {
  ;

  public static Parameter parameter(int arg) {
    return Parameter.create(arg);
  }

  static MethodQuery instanceMethod(Object targetObject, String methodName, Object... arguments) {
    return MethodQuery.create(false, requireNonNull(targetObject), targetTypeOf(targetObject), methodName, arguments);
  }

  static MethodQuery classMethod(Class<?> targetClass, String methodName, Object... arguments) {
    return MethodQuery.create(true, null, targetClass, methodName, arguments);
  }

  interface Parameter {
    static Parameter create(int i) {
      requireArgument(i, v -> v >= 0, () -> "parameter index must not be negative, but " + i + " was given.");
      return new Parameter() {
        @Override
        public int index() {
          return i;
        }

        @Override
        public String toString() {
          return format("p_%s", i);
        }

        @Override
        public int hashCode() {
          return i;
        }

        @Override
        public boolean equals(Object anotherObject) {
          if (!(anotherObject instanceof Parameter))
            return false;
          Parameter another = (Parameter) anotherObject;
          return this.index() == another.index();
        }
      };
    }

    int index();
  }

  interface MethodQuery {
    boolean isStatic();

    Object targetObject();

    Class<?> targetClass();

    String methodName();

    Object[] arguments();

    String describe();

    default Integer numUnboundParameters() {
      return Stream.concat(Stream.of(this.targetObject()), Arrays.stream(this.arguments()))
          .filter(each -> each instanceof Parameter)
          .map(each -> (Parameter) each)
          .map(Parameter::index)
          .map(i -> i + 1)
          .max(comparingInt(o -> o))
          .orElse(0);
    }

    default MethodQuery bindActualArguments(Predicate<Object> isPlaceHolder, Function<Object, Object> replace) {
      Function<Object, Object> argReplacer = object -> replacePlaceHolderWithActualArgument(object, isPlaceHolder, replace);
      Object targetObject = argReplacer.apply(this.targetObject());
      return create(this.isStatic(), targetObject, targetObject.getClass(), this.methodName(), Arrays.stream(this.arguments()).map(argReplacer).toArray());
    }

    static MethodQuery create(boolean isStatic, Object targetObject, Class<?> targetClass, String methodName, Object[] arguments) {
      requireNonNull(targetClass);
      requireNonNull(arguments);
      requireNonNull(methodName);
      if (isStatic)
        requireArgument(targetObject, Objects::nonNull, () -> "targetObject must be null when isStatic is true.");
      else {
        requireNonNull(targetObject);
        requireArgument(targetObject, v -> targetClass.isAssignableFrom(v.getClass()), () -> format("Incompatible object '%s' was given it needs to be assignable to '%s'.", targetObject, targetClass.getName()));
      }

      return new MethodQuery() {
        @Override
        public boolean isStatic() {
          return isStatic;
        }

        @Override
        public Object targetObject() {
          return targetObject;
        }

        @Override
        public String methodName() {
          return methodName;
        }

        @Override
        public Class<?> targetClass() {
          return targetClass;
        }

        @Override
        public Object[] arguments() {
          return arguments;
        }

        @Override
        public String describe() {
          return format("%s.%s(%s)",
              isStatic ? targetClass.getName() : "",
              methodName,
              Arrays.stream(arguments).map(Objects::toString).collect(joining(",")));
        }
      };
    }
  }

  @SuppressWarnings("unchecked")
  public static <R> R invokeMethod(MethodQuery methodQuery) {
    try {
      return (R) setAccessible(findMethod(methodQuery.targetClass(), methodQuery.methodName(), methodQuery.arguments()))
          .invoke(methodQuery.targetObject(), methodQuery.arguments());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e.getCause());
    }
  }

  private static Method setAccessible(Method m) {
    m.setAccessible(true);
    return m;
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
    return new RuntimeException(format(
        "Method matching '%s%s' was not found by selector=%s in %s.",
        methodName,
        asList(args),
        methodSelector,
        aClass.getCanonicalName()
    ));
  }

  private static RuntimeException exceptionOnAmbiguity(Class<?> aClass, String methodName, Object[] args, MethodSelector methodSelector) {
    return new RuntimeException(format(
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

  private static Class<?> targetTypeOf(Object targetObject) {
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
        ChainUtils::addMethodIfNecessary,
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
        .map(ChainUtils::summarizeMethodName)
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

  private static Object replacePlaceHolderWithActualArgument(Object object, Predicate<Object> isPlaceHolder, Function<Object, Object> replace) {
    if (isPlaceHolder.test(object)) {
      return replace.apply(object);
    }
    return object;
  }
}
