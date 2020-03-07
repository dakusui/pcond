package com.github.dakusui.pcond.functions;

import java.util.Objects;
import java.util.function.Function;

abstract class PrintableLambdaFactory {
  private final Function<Object, String> nameComposer;

  protected PrintableLambdaFactory(Function<Object, String> nameComposer) {
    this.nameComposer = nameComposer;
  }

  public Function<Object, String> nameComposer() {
    return this.nameComposer;
  }

  interface Lambda<F extends PrintableLambdaFactory> {
    class Spec {
      final PrintableLambdaFactory factory;
      final Object                 arg;
      final Class<?>               type;

      public Spec(PrintableLambdaFactory factory, Object arg, Class<?> type) {
        this.factory = factory;
        this.arg = arg;
        this.type = type;
      }
    }

    Spec spec();

    @SuppressWarnings("unchecked")
    default F createdFrom() {
      return (F) spec().factory;
    }

    default Object arg() {
      return spec().arg;
    }

    @SuppressWarnings("unchecked")
    default Class<? extends Lambda<?>> type() {
      return (Class<? extends Lambda<?>>) spec().type;
    }

    default <T extends Lambda<?>> boolean equals(Object anotherObject, Class<T> intendedClass) {
      if (this == anotherObject)
        return true;
      if (!(intendedClass.isInstance(anotherObject)))
        return false;
      T another = intendedClass.cast(anotherObject);
      return this.createdFrom() == another.createdFrom() && Objects.equals(arg(), another.arg());
    }
  }
}
