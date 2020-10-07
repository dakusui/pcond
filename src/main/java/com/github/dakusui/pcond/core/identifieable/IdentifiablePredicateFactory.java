package com.github.dakusui.pcond.core.identifieable;

import com.github.dakusui.pcond.core.Evaluable;

import java.util.List;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.toEvaluableIfNecessary;
import static java.lang.String.format;

enum IdentifiablePredicateFactory {
  FOR_NEGATION {
    @Override
    <T> PrintablePredicate<T> create(List<Object> args) {
      @SuppressWarnings("unchecked") final PrintablePredicate<T> predicate = (PrintablePredicate<T>) args.get(0);
      final Evaluable<T> target = toEvaluableIfNecessary(predicate);
      class Ret extends PrintablePredicate<T> implements Evaluable.Negation<T> {
        @SuppressWarnings("unchecked")
        protected Ret(PrintablePredicate<T> predicate) {
          super(
              FOR_NEGATION,
              args,
              () -> format("!%s", predicate),
              (t) -> unwrapIfPrintablePredicate((Predicate<Object>) predicate).negate().test(t));
        }

        @Override
        public Evaluable<? super T> target() {
          return target;
        }
      }
      return new Ret(predicate);
    }
  },
  FOR_CONJUNCTION {
    @Override
    <T> PrintablePredicate<T> create(List<Object> args) {
      @SuppressWarnings("unchecked") final PrintablePredicate<T> predicate = (PrintablePredicate<T>) args.get(0);
      @SuppressWarnings("unchecked") final PrintablePredicate<T> other = (PrintablePredicate<T>) args.get(1);
      class Ret extends PrintablePredicate<T> implements Evaluable.Conjunction<T> {
        final Evaluable<T> a = toEvaluableIfNecessary(predicate);
        final Evaluable<T> b = toEvaluableIfNecessary(other);

        protected Ret(PrintablePredicate<T> predicate) {
          super(
              FOR_CONJUNCTION,
              args,
              () -> format("(%s&&%s)", predicate, other),
              unwrapIfPrintablePredicate(predicate).and(unwrapIfPrintablePredicate(other)));
        }

        @Override
        public Evaluable<? super T> a() {
          return a;
        }

        @Override
        public Evaluable<? super T> b() {
          return b;
        }
      }
      return new Ret(predicate);
    }
  },
  FOR_DISJUNCTION {
    @Override
    <T> PrintablePredicate<T> create(List<Object> args) {
      @SuppressWarnings("unchecked") final PrintablePredicate<T> predicate = (PrintablePredicate<T>) args.get(0);
      @SuppressWarnings("unchecked") final PrintablePredicate<T> other = (PrintablePredicate<T>) args.get(1);
      class Ret extends PrintablePredicate<T> implements Evaluable.Disjunction<T> {
        final Evaluable<T> a = toEvaluableIfNecessary(predicate);
        final Evaluable<T> b = toEvaluableIfNecessary(other);

        protected Ret(PrintablePredicate<T> predicate) {
          super(
              FOR_DISJUNCTION,
              args,
              () -> format("(%s||%s)", predicate, other),
              unwrapIfPrintablePredicate(predicate).or(unwrapIfPrintablePredicate(other)));
        }

        @Override
        public Evaluable<? super T> a() {
          return a;
        }

        @Override
        public Evaluable<? super T> b() {
          return b;
        }
      }
      return new Ret(predicate);
    }
  };

  abstract <T> PrintablePredicate<T> create(List<Object> args);

  static <T> Predicate<T> unwrapIfPrintablePredicate(Predicate<T> predicate) {
    Predicate<T> ret = predicate;
    if (predicate instanceof PrintablePredicate)
      ret = unwrapIfPrintablePredicate(((PrintablePredicate<T>) predicate).predicate);
    return ret;
  }
}
