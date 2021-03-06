package com.github.dakusui.pcond.core.currying;

import com.github.dakusui.pcond.core.refl.ReflUtils;
import com.github.dakusui.pcond.internals.InternalChecks;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalChecks.requireArgument;
import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static com.github.dakusui.pcond.internals.InternalUtils.wrapperClassOf;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

public enum Checks {
  ;

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

  public static <T> T validateArgumentType(T arg, Class<?> paramType, Supplier<String> messageFormatter) {
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

  /**
   * @param classA A non-primitive type class.
   * @param classB Another non-primitive type class.
   * @return {@code true} iff {@code classA} is a "wider" wrapper class than {@code classB}.
   */
  public static boolean isWiderThan(Class<?> classA, Class<?> classB) {
    assert !classB.isPrimitive();
    assert !classA.isPrimitive();
    Set<Class<?>> widerBoxedClassesForClassA = widerTypesThan(classB);
    return widerBoxedClassesForClassA.contains(classA);
  }

  public static boolean isWiderThanOrEqualTo(Class<?> classA, Class<?> classB) {
    assert !classB.isPrimitive();
    assert !classA.isPrimitive();
    return classA.equals(classB) || isWiderThan(classA, classB);
  }

  private static Set<Class<?>> widerTypesThan(Class<?> classB) {
    assert !classB.isPrimitive();
    return ReflUtils.WIDER_TYPES.getOrDefault(classB, emptySet());
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
