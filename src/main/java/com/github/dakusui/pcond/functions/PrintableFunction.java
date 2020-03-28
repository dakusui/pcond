package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.functions.currying.CurriedFunction;
import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

public class PrintableFunction<T, R> implements CurriedFunction<T, R> {
  private static final Factory<Object, Object, List<Function<Object, Object>>> COMPOSE_FACTORY = PrintableFunction.factory(
      arg -> String.format("%s->%s", arg.get(0), arg.get(1)),
      arg -> p -> unwrapIfPrintableFunction(arg.get(1)).compose(unwrapIfPrintableFunction(arg.get(0))).apply(p)
  );

  private static final Factory<Object, Object, List<Function<Object, Object>>> ANDTHEN_FACTORY = PrintableFunction.factory(
      arg -> String.format("%s->%s", arg.get(0), arg.get(1)),
      arg -> p -> unwrapIfPrintableFunction(arg.get(1)).compose(unwrapIfPrintableFunction(arg.get(0))).apply(p)
  );

  private final Supplier<String> s;
  private final Function<? super T, ? extends R> function;

  protected PrintableFunction(Supplier<String> s, Function<? super T, ? extends R> function) {
    this.s = Objects.requireNonNull(s);
    this.function = Objects.requireNonNull(function);
  }


  @SuppressWarnings({"unchecked"})
  public <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
    Objects.requireNonNull(before);
    return (Function<V, R>) COMPOSE_FACTORY.create(asList((Function<Object, Object>) before, (Function<Object, Object>) this));
  }

  @SuppressWarnings({"unchecked"})
  public <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
    Objects.requireNonNull(after);
    return (Function<T, V>) ANDTHEN_FACTORY.create(asList((Function<Object, Object>) this, (Function<Object, Object>) after));
  }

  @Override
  public String toString() {
    return s.get();
  }

  public static <T, R> PrintableFunction<T, R> create(String s, Function<? super T, ? extends R> function) {
    return new PrintableFunction<>(() -> Objects.requireNonNull(s), function);
  }

  static <T, R, E> Factory<T, R, E> factory(Function<E, String> nameComposer, Function<E, Function<T, R>> ff) {
    return new Factory<T, R, E>(nameComposer) {
      @Override
      Function<T, R> createFunction(E arg) {
        return ff.apply(arg);
      }
    };
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

  public static abstract class Factory<T, R, E> extends PrintableLambdaFactory<E> {

    abstract static class PrintableFunctionFromFactory<T, R, E> extends PrintableFunction<T, R> implements Lambda<Factory<T, R, E>, E> {
      PrintableFunctionFromFactory(Supplier<String> s, Function<? super T, ? extends R> function) {
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
      Lambda.Spec<E> spec = new Lambda.Spec<>(Factory.this, arg, PrintableFunctionFromFactory.class);
      return new PrintableFunctionFromFactory<T, R, E>(
          () -> this.nameComposer().apply(arg),
          createFunction(arg)) {
        @Override
        public Spec<E> spec() {
          return spec;
        }
      };
    }

    abstract Function<? super T, ? extends R> createFunction(E arg);
  }
}
