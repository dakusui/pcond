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

  static abstract class Factory<T> extends PrintableLambdaFactory {
    abstract static class PrintablePredicateFromFactory<T> extends PrintablePredicate<T> implements Lambda<Factory<T>> {
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

    Factory(Function<Object, String> s) {
      super(s);
    }

    PrintablePredicate<T> create(Object arg) {
      Lambda.Spec spec = new Lambda.Spec(Factory.this, arg, PrintablePredicateFromFactory.class);
      return new PrintablePredicateFromFactory<T>(() -> this.nameComposer().apply(arg), createPredicate(arg)) {
        @Override
        public Spec spec() {
          return spec;
        }
      };
    }

    abstract Predicate<? super T> createPredicate(Object arg);
  }
}
