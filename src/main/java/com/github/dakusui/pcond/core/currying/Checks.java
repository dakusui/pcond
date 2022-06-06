package com.github.dakusui.pcond.core.currying;

import com.github.dakusui.pcond.core.refl.ReflUtils;
import com.github.dakusui.pcond.internals.InternalChecks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalChecks.requireArgument;
import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static com.github.dakusui.pcond.internals.InternalUtils.wrapperClassOf;
import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

public enum Checks {
  ;
  private static final Set<Class<?>> PRIMITIVE_WRAPPERS = new HashSet<Class<?>>() {{
    add(Integer.class);
    add(Long.class);
    add(Boolean.class);
    add(Byte.class);
    add(Character.class);
    add(Float.class);
    add(Double.class);
    add(Short.class);
    add(Void.class);
  }};

  public static <T extends CurriedFunction<?, ?>> T requireLast(T value) {
    if (value.hasNext())
      throw new IllegalStateException();
    return value;
  }

  public static List<Integer> validateParamOrderList(List<Integer> order, int numParameters) {
    final List<Integer> paramOrder = unmodifiableList(order.stream().distinct().collect(toList()));
    requireArgument(order, o -> o.size() == paramOrder.size(), () -> "Duplicated elements are found in the 'order' argument:" + order.toString() + " " + paramOrder);
    requireArgument(order, o -> o.size() == numParameters, () -> "Inconsistent number of parameters are supplied by 'order'. Expected:" + numParameters + ", Actual: " + order.size());
    return paramOrder;
  }

  /**
   * Validates if a given argument value is appropriate for a parameter type (`paramType`).
   *
   * @param arg An argument value is to check with `paramType`.
   * @param paramType
   * @param messageFormatter  A message formatter which generates a message on a failure.
   * @param <T> The type of the argument value.
   * @return The `arg` value itself.
   */
  public static <T> T validateArgumentType(T arg, Class<?> paramType, Supplier<String> messageFormatter) {
    InternalChecks.checkArgument(isValidValueForType(arg, paramType), messageFormatter);
    return arg;
  }

  static boolean isValidValueForType(Object arg, Class<?> paramType) {
    if (paramType.isPrimitive()) {
      if (arg == null)
        return paramType.equals(void.class);
      if (isPrimitiveWrapperClassOrPrimitive(arg.getClass())) {
        Class<?> wrapperClassForParamType = wrapperClassOf(paramType);
        if (wrapperClassForParamType.equals(arg.getClass()))
          return true;
        return isWiderThan(wrapperClassForParamType, arg.getClass());
      }
      return false;
    } else {
      if (arg == null)
        return true;
      return paramType.isAssignableFrom(arg.getClass());
    }
  }

  /**
   * @param classA A non-primitive type class.
   * @param classB Another non-primitive type class.
   * @return {@code true} iff {@code classA} is a "wider" wrapper class than {@code classB}.
   */
  public static boolean isWiderThan(Class<?> classA, Class<?> classB) {
    requireArgument(classA, Checks::isPrimitiveWrapperClass, () -> format("'%s' is not a primitive wrapper class", classA));
    requireArgument(classB, Checks::isPrimitiveWrapperClass, () -> format("'%s' is not a primitive wrapper class", classB));
    Set<Class<?>> widerBoxedClassesForClassA = widerTypesThan(classB);
    return widerBoxedClassesForClassA.contains(classA);
  }

  public static boolean isWiderThanOrEqualTo(Class<?> classA, Class<?> classB) {
    requireArgument(classA, Checks::isPrimitiveWrapperClass, () -> format("'%s' is not a primitive wrapper class", classA));
    requireArgument(classB, Checks::isPrimitiveWrapperClass, () -> format("'%s' is not a primitive wrapper class", classB));
    return classA.equals(classB) || isWiderThan(classA, classB);
  }

  private static Set<Class<?>> widerTypesThan(Class<?> classB) {
    return ReflUtils.WIDER_TYPES.getOrDefault(classB, emptySet());
  }

  public static boolean isPrimitiveWrapperClass(Class<?> aClass) {
    return PRIMITIVE_WRAPPERS.contains(aClass);
  }

  public static boolean isPrimitiveWrapperClassOrPrimitive(Class<?> aClass) {
    return aClass.isPrimitive() || isPrimitiveWrapperClass(aClass);
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
}
