package com.github.dakusui.pcond.core.identifieable;

import com.github.dakusui.pcond.core.Evaluable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.toEvaluableIfNecessary;
import static java.lang.String.format;

enum IdentifiablePredicateFactory {
  FOR_NEGATION {
    class Negation<T> extends PrintablePredicate<T> implements Evaluable.Negation<T> {
      final Evaluable<T> target;

      @SuppressWarnings("unchecked")
      protected Negation(PrintablePredicate<T> predicate, List<Object> args) {
        super(
            FOR_NEGATION,
            args,
            () -> format("!%s", predicate),
            (t) -> unwrapIfPrintablePredicate((Predicate<Object>) predicate).negate().test(t));
        target = toEvaluableIfNecessary(predicate);
      }

      @Override
      public Evaluable<? super T> target() {
        return target;
      }
    }

    @Override
    <T> PrintablePredicate<T> create(List<Object> args) {
      @SuppressWarnings("unchecked") final PrintablePredicate<T> predicate = (PrintablePredicate<T>) args.get(0);
      return new Negation<T>(predicate, args);
    }
  },
  FOR_CONJUNCTION {
    class Conjunction<T> extends Junction<T> implements Evaluable.Conjunction<T> {
      protected Conjunction(PrintablePredicate<T> predicate, PrintablePredicate<T> other, List<Object> args) {
        super(predicate, other, FOR_CONJUNCTION, args, () -> format("(%s||%s)", predicate, other), (p, o) -> unwrapIfPrintablePredicate(p).or(unwrapIfPrintablePredicate(o)));
      }
    }

    @Override
    <T> PrintablePredicate<T> create(List<Object> args) {
      @SuppressWarnings("unchecked") final PrintablePredicate<T> predicate = (PrintablePredicate<T>) args.get(0);
      @SuppressWarnings("unchecked") final PrintablePredicate<T> other = (PrintablePredicate<T>) args.get(1);
      return new Conjunction<T>(predicate, other, args);
    }
  },
  FOR_DISJUNCTION {
    class Disjunction<T> extends Junction<T> implements Evaluable.Disjunction<T> {
      protected Disjunction(PrintablePredicate<T> predicate, PrintablePredicate<T> other, List<Object> args) {
        super(predicate, other, FOR_DISJUNCTION, args, () -> format("(%s&&%s)", predicate, other), (p, o) -> unwrapIfPrintablePredicate(p).and(unwrapIfPrintablePredicate(o)));
      }
    }

    @Override
    <T> PrintablePredicate<T> create(List<Object> args) {
      @SuppressWarnings("unchecked") final PrintablePredicate<T> predicate = (PrintablePredicate<T>) args.get(0);
      @SuppressWarnings("unchecked") final PrintablePredicate<T> other = (PrintablePredicate<T>) args.get(1);
      return new Disjunction<T>(predicate, other, args);
    }
  },
  FOR_LEAF {
    @Override
    <T> PrintablePredicate<T> create(List<Object> args) {
      @SuppressWarnings("unchecked") final Supplier<String> formatter = (Supplier<String>) args.get(0);
      @SuppressWarnings("unchecked") final PrintablePredicate<T> predicate = (PrintablePredicate<T>) args.get(1);
      return new LeafPredicate<>(this, args, formatter, predicate);
    }
  },
  FOR_PARAMETERIZED_LEAF {
    @Override
    <T> PrintablePredicate<T> create(List<Object> args) {
      @SuppressWarnings("unchecked") final Function<List<Object>, Supplier<String>> formatterFactory
          = (Function<List<Object>, Supplier<String>>) args.get(0);
      @SuppressWarnings("unchecked") final Function<List<Object>, PrintablePredicate<T>> predicateFactory
          = (Function<List<Object>, PrintablePredicate<T>>) args.get(1);
      List<Object> args_ = args.subList(2, args.size());
      return new LeafPredicate<>(predicateFactory, args_, formatterFactory.apply(args_), predicateFactory.apply(args_));
    }
  };

  abstract <T> PrintablePredicate<T> create(List<Object> args);

  @SuppressWarnings("unchecked")
  static <T> Predicate<T> unwrapIfPrintablePredicate(Predicate<? super T> predicate) {
    Predicate<? super T> ret = predicate;
    if (predicate instanceof PrintablePredicate)
      ret = unwrapIfPrintablePredicate(((PrintablePredicate<? super T>) predicate).predicate);
    return (Predicate<T>) ret;
  }

  static class LeafPredicate<T> extends PrintablePredicate<T> implements Evaluable.LeafPred<T> {
    protected LeafPredicate(Object creator, List<Object> args, Supplier<String> formatter, Predicate<? super T> predicate) {
      super(creator, args, formatter, predicate);
    }

    @Override
    public Predicate<? super T> predicate() {
      return predicate;
    }

    @Override
    public String toString() {
      return formatter.get();
    }
  }

  abstract static class Junction<T> extends PrintablePredicate<T> implements Evaluable.Composite<T> {
    final Evaluable<T> a;
    final Evaluable<T> b;

    protected Junction(
        PrintablePredicate<T> predicate,
        PrintablePredicate<T> other,
        IdentifiablePredicateFactory creator,
        List<Object> args, Supplier<String> formatter, BiFunction<Predicate<T>, Predicate<T>, Predicate<T>> predicateFactory) {
      super(
          creator,
          args,
          formatter,
          predicateFactory.apply(predicate, other));
      a = toEvaluableIfNecessary(predicate);
      b = toEvaluableIfNecessary(other);
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
}
