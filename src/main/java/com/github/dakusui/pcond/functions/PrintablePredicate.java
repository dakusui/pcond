package com.github.dakusui.pcond.functions;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public class PrintablePredicate<T> implements Predicate<T> {
  final Predicate<? super T> predicate;
  final Supplier<String>     s;

  public PrintablePredicate(Supplier<String> s, Predicate<? super T> predicate) {
    this.predicate = requireNonNull(predicate);
    this.s = requireNonNull(s);
  }

  static <T, E> Factory<T, E> factory(Function<E, String> nameComposer, Function<E, Predicate<T>> ff) {
    return new Factory<T, E>(nameComposer) {
      @Override
      Predicate<? super T> createPredicate(E arg) {
        return ff.apply(arg);
      }
    };
  }

  @Override
  public boolean test(T t) {
    return predicate.test(t);
  }

  @Override
  public Predicate<T> and(Predicate<? super T> other) {
    requireNonNull(other);
    final PrintablePredicate.Factory<T, List<Predicate<? super T>>> and_factory = factory(
        (arg) -> format("(%s&&%s)", arg.get(0), arg.get(1)),
        arg -> t -> arg.get(0).test(t) && (arg.get(1)).test(t)
    );
    return and_factory.create(asList(predicate, other));
  }

  @Override
  public Predicate<T> negate() {
    return new PrintablePredicate<>(() -> String.format("!%s", s.get()), predicate.negate());
  }

  @Override
  public Predicate<T> or(Predicate<? super T> other) {
    requireNonNull(other);
    return new PrintablePredicate<>(() -> format("(%s||%s)", s.get(), other), t -> predicate.test(t) || other.test(t));
  }

  @Override
  public int hashCode() {
    return this.predicate.hashCode();
  }

  @Override
  public boolean equals(Object anotherObject) {
    if (this == anotherObject)
      return true;
    if (!(anotherObject instanceof Predicate))
      return false;
    @SuppressWarnings("unchecked") PrintablePredicate<T> another = (PrintablePredicate<T>) anotherObject;
    return this.predicate.equals(another.predicate);
  }

  @Override
  public String toString() {
    return s.get();
  }

  static abstract class Factory<T, E> extends PrintableLambdaFactory<E> {
    abstract static class PrintablePredicateFromFactory<T, E> extends PrintablePredicate<T> implements Lambda<Factory<T, E>, E> {
      PrintablePredicateFromFactory(Supplier<String> s, Predicate<? super T> function) {
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

    Factory(Function<E, String> s) {
      super(s);
    }

    PrintablePredicate<T> create(E arg) {
      Lambda.Spec<E> spec = new Lambda.Spec<>(Factory.this, arg, PrintablePredicateFromFactory.class);
      return new PrintablePredicateFromFactory<T, E>(() -> this.nameComposer().apply(arg), createPredicate(arg)) {
        @Override
        public Spec<E> spec() {
          return spec;
        }
      };
    }

    abstract Predicate<? super T> createPredicate(E arg);
  }
}
