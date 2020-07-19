package com.github.dakusui.pcond.functions.chain;

import com.github.dakusui.pcond.functions.Context;
import com.github.dakusui.pcond.functions.MultiFunction;
import com.github.dakusui.pcond.functions.Printables;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static com.github.dakusui.pcond.functions.chain.ChainUtils.*;
import static java.util.Collections.singletonList;
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

  <T, R> CallChain andThen(Function<T, R> func);

  <T> CallChain andThen(Predicate<T> pred);

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
    MultiFunction<Object> p = this.build();
    return Printables.printablePredicate(p.name(), (T value) -> (boolean) p.apply(singletonList(value)));
  }

  static CallChain create(MethodQuery query) {
    return new Impl(null, query);
  }

  static CallChain create(Object target, String metodName, Object... args) {
    return create(instanceMethod(target, metodName, args));
  }

  static CallChain call(String methodName, Object... args) {
    return create(instanceMethod(parameter(0), methodName, args));
  }

  static CallChain fromMultiFunction(MultiFunction<?> func, Assignments assignments) {
    return new Impl(null, func, assignments);
  }

  static <T, R> CallChain fromFunction(Function<T, R> func) {
    return fromMultiFunction(MultiFunction.toMulti(func), Assignments.EMPTY);
  }

  /**
   * An interface that represents "assignments" passed to a Multi-function.
   */
  @FunctionalInterface
  interface Assignments extends Function<Integer, Optional<Object>> {
    Assignments CHAINING = i -> i == 0 ? Optional.of(TAIL) : Optional.empty();
    Assignments EMPTY    = i -> Optional.empty();

    /**
     * A method that should return an argument of index {@code i}.
     *
     * @param i The argument index.
     * @return The {@code i}the argument passed to a multi-function passed to this object.
     */
    @Override
    Optional<Object> apply(Integer i);

    static Assignments create(Object... args) {
      return i -> i < args.length ? Optional.of(args[i]) : Optional.empty();
    }
  }

  class Impl implements CallChain {
    private final CallChain            parent;
    private final MultiFunctionFactory multiFuncFactory;

    public Impl(CallChain parent, MethodQuery methodQuery) {
      this(parent, new MultiFunctionFactory() {
        @Override
        public <R> MultiFunction<R> create(Object actualTailValue) {
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

        @Override
        public int numUnboundParameters() {
          return methodQuery.numUnboundParameters();
        }

        @Override
        public String name() {
          return methodQuery.methodName();
        }
      });
    }

    public Impl(CallChain parent, MultiFunction<?> multiFunc, Assignments assignments) {
      this(parent, new MultiFunctionFactory() {
        @Override
        public <R> MultiFunction<R> create(Object actualTailValue) {
          AtomicInteger j = new AtomicInteger(0);
          return new MultiFunction.Builder<R>(createListFunction(actualTailValue))
              .name(multiFunc.name())
              .addParameters(
                  IntStream.range(0, multiFunc.arity())
                      .mapToObj(i -> assignments.apply(i).orElse(parameter(j.getAndIncrement())))
                      .filter(v -> v instanceof Parameter)
                      .map(v -> Object.class)
                      .collect(toList()))
              .$();
        }

        private <R> Function<List<Object>, R> createListFunction(Object actualTailValue) {
          return new Function<List<Object>, R>() {
            @Override
            public R apply(List<Object> args) {
              //noinspection unchecked
              return (R) multiFunc.apply(args(args));
            }

            public List<Object> args(List<Object> args) {
              AtomicInteger j = new AtomicInteger(0);
              return IntStream.range(0, multiFunc.arity())
                  .mapToObj(i -> assignments.apply(i).orElse(parameter(j.getAndIncrement())))
                  .map(v -> v == TAIL ? actualTailValue : v)
                  .map(v -> v instanceof Parameter ? args.get(((Parameter) v).index()) : v)
                  .collect(toList());
            }

            public String toString() {
              return multiFunc.toString();
            }
          };
        }

        @Override
        public int numUnboundParameters() {
          return (int) IntStream.range(0, multiFunc.arity())
              .filter(i -> !assignments.apply(i).isPresent())
              .count();
        }

        @Override
        public String name() {
          return multiFunc.toString();
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
    public <T, R> CallChain andThen(Function<T, R> func) {
      return new Impl(this, MultiFunction.toMulti(func), Assignments.CHAINING);
    }

    @Override
    public <T> CallChain andThen(Predicate<T> pred) {
      return new Impl(this, MultiFunction.toMulti(pred), i -> i == 0 ? Optional.of(TAIL) : Optional.empty());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> MultiFunction<R> build() {
      if (this.parent == null)
        return toMultiFunction(null);
      MultiFunction<Object> parentFunc = parent.build();

      List<Class<?>> parameterTypes = range(
          0,
          Integer.max(multiFuncFactory.numUnboundParameters(), ((Impl) parent).multiFuncFactory.numUnboundParameters()))
          .mapToObj(i -> Object.class)
          .collect(toList());
      return new MultiFunction.Builder<>(args -> (R) toMultiFunction(parentFunc.apply(parametersFor(args, ((Impl) parent).multiFuncFactory))).apply(parametersFor(args, this.multiFuncFactory)))
          .addParameters(parameterTypes)
          .name(parentFunc.name() + "." + multiFuncFactory.name())
          .$();
    }

    public <R> MultiFunction<R> toMultiFunction(Object actualTailValue) {
      return multiFuncFactory.create(actualTailValue);
    }

    private static List<Object> parametersFor(List<Object> args, MultiFunctionFactory multiFuncFactory) {
      return args.subList(0, multiFuncFactory.numUnboundParameters());
    }

    public interface MultiFunctionFactory {
      <R> MultiFunction<R> create(Object actualTailValue);

      int numUnboundParameters();

      String name();
    }
  }
}
