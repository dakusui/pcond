package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.toEvaluableIfNecessary;
import static java.util.Arrays.asList;

public class PrintableFunction<T, R> implements CurriedFunction<T, R>, Evaluable.Func<T> {
  private static final Factory<Object, Object, List<Function<Object, Object>>> COMPOSE_FACTORY = PrintableFunction.composedFunctionFactory(
      arg -> String.format("%s->%s", arg.get(0), arg.get(1)),
      arg -> p -> unwrapIfPrintableFunction(arg.get(1)).compose(unwrapIfPrintableFunction(arg.get(0))).apply(p)
  );

  private static final Factory<Object, Object, List<Function<Object, Object>>> ANDTHEN_FACTORY = PrintableFunction.composedFunctionFactory(
      arg -> String.format("%s->%s", arg.get(0), arg.get(1)),
      arg -> p -> unwrapIfPrintableFunction(arg.get(1)).compose(unwrapIfPrintableFunction(arg.get(0))).apply(p)
  );

  private final Supplier<String>                 s;
  private final Function<? super T, ? extends R> function;
  private final Function<? super T, ?>           head;
  private final Evaluable<?>                     tail;

  protected PrintableFunction(Supplier<String> s, Function<? super T, ? extends R> function, Function<? super T, ?> head, Evaluable<?> tail) {
    this.s = Objects.requireNonNull(s);
    this.function = Objects.requireNonNull(function);
    this.head = head;
    this.tail = tail;
  }

  protected PrintableFunction(Supplier<String> s, Function<? super T, ? extends R> function) {
    this.s = Objects.requireNonNull(s);
    this.function = Objects.requireNonNull(function);
    this.head = this;
    this.tail = null;
  }


  @SuppressWarnings({ "unchecked" })
  public <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
    Objects.requireNonNull(before);
    return (Function<V, R>) COMPOSE_FACTORY.create(asList((Function<Object, Object>) before, (Function<Object, Object>) this));
  }

  @SuppressWarnings({ "unchecked" })
  public <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
    Objects.requireNonNull(after);
    return (Function<T, V>) ANDTHEN_FACTORY.create(asList((Function<Object, Object>) this, (Function<Object, Object>) after));
  }

  @SuppressWarnings("unchecked")
  private static Function<Object, Object> unwrapIfPrintableFunction(Function<Object, Object> function) {
    Function<Object, Object> ret = function;
    if (function instanceof PrintableFunction)
      ret = (Function<Object, Object>) ((PrintableFunction<Object, Object>) function).function;
    return ret;
  }

  @Override
  public R applyFunction(T value) {
    return this.function.apply(value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<?> parameterType() {
    return function instanceof CurriedFunction ?
        ((CurriedFunction<? super T, ? extends R>) function).parameterType() :
        Object.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<?> returnType() {
    return function instanceof CurriedFunction ?
        ((CurriedFunction<? super T, ? extends R>) function).returnType() :
        Object.class;
  }

  @Override
  public Function<? super T, ?> head() {
    return this.head;
  }

  @Override
  public Optional<Evaluable<?>> tail() {
    return Optional.ofNullable(this.tail);
  }

  @Override
  public String toString() {
    return s.get();
  }

  public static <T, R> PrintableFunction<T, R> create(String s, Function<? super T, ? extends R> function) {
    return new PrintableFunction<>(() -> Objects.requireNonNull(s), function);
  }

  public static <T, R, E> Factory<T, R, E> factory(Function<E, String> nameComposer, Function<E, Function<T, R>> ff) {
    return new Factory<T, R, E>(nameComposer) {
      @Override
      Function<T, R> createFunction(E arg) {
        return ff.apply(arg);
      }
    };
  }

  public static <T, R> Factory<T, R, List<Function<Object, Object>>> composedFunctionFactory(
      Function<List<Function<Object, Object>>, String> nameComposer, Function<List<Function<Object, Object>>, Function<T, R>> ff) {
    return new Factory<T, R, List<Function<Object, Object>>>(nameComposer) {
      public PrintableFunction<T, R> create(List<Function<Object, Object>> arg) {
        final Lambda.Spec<List<Function<Object, Object>>> spec = new Lambda.Spec<>(this, arg, PrintableFunctionFromFactory.class);
        final Function<? super T, ? extends R> function = createFunction(arg);
        return new PrintableFunctionFromFactory<T, R, List<Function<Object, Object>>>(
            () -> this.nameComposer().apply(arg), function, createHead(arg), createTail(arg)) {

          @Override
          public Spec<List<Function<Object, Object>>> spec() {
            return spec;
          }
        };
      }

      @Override
      Function<T, R> createFunction(List<Function<Object, Object>> arg) {
        return ff.apply(arg);
      }

      @SuppressWarnings("unchecked")
      Function<? super T, ? extends R> createHead(List<Function<Object, Object>> arg) {
        return (Function<? super T, ? extends R>) arg.get(0);
      }

      Evaluable<T> createTail(List<Function<Object, Object>> arg) {
        return toEvaluableIfNecessary(arg.get(1));
      }
    };
  }

  public static abstract class Factory<T, R, E> extends PrintableLambdaFactory<E> {
    abstract static class PrintableFunctionFromFactory<T, R, E> extends PrintableFunction<T, R> implements Lambda<Factory<T, R, E>, E> {
      PrintableFunctionFromFactory(Supplier<String> s, Function<? super T, ? extends R> function, Function<? super T, ?> head, Evaluable<?> tail) {
        super(s, function, head, tail);
      }

      public PrintableFunctionFromFactory(Supplier<String> s, Function<? super T, ? extends R> function) {
        super(s, function);
      }

      @Override
      public int hashCode() {
        return Objects.hashCode(arg());
      }

      @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
      @Override
      public boolean equals(Object anotherObject) {
        return equals(anotherObject, type());
      }
    }

    Factory(Function<E, String> s) {
      super(s);
    }

    public PrintableFunction<T, R> create(E arg) {
      Function<? super T, ? extends R> function = createFunction(arg);
      return new PrintableFunctionFromFactory<T, R, E>(() -> this.nameComposer().apply(arg), function) {
        final Lambda.Spec<E> spec = new Lambda.Spec<>(Factory.this, arg, PrintableFunctionFromFactory.class);

        @Override
        public Spec<E> spec() {
          return spec;
        }
      };
    }

    abstract Function<? super T, ? extends R> createFunction(E arg);
  }
}
