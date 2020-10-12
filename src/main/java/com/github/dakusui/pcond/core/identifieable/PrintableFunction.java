package com.github.dakusui.pcond.core.identifieable;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.currying.CurriedFunction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class PrintableFunction<T, R> extends Identifiable.Base implements Evaluable.Func<T>, CurriedFunction<T, R> {
  private final Function<? super T, ? extends R> function;
  private final Function<? super T, ?>           head;
  private final Evaluable<?>                     tail;
  private final Supplier<String>                 s;

  protected PrintableFunction(Object creator, List<Object> args, Supplier<String> s, Function<? super T, ? extends R> function, Function<? super T, ?> head, Evaluable<?> tail) {
    super(creator, args);
    this.s = Objects.requireNonNull(s);
    this.function = Objects.requireNonNull(function);
    this.head = head != null ? head : this;
    this.tail = tail;
  }

  protected PrintableFunction(Object creator, List<Object> args, Supplier<String> s, Function<? super T, ? extends R> function) {
    this(creator, args, s, function, null, null);
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
}
