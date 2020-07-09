package com.github.dakusui.pcond.functions.chain;

import com.github.dakusui.pcond.functions.MultiFunction;

import java.util.Arrays;
import java.util.List;

import static com.github.dakusui.pcond.functions.chain.ChainUtils.*;
import static java.lang.Math.max;
import static java.util.Comparator.comparingInt;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public interface CallChain {

  /**
   * Adds a call of a method specified by {@code object}, {@code methodName} and
   * {@code args}.
   *
   * @return This object.
   */
  CallChain andThen(MethodQuery methodQuery);

  int numParameters();

  default CallChain andThen(String methodName, Object... args) {
    return andThenOn(parameter(0), methodName, args);
  }

  default CallChain andThen(Class<?> clazz, String methodName, Object... args) {
    return andThen(classMethod(clazz, methodName, args));
  }

  default CallChain andThenOn(Object target, String methodName, Object... args) {
    return andThen(instanceMethod(target, methodName, args));
  }

  <R> MultiFunction<R> build();

  static CallChain create(MethodQuery query) {
    return new Impl(null, query);
  }

  static CallChain create(Object target, String metodName, Object... args) {
    return create(instanceMethod(target, metodName, args));
  }

  class Impl implements CallChain {
    private final CallChain   parent;
    private final MethodQuery methodQuery;

    public Impl(CallChain parent, MethodQuery methodQuery) {
      this.parent = parent;
      this.methodQuery = requireNonNull(methodQuery);
    }

    @Override
    public CallChain andThen(MethodQuery methodQuery) {
      return new Impl(this, methodQuery);
    }

    @Override
    public int numParameters() {
      int parentNumParameters = 0;
      return max(
          parentNumParameters,
          Arrays.stream(methodQuery.arguments())
              .filter(each -> each instanceof Parameter)
              .map(each -> (Parameter) each)
              .map(Parameter::index)
              .map(i -> i + 1)
              .max(comparingInt(o -> o))
              .orElse(0));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> MultiFunction<R> build() {
      if (this.parent == null)
        return methodCall(this.methodQuery);
      List<Class<?>> parameterTypes = range(0, numParameters())
          .mapToObj(i -> Object.class)
          .collect(toList());
      return this.methodQuery.isStatic() ?
          new MultiFunction.Builder<>(args -> {
            Object tail = this.parent.build().apply(args);
            return (R) methodCall(classMethod(
                this.methodQuery.targetClass(),
                this.methodQuery.methodName(),
                this.methodQuery.arguments())).apply(args);
          })
              .addParameters(parameterTypes)
              .$() :
          new MultiFunction.Builder<>(args -> {
            Object tail = this.parent.build().apply(args.subList(0, this.parent.numParameters()));
            return (R) methodCall(instanceMethod(
                tail,
                this.methodQuery.methodName(),
                this.methodQuery.arguments())).apply(args.subList(0, this.numParameters()));
          })
              .addParameters(parameterTypes)
              .$();
    }
  }
}
