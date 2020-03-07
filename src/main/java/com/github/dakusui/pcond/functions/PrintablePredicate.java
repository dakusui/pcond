package com.github.dakusui.pcond.functions;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class PrintablePredicate<T> implements Predicate<T> {
  final Predicate<? super T> predicate;
  final Supplier<String>     s;

  public PrintablePredicate(Supplier<String> s, Predicate<? super T> predicate) {
    this.predicate = requireNonNull(predicate);
    this.s = requireNonNull(s);
  }

  @Override
  public boolean test(T t) {
    return predicate.test(t);
  }

  @Override
  public Predicate<T> and(Predicate<? super T> other) {
    requireNonNull(other);
    return new PrintablePredicate<>(() -> format("(%s&&%s)", s.get(), other), t -> predicate.test(t) && other.test(t));
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
  public String toString() {
    return s.get();
  }

  static abstract class Factory<T> {
    private final Function<Object, String> nameComposer;

    abstract static class PrintablePredicateFromFactory<T> extends PrintablePredicate<T> {
      PrintablePredicateFromFactory(Supplier<String> s, Predicate<? super T> function) {
        super(s, function);
      }

      abstract Factory<T> createdFrom();

      abstract Object arg();
    }

    Factory(Function<Object, String> s) {
      this.nameComposer = s;
    }

    PrintablePredicate<T> create(Object arg) {
      return new PrintablePredicateFromFactory<T>(() -> this.nameComposer.apply(arg), createPredicate(arg)) {
        @Override
        Factory<T> createdFrom() {
          return Factory.this;
        }

        @Override
        Object arg() {
          return arg;
        }

        @Override
        public int hashCode() {
          return Objects.hashCode(arg);
        }

        @Override
        public boolean equals(Object anotherObject) {
          if (this == anotherObject)
            return true;
          if (!(anotherObject instanceof PrintablePredicate.Factory.PrintablePredicateFromFactory))
            return false;
          PrintablePredicateFromFactory<?> another = (PrintablePredicateFromFactory<?>) anotherObject;
          return this.createdFrom() == another.createdFrom() && Objects.equals(arg, another.arg());
        }
      };
    }
    abstract Predicate<? super T> createPredicate(Object arg);
  }
}
