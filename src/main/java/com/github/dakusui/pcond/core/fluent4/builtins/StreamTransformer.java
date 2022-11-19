package com.github.dakusui.pcond.core.fluent4.builtins;

import com.github.dakusui.pcond.core.fluent4.AbstractObjectTransformer;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface StreamTransformer<
    T,
    E> extends
    AbstractObjectTransformer<
        StreamTransformer<T, E>,
        StreamChecker<T, E>,
        T,
        Stream<E>
        > {
  static <E> StreamTransformer<Stream<E>, E> create(Supplier<Stream<E>> value) {
    return new Impl<>(value, trivialIdentityFunction());
  }

  class Impl<
      T,
      E> extends
      Base<
          StreamTransformer<T, E>,
          StreamChecker<T, E>,
          T,
          Stream<E>> implements
      StreamTransformer<T, E> {

    public Impl(Supplier<T> rootValue, Function<T, Stream<E>> root) {
      super(rootValue, root);
    }

    @Override
    protected StreamChecker<T, E> toChecker(Function<T, Stream<E>> transformFunction) {
      return new StreamChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected StreamTransformer<Stream<E>, E> rebase() {
      return new StreamTransformer.Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
