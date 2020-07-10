package com.github.dakusui.pcond.functions.chain;

import com.github.dakusui.pcond.functions.MultiFunction;

import java.util.List;

import static com.github.dakusui.pcond.functions.chain.ChainUtils.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public interface CallChain {
  /**
   * A place holder to represent an object on which a method call
   */
  Object TAIL = new Object() {
    public String toString() {
      return "(TAIL)";
    }
  };

  /**
   * Adds a call of a method specified by {@code object}, {@code methodName} and
   * {@code args}.
   *
   * @return This object.
   */
  CallChain andThen(MethodQuery methodQuery);

  int numParameters();

  default CallChain andThen(String methodName, Object... args) {
    return andThenOn(TAIL, methodName, args);
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
      return methodQuery.numUnboundParameters();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> MultiFunction<R> build() {
      List<Class<?>> parameterTypes = range(0, methodQuery.numUnboundParameters())
          .mapToObj(i -> Object.class)
          .collect(toList());
      if (this.parent == null)
        return methodCall(this.methodQuery);
      return new MultiFunction.Builder<>(args -> {
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
