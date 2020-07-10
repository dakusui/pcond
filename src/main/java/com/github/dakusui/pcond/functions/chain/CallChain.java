package com.github.dakusui.pcond.functions.chain;

import com.github.dakusui.pcond.functions.Context;
import com.github.dakusui.pcond.functions.MultiFunction;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

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

  <R> MultiFunction<R> build();

  default CallChain andThen(String methodName, Object... args) {
    return andThenOn(TAIL, methodName, args);
  }

  default CallChain andThen(Class<?> clazz, String methodName, Object... args) {
    return andThen(classMethod(clazz, methodName, args));
  }

  default CallChain andThenOn(Object target, String methodName, Object... args) {
    return andThen(instanceMethod(target, methodName, args));
  }

  default Predicate<Context> toContextPredicate() {
    return this.build().toContextPredicate();
  }

  default <T> Predicate<T> toPredicate() {
    return (T value) -> (boolean) this.build().apply(Collections.singletonList(value));
  }

  static CallChain create(MethodQuery query) {
    return new Impl(null, query);
  }

  static CallChain create(Object target, String metodName, Object... args) {
    return create(instanceMethod(target, metodName, args));
  }

  interface MultiFunctionFactory {
    <R> MultiFunction<R> create(Object actualTailValue);

    int numUnboundParameters();
  }

  class Impl implements CallChain {
    private final CallChain         parent;
    //private final MethodQuery  methodQuery;
    private final MultiFunctionFactory multiFuncFactory;

    public Impl(CallChain parent, MethodQuery methodQuery) {
      this(parent, new MultiFunctionFactory() {
        @Override
        public <R> MultiFunction<R> create(Object actualTailValue) {
          return toMultiFunction(actualTailValue, methodQuery);
        }

        @Override
        public int numUnboundParameters() {
          return methodQuery.numUnboundParameters();
        }
      });
    }

    public Impl(CallChain parent, MultiFunctionFactory multiFuncFactory) {
      this.parent = parent;
      this.multiFuncFactory = requireNonNull(multiFuncFactory);
    }

    @Override
    public CallChain andThen(MethodQuery methodQuery) {
      return new Impl(this, methodQuery);
    }

    @Override
    public int numParameters() {
      return multiFuncFactory.numUnboundParameters();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> MultiFunction<R> build() {
      List<Class<?>> parameterTypes = range(0, multiFuncFactory.numUnboundParameters())
          .mapToObj(i -> Object.class)
          .collect(toList());
      if (this.parent == null)
        return toMultiFunction(null);
      return new MultiFunction.Builder<>(
          args -> (R) toMultiFunction(applyParent(args)).apply(parametersFor(this, args)))
          .addParameters(parameterTypes)
          .$();
    }

    public <R> MultiFunction<R> toMultiFunction(Object actualTailValue) {
      return multiFuncFactory.create(actualTailValue);
    }

    private Object applyParent(List<Object> args) {
      return parent.build().apply(parametersFor(parent, args));
    }

    private static List<Object> parametersFor(CallChain chain, List<Object> args) {
      return args.subList(0, chain.numParameters());
    }

    private static <R> MultiFunction<R> toMultiFunction(Object actualTailValue, MethodQuery methodQuery) {
      return new MultiFunction.Builder<R>(
          argList -> invokeMethod(
              methodQuery
                  .bindActualArguments(o -> o instanceof Parameter, o -> argList.get(((Parameter) o).index()))
                  .bindActualArguments(o -> o == TAIL, o -> actualTailValue)))
          .addParameters(range(0, methodQuery.numUnboundParameters())
              .mapToObj(i -> Object.class)
              .collect(toList()))
          .name(methodQuery.describe())
          .$();
    }
  }
}
