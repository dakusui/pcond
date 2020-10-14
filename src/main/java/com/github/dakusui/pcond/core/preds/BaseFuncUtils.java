package com.github.dakusui.pcond.core.preds;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.printable.PrintableFunction;
import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public enum BaseFuncUtils {
  ;

  public static <T, R, E> Factory<T, R, E> factory(Function<E, String> nameComposer, Function<E, Function<T, R>> ff) {
    return new Factory<T, R, E>(nameComposer) {
      @Override
      public Function<T, R> createFunction(E arg) {
        return ff.apply(arg);
      }
    };
  }

  public static abstract class Factory<T, R, E> extends PrintableLambdaFactory<E> {
    public abstract static class PrintableFunctionFromFactory<T, R, E> extends PrintableFunction<T, R> implements Lambda<Factory<T, R, E>, E> {
      public PrintableFunctionFromFactory(Supplier<String> s, Function<? super T, ? extends R> function) {
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

    protected Factory(Function<E, String> s) {
      super(s);
    }

    public PrintableFunction<T, R> create(E arg) {
      Function<? super T, ? extends R> function = createFunction(arg);
      return new PrintableFunctionFromFactory<T, R, E>(() -> this.nameComposer().apply(arg), function) {
        final Spec<E> spec = new Spec<>(Factory.this, arg, PrintableFunctionFromFactory.class);

        @Override
        public Spec<E> spec() {
          return spec;
        }
      };
    }

    public abstract Function<? super T, ? extends R> createFunction(E arg);
  }
}
