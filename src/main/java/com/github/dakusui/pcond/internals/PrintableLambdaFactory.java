package com.github.dakusui.pcond.internals;

import java.util.Objects;
import java.util.function.Function;

public abstract class PrintableLambdaFactory<E> {
  private final Function<E, String> nameComposer;

  protected PrintableLambdaFactory(Function<E, String> nameComposer) {
    this.nameComposer = nameComposer;
  }

  public Function<E, String> nameComposer() {
    return this.nameComposer;
  }

  public interface Lambda<F extends PrintableLambdaFactory<E>, E> {
    class Spec<E> {
      final PrintableLambdaFactory<E> factory;
      final E                         arg;
      final Class<?>                  type;

      public Spec(PrintableLambdaFactory<E> factory, E arg, Class<?> type) {
        this.factory = factory;
        this.arg = arg;
        this.type = type;
      }
    }

    Spec<E> spec();

    @SuppressWarnings("unchecked")
    default F createdFrom() {
      return (F) spec().factory;
    }

    default Object arg() {
      return spec().arg;
    }

    @SuppressWarnings("unchecked")
    default Class<? extends Lambda<?, E>> type() {
      return (Class<? extends Lambda<?, E>>) spec().type;
    }

    default <T extends Lambda<?, E>> boolean equals(Object anotherObject, Class<T> intendedClass) {
      if (this == anotherObject)
        return true;
      if (!(intendedClass.isInstance(anotherObject)))
        return false;
      T another = intendedClass.cast(anotherObject);
      return this.createdFrom() == another.createdFrom() && Objects.equals(arg(), another.arg());
    }
  }
}
