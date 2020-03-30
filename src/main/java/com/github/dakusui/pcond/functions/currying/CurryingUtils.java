package com.github.dakusui.pcond.functions.currying;

import com.github.dakusui.pcond.functions.MultiParameterFunction;
import com.github.dakusui.pcond.functions.PrintableFunction;
import com.github.dakusui.pcond.functions.Printables;
import com.github.dakusui.pcond.internals.InternalChecks;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.functions.currying.CurryingUtils.Reflections.wrapperClassOf;
import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.*;

/**
 * Intended for internal use only.
 */
public enum CurryingUtils {
  ;

  public static CurriedFunction<Object, Object> curry(String functionName, MultiParameterFunction<Object> function) {
    return curry(functionName, function, emptyList());
  }

  private static CurriedFunction<Object, Object> curry(String functionName, MultiParameterFunction<Object> function, List<? super Object> ongoingContext) {
    return Printables.functionFactory(
        Formatters.functionNameFormatter(functionName, ongoingContext),
        (MultiParameterFunction<Object> arg) -> new CurriedFunction<Object, Object>() {
          @Override
          public Class<?> parameterType() {
            return function.parameterType(ongoingContext.size());
          }

          @Override
          public Class<?> returnType() {
            if (ongoingContext.size() == function.arity() - 1)
              return function.returnType();
            else
              return CurriedFunction.class;
          }

          @Override
          public Object applyFunction(Object p) {
            if (ongoingContext.size() == function.arity() - 1)
              return function.apply(InternalUtils.append(ongoingContext, p));
            return curry(functionName, function, InternalUtils.append(ongoingContext, p));
          }
        }).create(function);
  }

  public enum Reflections {
    ;

    private static final ThreadLocal<Map<List<Object>, MultiParameterFunction<?>>> METHOD_BASED_FUNCTION_POOL = new ThreadLocal<>();

    private static final Map<Class<?>, Set<Class<?>>> WIDER_TYPES = new HashMap<Class<?>, Set<Class<?>>>() {
      {
        // https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.2
        put(wrapperClassOf(byte.class), wrapperClassesOf(asSet(short.class, int.class, long.class, float.class, double.class)));
        put(wrapperClassOf(short.class), wrapperClassesOf(asSet(int.class, long.class, float.class, double.class)));
        put(wrapperClassOf(char.class), wrapperClassesOf(asSet(int.class, long.class, float.class, double.class)));
        put(wrapperClassOf(int.class), wrapperClassesOf(asSet(long.class, float.class, double.class)));
        put(wrapperClassOf(long.class), wrapperClassesOf(asSet(float.class, double.class)));
        put(wrapperClassOf(float.class), wrapperClassesOf(asSet(double.class)));
      }

      private Set<Class<?>> wrapperClassesOf(Set<Class<?>> collect) {
        return collect.stream().map(Reflections::wrapperClassOf).collect(toSet());
      }

      private Set<Class<?>> asSet(Class<?>... classes) {
        return new HashSet<Class<?>>() {{
          addAll(asList(classes));
        }};
      }
    };

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

    @SuppressWarnings("unchecked")
    public static <R> MultiParameterFunction<R> createFunctionFromStaticMethod(int[] order, Class<?> aClass, String methodName, Class<?>... parameterTypes) {
      final List<Integer> paramOrder = unmodifiableList(Arrays.stream(order).boxed().distinct().collect(toList()));
      InternalChecks.requireArgument(order, o -> o.length == paramOrder.size(), () -> "Duplicated elements are found in the 'order' argument:" + Arrays.toString(order) + " " + paramOrder);
      InternalChecks.requireArgument(order, o -> o.length == parameterTypes.length, () -> "Inconsistent number of parameter numbers are specified by 'order'. Expected:" + parameterTypes.length + ", Actual: " + order.length);
      System.out.println("-->" + paramOrder);
      Map<List<Object>, MultiParameterFunction<?>> methodBasedMultiParameterFunctionPool = methodBasedMultiParameterFunctionPool();
      Method method = Checks.validateMethod(getMethod(aClass, methodName, parameterTypes));
      methodBasedMultiParameterFunctionPool.computeIfAbsent(
          asList(method, paramOrder),
          m -> {
            class PrintableMultiParameterFunction<RR> extends PrintableFunction<List<? super Object>, RR> implements MultiParameterFunction<RR> {
              final List<Object> identity = m;

              PrintableMultiParameterFunction() {
                super(
                    () -> Formatters.formatMethodName((Method) m.get(0)) + Formatters.formatParameterOrder((List<Integer>) m.get(1)),
                    objects -> {
                      try {
                        return (RR) ((Method) m.get(0)).invoke(null, ((List<Integer>) m.get(1)).stream().map(objects::get).toArray());
                      } catch (IllegalAccessException | InvocationTargetException e) {
                        throw InternalUtils.wrap(
                            String.format("Invoked method:%s threw an exception", Formatters.formatMethodName(((Method) m.get(0)))),
                            e instanceof InvocationTargetException ? e.getCause() : e);
                      }
                    });
              }

              @Override
              public int arity() {
                return ((Method) m.get(0)).getParameterCount();
              }

              @Override
              public Class<?> parameterType(int i) {
                return ((Method) m.get(0)).getParameterTypes()[paramOrder.get(i)];
              }

              @Override
              public Class<? extends RR> returnType() {
                return (Class<? extends RR>) ((Method) m.get(0)).getReturnType();
              }

              @Override
              public int hashCode() {
                return m.hashCode();
              }

              @Override
              public boolean equals(Object anotherObject) {
                if (anotherObject == this)
                  return true;
                if (anotherObject instanceof PrintableMultiParameterFunction) {
                  PrintableMultiParameterFunction<?> another = (PrintableMultiParameterFunction<?>) anotherObject;
                  return this.identity().equals(another.identity());
                }
                return false;
              }

              List<Object> identity() {
                return this.identity;
              }
            }
            return new PrintableMultiParameterFunction<>();
          });
      return (MultiParameterFunction<R>) methodBasedMultiParameterFunctionPool.get(asList(method, paramOrder));
    }

