package com.github.dakusui.pcond.core.identifieable;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.context.Context;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.toEvaluableIfNecessary;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

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
  },
  ;

  abstract <T> PrintablePredicate<T> create(List<Object> args);

  @SuppressWarnings("unchecked")
  private static <T> Predicate<T> unwrapIfPrintablePredicate(Predicate<? super T> predicate) {
    Predicate<? super T> ret = predicate;
    if (predicate instanceof PrintablePredicate)
      ret = unwrapIfPrintablePredicate(((PrintablePredicate<? super T>) predicate).predicate);
    return (Predicate<T>) ret;
  }

  private static class LeafPredicate<T> extends PrintablePredicate<T> implements Evaluable.LeafPred<T> {
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

  static class TransformingPredicate<P, O> extends PrintablePredicate<O> implements Evaluable.Transformation<O, P> {
    private final Evaluable<? super P> checker;
    private final Evaluable<? super O> mapper;

    private TransformingPredicate(String name, Predicate<? super P> predicate, Function<? super O, ? extends P> function) {
      super(
          TransformingPredicate.class,
          asList(predicate, function),
          () -> format("%s%s %s", name == null ? "" : name, function, predicate),
          v -> predicate.test(function.apply(v)));
      this.checker = toEvaluableIfNecessary(predicate);
      this.mapper = toEvaluableIfNecessary(function);
    }

    @Override
    public Evaluable<? super O> mapper() {
      return this.mapper;
    }

    @Override
    public Evaluable<? super P> checker() {
      return this.checker;
    }
  }

  static class ContextPredicate extends PrintablePredicate<Context> implements Evaluable.ContextPred {
    public static <T> ContextPredicate create(PrintablePredicate<T> predicate, int argIndex) {
      return new ContextPredicate(ContextPredicate.class, asList(predicate, argIndex), predicate, argIndex);
    }

    private final Evaluable<?> enclosed;
    private final int          argIndex;

    private <T> ContextPredicate(Object creator, List<Object> args, Predicate<T> predicate, int argIndex) {
      super(
          creator,
          args,
          () -> format("contextPredicate[%s,%s]", predicate, argIndex),
          context -> predicate.test(context.valueAt(argIndex)));
      this.enclosed = toEvaluableIfNecessary(predicate);
      this.argIndex = argIndex;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <TT> Evaluable<? super TT> enclosed() {
      return (Evaluable<? super TT>) this.enclosed;
    }

    @Override
    public int argIndex() {
      return argIndex;
    }
  }

  static class AllMatch<E> extends StreamPredicate<E> {
    public static <E> StreamPredicate<E> create(Predicate<? super E> predicate) {
      return new AllMatch<>(
          predicate
      );
    }

    private AllMatch(Predicate<? super E> predicate) {
      super(
          AllMatch.class,
          singletonList(predicate),
          () -> format("allMatch[%s]", predicate),
          (Stream<E> stream) -> stream.allMatch(predicate),
          toEvaluableIfNecessary(predicate),
          true,
          false);
    }
  }

  static class NoneMatch<E> extends StreamPredicate<E> {
    public static <E> StreamPredicate<E> create(Predicate<? super E> predicate) {
      return new NoneMatch<>(
          predicate
      );
    }

    private NoneMatch(Predicate<? super E> predicate) {
      super(
          NoneMatch.class,
          singletonList(predicate),
          () -> format("noneMatch[%s]", predicate),
          (Stream<E> stream) -> stream.allMatch(predicate),
          toEvaluableIfNecessary(predicate),
          true,
          true);
    }
  }

  static class AnyMatch<E> extends StreamPredicate<E> {
    public static <E> StreamPredicate<E> create(Predicate<? super E> predicate) {
      return new AnyMatch<>(
          predicate
      );
    }

    private AnyMatch(Predicate<? super E> predicate) {
      super(
          AnyMatch.class,
          singletonList(predicate),
          () -> format("anyMatch[%s]", predicate),
          (Stream<E> stream) -> stream.allMatch(predicate),
          toEvaluableIfNecessary(predicate),
          false,
          true);
    }
  }

  abstract static class StreamPredicate<E> extends PrintablePredicate<Stream<E>> implements Evaluable.StreamPred<E> {
    private final Evaluable<? super E> cut;
    private final boolean              defaultValue;
    private final boolean              cutOn;

    private StreamPredicate(Object creator, List<Object> args, Supplier<String> formatter, Predicate<? super Stream<E>> predicate, Evaluable<? super E> cut, boolean defaultValue, boolean cutOn) {
      super(creator, args, formatter, predicate);
      this.cut = requireNonNull(cut);
      this.defaultValue = defaultValue;
      this.cutOn = cutOn;
    }

    @Override
    public boolean defaultValue() {
      return defaultValue;
    }

    @Override
    public Evaluable<? super E> cut() {
      return cut;
    }

    @Override
    public boolean valueToCut() {
      return cutOn;
    }
  }
}
