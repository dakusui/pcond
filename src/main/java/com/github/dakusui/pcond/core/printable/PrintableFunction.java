package com.github.dakusui.pcond.core.printable;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.currying.CurriedFunction;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class PrintableFunction<T, R> implements CurriedFunction<T, R>, Evaluable.Func<T> {
  private final Function<? super T, ? extends R> function;
  private final Function<? super T, ?>           head;
  private final Evaluable<?>                     tail;

  protected PrintableFunction(Supplier<String> s, Function<? super T, ? extends R> function, Function<? super T, ?> head, Evaluable<?> tail) {
    this.function = Objects.requireNonNull(function);
    this.head = head;
    this.tail = tail;
  }

  protected PrintableFunction(Supplier<String> s, Function<? super T, ? extends R> function) {
    this.function = Objects.requireNonNull(function);
    this.head = this;
    this.tail = null;
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
  public Class<? extends R> returnType() {
    return function instanceof CurriedFunction ?
        (Class<? extends R>) ((CurriedFunction<? super T, ? extends R>) function).returnType() :
        (Class<? extends R>) Object.class;
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
  public int hashCode() {
    return this.function.hashCode();
  }

  @Override
  public boolean equals(Object anotherObject) {
    if (!(anotherObject instanceof PrintableFunction))
      return false;
    @SuppressWarnings("unchecked") PrintableFunction<T, R> another = (PrintableFunction<T, R>) anotherObject;
    return this.function.equals(another.function) && this.toString().equals(another.toString());
  }
}
