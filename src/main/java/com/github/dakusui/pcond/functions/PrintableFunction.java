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


  static abstract class Factory<T, R> {
    private final Function<Object, String> nameComposer;

    abstract static class PrintableFunctionFromFactory<T, R> extends PrintableFunction<T, R> {
      PrintableFunctionFromFactory(Supplier<String> s, Function<? super T, ? extends R> function) {
        super(s, function);
      }

      abstract Factory<T, R> createdFrom();

      abstract Object arg();
    }

    Factory(Function<Object, String> s) {
      this.nameComposer = s;
    }

    PrintableFunction<T, R> create(Object arg) {
      return new PrintableFunctionFromFactory<T, R>(() -> this.nameComposer.apply(arg), createFunction(arg)) {
        @Override
        Factory<T, R> createdFrom() {
          return Factory.this;
        }

        @Override
        Object arg() {
          return arg;
        }

        @Override
        public int hashCode() {
          return Objects.hashCode(arg);
        }

        @Override
        public boolean equals(Object anotherObject) {
          if (this == anotherObject)
            return true;
          if (!(anotherObject instanceof PrintableFunctionFromFactory))
            return false;
          PrintableFunctionFromFactory<?, ?> another = (PrintableFunctionFromFactory<?, ?>) anotherObject;
          return this.createdFrom() == another.createdFrom() && Objects.equals(arg, another.arg());
        }
      };
    }

    abstract Function<? super T, ? extends R> createFunction(Object arg);
  }
}
