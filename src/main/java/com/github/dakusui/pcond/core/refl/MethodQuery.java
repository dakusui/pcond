package com.github.dakusui.pcond.core.refl;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.refl.ReflUtils.replacePlaceHolderWithActualArgument;
import static com.github.dakusui.pcond.internals.InternalChecks.requireArgument;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public interface MethodQuery {
  static MethodQuery instanceMethod(Object targetObject, String methodName, Object... arguments) {
    return create(false, requireNonNull(targetObject), ReflUtils.targetTypeOf(targetObject), methodName, arguments);
  }

  static MethodQuery classMethod(Class<?> targetClass, String methodName, Object... arguments) {
    return create(true, null, targetClass, methodName, arguments);
  }

  boolean isStatic();

  Object targetObject();

  Class<?> targetClass();

  String methodName();

  Object[] arguments();

  String describe();

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
