package com.github.dakusui.pcond.functions;

import java.util.Objects;
import java.util.function.Function;

public class PrintableFunction<T, R> implements Function<T, R> {
  private final String                           s;
  private final Function<? super T, ? extends R> function;

  PrintableFunction(String s, Function<? super T, ? extends R> function) {
    this.s = Objects.requireNonNull(s);
    this.function = Objects.requireNonNull(function);
  }

  @Override
  public R apply(T t) {
    return this.function.apply(t);
  }

  public <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
    Objects.requireNonNull(before);
    return new PrintableFunction<>(String.format("%s->%s", before, s), this.function.compose(before));
  }

  public <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
    Objects.requireNonNull(after);
    return new PrintableFunction<>(String.format("%s->%s", s, after), this.function.andThen(after));
  }

  @Override
  public String toString() {
    return s;
  }

  public static <T, R> PrintableFunction<T, R> create(String s, Function<? super T, ? extends R> function) {
    return new PrintableFunction<>(s, function);
  }
}