    private static Map<List<Object>, MultiParameterFunction<?>> methodBasedMultiParameterFunctionPool() {
      if (METHOD_BASED_FUNCTION_POOL.get() == null)
        METHOD_BASED_FUNCTION_POOL.set(new HashMap<>());
      return METHOD_BASED_FUNCTION_POOL.get();
    }

    private static Method getMethod(Class<?> aClass, String methodName, Class<?>[] parameterTypes) {
      try {
        return aClass.getMethod(methodName, parameterTypes);
      } catch (NoSuchMethodException e) {
        throw InternalUtils.wrap(String.format("Requested method: %s(%s) was not found in %s", methodName, Arrays.stream(parameterTypes).map(Class::getName).collect(joining(",")), aClass.getName()), e);
      }
    }
  }

  enum Formatters {
    ;

    private static Function<MultiParameterFunction<Object>, String> functionNameFormatter(String functionName, List<? super Object> ongoingContext) {
      return (MultiParameterFunction<Object> function) -> functionName +
          (!ongoingContext.isEmpty() ? IntStream.range(0, ongoingContext.size())
              .mapToObj(i -> function.parameterType(i).getSimpleName() + ":" + ongoingContext.get(i))
              .collect(joining(",", "(", ")")) : "") +
          IntStream.range(ongoingContext.size(), function.arity())
              .mapToObj(i -> "(" + function.parameterType(i).getSimpleName() + ")")
              .collect(joining());
    }

    private static String formatMethodName(Method method) {
      return String.format("%s.%s(%s)",
          method.getDeclaringClass().getName(),
          method.getName(),
          Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(joining(",")));
    }

    public static String formatParameterOrder(List<Integer> paramOrder) {
      String formatted = formatParamOrder(paramOrder.stream());
      String uncustomized = formatParamOrder(IntStream.range(0, paramOrder.size()).boxed());
      return formatted.equals(uncustomized) ?
          "" :
          formatted;
    }

    public static String formatParamOrder(Stream<Integer> paramOrderStream) {
      return paramOrderStream.map(Object::toString).collect(joining(",", "(", ")"));
    }

    static Supplier<String> messageInvalidTypeArgument(Object value, Class<?> aClass) {
      return () -> "Given argument:" + formatObject(value) +
          (value == null ?
              "" :
              "(" + value.getClass() + ")") +
          " cannot be assigned to parameter:" + aClass.getCanonicalName();
    }
  }

  enum Checks {
    ;

    static <T> T validateArgumentType(T arg, Class<?> paramType, Supplier<String> messageFormatter) {
      InternalChecks.checkArgument(isValidValueForType(arg, paramType), messageFormatter);
      return arg;
    }

    static boolean isValidValueForType(Object arg, Class<?> paramType) {
      if (paramType.isPrimitive()) {
        if (arg == null)
          return paramType.equals(void.class);
        Class<?> wrapperClass = wrapperClassOf(paramType);
        if (wrapperClass.equals(arg.getClass()))
          return true;
        return isWiderThan(wrapperClass, arg.getClass());
      } else {
        if (arg == null)
          return true;
        return paramType.isAssignableFrom(arg.getClass());
      }
    }

    private static boolean isWiderThan(Class<?> classA, Class<?> classB) {
      assert !classB.isPrimitive();
      assert !classA.isPrimitive();
      Set<Class<?>> widerBoxedClassesForClassA = Reflections.WIDER_TYPES.get(classB);
      return widerBoxedClassesForClassA != null && widerBoxedClassesForClassA.contains(classA);
    }

    private static Method validateMethod(Method method) {
      if (!Modifier.isStatic(method.getModifiers()))
        throw new IllegalArgumentException(String.format("The specified method '%s' is not static", method));
      return method;
    }

    @SuppressWarnings("unchecked")
    static <T> T ensureReturnedValueType(Object value, Class<?> returnType) {
      if (isValidValueForType(value, returnType))
        return (T) value;
      else
        throw new IllegalStateException("Returned value:"
            + formatObject(value)
            + (value != null ? "(" + value.getClass().getName() + ")" : "")
            + " is neither null nor an instance of " + returnType.getName() + ". ");
    }

    public static <T extends CurriedFunction<?, ?>> T requireLast(T value) {
      if (value.hasNext())
        throw new IllegalStateException();
      return value;
    }
  }
}
