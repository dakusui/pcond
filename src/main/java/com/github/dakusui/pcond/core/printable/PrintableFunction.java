package com.github.dakusui.pcond.core.printable;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.core.identifieable.Identifiable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class PrintableFunction<T, R> extends Identifiable.Base implements Evaluable.Func<T>, CurriedFunction<T, R> {
  final         Function<? super T, ? extends R> function;
  private final Function<? super T, ?>           head;
  private final Evaluable<?>                     tail;
  private final Supplier<String>                 formatter;
  private final Function<?, R>                   tailAsFunction;

  @SuppressWarnings("unchecked")
  protected PrintableFunction(Object creator, List<Object> args, Supplier<String> s, Function<? super T, ? extends R> function, Function<? super T, ?> head, Evaluable<?> tail) {
    super(creator, args);
    this.formatter = Objects.requireNonNull(s);
    this.function = requireNonPrintableFunction(unwrap(Objects.requireNonNull(function)));
    this.head = head != null ? head : this;
    this.tail = tail;
    this.tailAsFunction = (Function<?, R>) tail;
  }

  protected PrintableFunction(Object creator, List<Object> args, Supplier<String> s, Function<? super T, ? extends R> function) {
    this(creator, args, s, function, null, null);
  }

  @Override
  public String toString() {
    return this.formatter.get();
  }

  @Override
  public <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
    Objects.requireNonNull(before);
    return PrintableFunctionFactory.<V, T, R>compose(before, this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
    Objects.requireNonNull(after);
    @SuppressWarnings("rawtypes") Function f = this.tailAsFunction == null ? after : PrintableFunctionFactory.compose((Function) this.tailAsFunction, after);
    return PrintableFunctionFactory.compose(this.head, f);
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

  @SuppressWarnings("unchecked")
  public static <T, R> Function<T, R> unwrap(Function<T, R> function) {
    Function<T, R> ret = function;
    if (function instanceof PrintableFunction) {
      ret = (Function<T, R>) ((PrintableFunction<T, R>) function).function;
      assert !(ret instanceof PrintableFunction);
    }
    return ret;
  }

  private static <T, R> Function<T, R> requireNonPrintableFunction(Function<T, R> function) {
    assert !(function instanceof PrintableFunction);
    return function;
  }
}
