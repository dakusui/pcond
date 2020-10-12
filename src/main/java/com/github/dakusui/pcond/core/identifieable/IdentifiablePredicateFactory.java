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
  FOR_NEGATION,
  FOR_CONJUNCTION,
  FOR_DISJUNCTION,
  FOR_LEAF,
  FOR_PARAMETERIZED_LEAF,
  FOR_MATCHES_REGEX;

  public static <T> PrintablePredicate<T> leaf(Supplier<String> formatter, Predicate<? super T> predicate) {
    return new LeafPredicate<>(FOR_LEAF, singletonList(predicate), formatter, predicate);
  }

  public static <T> PrintablePredicate<T> parameterizedLeaf(
      Object factoryIdentifier,
      Function<List<Object>, Supplier<String>> formatterFactory,
      Function<List<Object>, Predicate<T>> predicateFactory,
      List<Object> args
  ) {
    return new LeafPredicate<>(factoryIdentifier, args, formatterFactory.apply(args), predicateFactory.apply(args));
  }

  public static PrintablePredicate<String> matchesRegex(String regex) {
    return parameterizedLeaf(
        FOR_MATCHES_REGEX,
        (args) -> () -> format("matchesRegex[%s]", regex),
        (args) -> (String s) -> s.matches(regex),
        singletonList(regex));
  }

  public static <P, O> TransformingPredicate.Factory<P, O> transform(Function<O, P> function) {
    return TransformingPredicate.Factory.create(function);
  }

  public static <T> PrintablePredicate<T> and(Predicate<? super T> predicate, Predicate<? super T> other) {
    return new Conjunction<T>(toPrintablePredicate(predicate), toPrintablePredicate(other), asList(predicate, other));
  }

  public static <T> PrintablePredicate<T> or(Predicate<? super T> predicate, Predicate<? super T> other) {
    return new Disjunction<T>(toPrintablePredicate(predicate), toPrintablePredicate(other), asList(predicate, other));
  }

  public static <T> PrintablePredicate<T> not(Predicate<T> predicate) {
    return new Negation<T>(toPrintablePredicate(predicate), singletonList(predicate));
  }

  public static <E> Predicate<Stream<? extends E>> allMatch(Predicate<E> predicate) {
    return AllMatch.create(predicate);
  }

  public static <E> Predicate<Stream<? extends E>> noneMatch(Predicate<E> predicate) {
    return NoneMatch.create(predicate);
  }

  public static <E> Predicate<Stream<? extends E>> anyMatch(Predicate<E> predicate) {
    return AnyMatch.create(predicate);
  }

  public static <T> Predicate<Context> contextPredicate(Predicate<T> predicate, int argIndex) {
    return ContextPredicate.create(toPrintablePredicate(predicate), argIndex);
  }

  @SuppressWarnings("unchecked")
  private static <T> PrintablePredicate<T> toPrintablePredicate(Predicate<? super T> predicate) {
    if (!(predicate instanceof PrintablePredicate))
      return leaf(() -> "noname:" + predicate.toString(), predicate);
    return (PrintablePredicate<T>) predicate;
  }

  @SuppressWarnings("unchecked")
  private static <T> Predicate<T> unwrapIfPrintablePredicate(Predicate<? super T> predicate) {
    Predicate<? super T> ret = predicate;
    if (predicate instanceof PrintablePredicate)
      ret = unwrapIfPrintablePredicate(((PrintablePredicate<? super T>) predicate).predicate);
    return (Predicate<T>) ret;
  }

  private static class Disjunction<T> extends Junction<T> implements Evaluable.Disjunction<T> {
    protected Disjunction(PrintablePredicate<T> predicate, PrintablePredicate<T> other, List<Object> args) {
      super(predicate, other, FOR_DISJUNCTION, args, () -> format("(%s&&%s)", predicate, other), (p, o) -> unwrapIfPrintablePredicate(p).and(unwrapIfPrintablePredicate(o)));
    }
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

  static class Negation<T> extends PrintablePredicate<T> implements Evaluable.Negation<T> {
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

  static class Conjunction<T> extends Junction<T> implements Evaluable.Conjunction<T> {
    protected Conjunction(PrintablePredicate<T> predicate, PrintablePredicate<T> other, List<Object> args) {
      super(predicate, other, FOR_CONJUNCTION, args, () -> format("(%s||%s)", predicate, other), (p, o) -> unwrapIfPrintablePredicate(p).or(unwrapIfPrintablePredicate(o)));
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
    interface Factory<P, O> {
      static <P, O> Factory<P, O> create(Function<O, P> function) {
        return cond -> new TransformingPredicate<>(null, toPrintablePredicate(cond), function);
      }

      default TransformingPredicate<P, O> check(String condName, Predicate<? super P> cond) {
        return check(leaf(() -> condName, cond));
      }

      TransformingPredicate<P, O> check(Predicate<? super P> cond);
    }

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
    static <E> StreamPredicate<E> create(Predicate<? super E> predicate) {
      return new AllMatch<>(
          predicate
      );
    }

    @SuppressWarnings("unchecked")
    private AllMatch(Predicate<? super E> predicate) {
      super(
          AllMatch.class,
          singletonList(predicate),
          () -> format("allMatch[%s]", predicate),
          (Stream<? extends E> stream) -> stream.allMatch(predicate),
          toEvaluableIfNecessary((Predicate<? super Stream<? extends E>>) predicate),
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

    @SuppressWarnings("unchecked")
    private NoneMatch(Predicate<? super E> predicate) {
      super(
          NoneMatch.class,
          singletonList(predicate),
          () -> format("noneMatch[%s]", predicate),
          (Stream<? extends E> stream) -> stream.allMatch(predicate),
          toEvaluableIfNecessary((Predicate<? super Stream<? extends E>>) predicate),
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

    @SuppressWarnings("unchecked")
    private AnyMatch(Predicate<? super E> predicate) {
      super(
          AnyMatch.class,
          singletonList(predicate),
          () -> format("anyMatch[%s]", predicate),
          (Stream<? extends E> stream) -> stream.allMatch(predicate),
          toEvaluableIfNecessary((Predicate<? super Stream<? extends E>>) predicate),
          false,
          true);
    }
  }

  public abstract static class StreamPredicate<E> extends PrintablePredicate<Stream<? extends E>> implements Evaluable.StreamPred<E> {
    private final Evaluable<? super Stream<? extends E>> cut;
    private final boolean                                defaultValue;
    private final boolean                                cutOn;

    private StreamPredicate(Object creator, List<Object> args, Supplier<String> formatter, Predicate<? super Stream<? extends E>> predicate, Evaluable<? super Stream<? extends E>> cut, boolean defaultValue, boolean cutOn) {
      super(creator, args, formatter, predicate);
      this.cut = requireNonNull(cut);
      this.defaultValue = defaultValue;
      this.cutOn = cutOn;
    }

    @Override
    public boolean defaultValue() {
      return defaultValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Evaluable<? super E> cut() {
      return (Evaluable<? super E>) this.cut;
    }

    @Override
    public boolean valueToCut() {
      return cutOn;
    }
  }
}
