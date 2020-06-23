package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.functions.preds.BaseFuncUtils;
import com.github.dakusui.pcond.functions.preds.ComposedFuncUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

public class PrintableFunction<T, R> implements CurriedFunction<T, R>, Evaluable.Func<T> {
  private static final BaseFuncUtils.Factory<Object, Object, List<Function<Object, Object>>> COMPOSE_FACTORY = ComposedFuncUtils.factory(
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
    return (Function<T, V>) COMPOSE_FACTORY.create(asList((Function<Object, Object>) this, (Function<Object, Object>) after));
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

  @Override
  public String toString() {
    return s.get();
  }

  public static <T, R> PrintableFunction<T, R> create(String s, Function<? super T, ? extends R> function) {
    return new PrintableFunction<>(() -> Objects.requireNonNull(s), function);
  }

}
