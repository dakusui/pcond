package com.github.dakusui.pcond.functions;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class PrintableFunction<T, R> implements Function<T, R> {
  private final Supplier<String>                 s;
  private final Function<? super T, ? extends R> function;

  PrintableFunction(Supplier<String> s, Function<? super T, ? extends R> function) {
    this.s = Objects.requireNonNull(s);
    this.function = Objects.requireNonNull(function);
  }

  @Override
  public R apply(T t) {
    return this.function.apply(t);
  }

  public <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
    Objects.requireNonNull(before);
    return new PrintableFunction<>(() -> String.format("%s->%s", before, s.get()), this.function.compose(before));
  }

  public <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
    Objects.requireNonNull(after);
    return new PrintableFunction<>(() -> String.format("%s->%s", s.get(), after), this.function.andThen(after));
  }

  @Override
  public String toString() {
    return s.get();
  }

  public static <T, R> PrintableFunction<T, R> create(String s, Function<? super T, ? extends R> function) {
    return new PrintableFunction<>(() -> Objects.requireNonNull(s), function);
  }


  static abstract class Factory<T, R, E> extends PrintableLambdaFactory<E> {

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

    PrintableFunction<T, R> create(E arg) {
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
