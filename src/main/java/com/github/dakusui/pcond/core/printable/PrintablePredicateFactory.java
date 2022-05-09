package com.github.dakusui.pcond.core.printable;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.context.Context;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.identifieable.Identifiable.argsOf;
import static com.github.dakusui.pcond.core.identifieable.Identifiable.creatorOf;
import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static com.github.dakusui.pcond.internals.InternalUtils.toEvaluableIfNecessary;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public enum PrintablePredicateFactory {
  NEGATION,
  CONJUNCTION,
  DISJUNCTION,
  MESSAGE,
  LEAF,
  ;

  private static <T> PrintablePredicate<T> toPrintablePredicate(Predicate<T> predicate) {
    return (PrintablePredicate<T>) leaf(predicate);
  }

  public static <T> Predicate<T> leaf(Predicate<T> predicate) {
    if (!(predicate instanceof PrintablePredicate))
      return leaf(Identifiable.formatObjectName(predicate), predicate);
    return predicate;
  }

  public static <T> Predicate<T> leaf(String name, Predicate<T> predicate) {
    return leaf(() -> name, predicate);
  }

  public static <T> Predicate<T> leaf(Supplier<String> formatter, Predicate<T> predicate) {
    return leaf(formatter, predicate, PrintablePredicateFactory.class);
  }

  public static <T> Predicate<T> leaf(Supplier<String> formatter, Predicate<T> predicate, Object fallbackCreator) {
    return parameterizedLeaf(
        (args) -> formatter,
        (args) -> predicate,
        emptyList(),
        fallbackCreator);
  }

  public static <T> Predicate<T> parameterizedLeaf(
      Function<List<Object>, Supplier<String>> formatterFactory,
      Function<List<Object>, Predicate<T>> predicateFactory,
      List<Object> args,
      Object fallbackCreator
  ) {
    Supplier<String> formatter = formatterFactory.apply(args);
    Predicate<T> predicate = predicateFactory.apply(args);
    return creatorOf(predicate)
        .map(c -> new LeafPredicate<>(c, argsOf(predicate), formatter, predicate))
        .orElse(new LeafPredicate<>(fallbackCreator, args, formatter, predicate));
  }

  public static <P, O> TransformingPredicate.Factory<P, O> transform(Function<O, P> function) {
    return TransformingPredicate.Factory.create(function);
  }

  public static <T> Conjunction<T> and(List<Predicate<? super T>> predicates) {
    return new Conjunction<T>(predicates, true);
  }

  public static <T> Disjunction<T> or(List<Predicate<? super T>> predicates) {
    return new Disjunction<T>(predicates, true);
  }

  public static <T> Conjunction<T> allOf(List<Predicate<? super T>> predicates) {
    return new Conjunction<T>(predicates, false);
  }

  public static <T> Disjunction<T> anyOf(List<Predicate<? super T>> predicates) {
    return new Disjunction<T>(predicates, false);
  }

  public static <T> Predicate<T> not(Predicate<T> predicate) {
    return not_(predicate);
  }

  public static <T> Negation<T> not_(Predicate<T> predicate) {
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

  private static RuntimeException noPredicateGiven() {
    throw new IllegalArgumentException("No predicate was given");
  }

  public static <T> Predicate<T> withMessage(Supplier<String> messageSupplier, Predicate<T> predicate) {
    return new Messaged<T>(messageSupplier, toPrintablePredicate(predicate), singletonList(predicate));
  }

  /*
      Expected: "100 -> &&                                     ->   false"
       but: was "100 -> &&                                            ->   false"
   */
  public enum Leaf {
    ALWAYS_TRUE("alwaysTrue", v -> true),
    IS_TRUE("isTrue", (Boolean v) -> v),
    IS_FALSE("isFalse", (Boolean v) -> !v),
    IS_NULL("isNull", Objects::isNull),
    IS_NOT_NULL("isNotNull", Objects::nonNull),
    IS_EMPTY_STRING("isEmpty", v -> ((String) v).isEmpty()),
    IS_NULL_OR_EMPTY_STRING("isNullOrEmptyString", v -> v == null || ((String) v).isEmpty()),
    IS_EMPTY_ARRAY("isEmptyArray", objects -> ((Object[]) objects).length == 0),
    IS_EMPTY_COLLECTION("isEmpty", v -> ((Collection<?>) v).isEmpty()),
    ;
    final Predicate<?> instance;

    @SuppressWarnings("unchecked")
    Leaf(String s, Predicate<?> predicate) {
      this.instance = leaf(() -> s, (Predicate<? super Object>) predicate, this);
    }

    @SuppressWarnings("unchecked")
    public <T> Predicate<T> instance() {
      return (Predicate<T>) this.instance;
    }
  }

  public enum ParameterizedLeafFactory {
    IS_EQUAL_TO(
        (args) -> () -> format("isEqualTo[%s]", formatObject(args.get(0))),
        (args) -> v -> Objects.equals(v, args.get(0))),
    @SuppressWarnings("unchecked") GREATER_THAN(
        (args) -> () -> format(">[%s]", formatObject(args.get(0))),
        (args) -> v -> ((Comparable<? super Comparable<?>>) v).compareTo((Comparable<? super Comparable<?>>) args.get(0)) > 0),
    @SuppressWarnings("unchecked") GREATER_THAN_OR_EQUAL_TO(
        (args) -> () -> format(">=[%s]", formatObject(args.get(0))),
        (args) -> v -> ((Comparable<? super Comparable<?>>) v).compareTo((Comparable<? super Comparable<?>>) args.get(0)) >= 0),
    @SuppressWarnings("unchecked") LESS_THAN_OR_EQUAL_TO(
        (args) -> () -> format("<=[%s]", formatObject(args.get(0))),
        (args) -> v -> ((Comparable<? super Comparable<?>>) v).compareTo((Comparable<? super Comparable<?>>) args.get(0)) <= 0),
    @SuppressWarnings("unchecked") LESS_THAN(
        (args) -> () -> format("<[%s]", formatObject(args.get(0))),
        (args) -> v -> ((Comparable<? super Comparable<?>>) v).compareTo((Comparable<? super Comparable<?>>) args.get(0)) < 0),
    @SuppressWarnings("unchecked") EQUAL_TO(
        (args) -> () -> format("=[%s]", formatObject(args.get(0))),
        (args) -> v -> ((Comparable<? super Comparable<?>>) v).compareTo((Comparable<? super Comparable<?>>) args.get(0)) == 0),
    MATCHES_REGEX(
        (args) -> () -> String.format("matchesRegex[%s]", formatObject(args.get(0))),
        (args) -> (s) -> ((String) s).matches((String) args.get(0))),
    CONTAINS_STRING(
        (args) -> () -> format("containsString[%s]", formatObject(args.get(0))),
        (args) -> (s) -> ((String) s).contains((String) args.get(0))),
    STARTS_WITH(
        (args) -> () -> format("startsWith[%s]", formatObject(args.get(0))),
        (args) -> (s) -> ((String) s).startsWith((String) args.get(0))),
    ENDS_WITH(
        (args) -> () -> format("endsWith[%s]", formatObject(args.get(0))),
        (args) -> (s) -> ((String) s).endsWith((String) args.get(0))),
    EQUALS_IGNORE_CASE(
        (args) -> () -> format("equalsIgnoreCase[%s]", formatObject(args.get(0))),
        (args) -> (s) -> ((String) s).equalsIgnoreCase((String) args.get(0))),
    OBJECT_IS_SAME_AS(
        arg -> () -> format("==[%s]", formatObject(arg.get(0))),
        args -> v -> v == args.get(0)),
    CONTAINS(
        (args) -> () -> format("contains[%s]", formatObject(args.get(0))),
        (args) -> (c) -> ((Collection<?>) c).contains(args.get(0)));
    private final Function<List<Object>, Predicate<Object>> predicateFactory;
    private final Function<List<Object>, Supplier<String>>  formatterFactory;

    ParameterizedLeafFactory(Function<List<Object>, Supplier<String>> formatterFactory, Function<List<Object>, Predicate<Object>> predicateFactory) {
      this.predicateFactory = predicateFactory;
      this.formatterFactory = formatterFactory;
    }

    Function<List<Object>, Supplier<String>> formatterFactory() {
      return this.formatterFactory;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    <T> Function<List<Object>, Predicate<T>> functionFactory() {
      return (Function) this.predicateFactory;
    }

    public static <T> Predicate<T> create(ParameterizedLeafFactory parameterizedLeafFactory, List<Object> args) {
      return parameterizedLeaf(
          parameterizedLeafFactory.formatterFactory(), parameterizedLeafFactory.functionFactory(), args, parameterizedLeafFactory
      );
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
    protected Negation(Predicate<T> predicate, List<Object> args) {
      super(
          NEGATION,
          args,
          () -> format("!%s", predicate),
          (t) -> PrintablePredicate.unwrap((Predicate<Object>) predicate).negate().test(t));
      target = toEvaluableIfNecessary(predicate);
    }

    @Override
    public Evaluable<? super T> target() {
      return target;
    }
  }

  static class Conjunction<T> extends Junction<T> implements Evaluable.Conjunction<T> {
    protected Conjunction(List<Predicate<? super T>> predicates, boolean shortcut) {
      super(
          predicates,
          CONJUNCTION,
          "&&",
          Predicate::and,
          shortcut);
    }
  }

  static class Messaged<T> extends PrintablePredicate<T> implements Evaluable.Messaged<T> {
    //private final String               message;
    private final Evaluable<? super T> target;

    @SuppressWarnings("unchecked")
    protected Messaged(Supplier<String> messageSupplier, Predicate<T> predicate, List<Object> args) {
      super(
          MESSAGE,
          args,
          requireNonNull(messageSupplier),
          (t) -> PrintablePredicate.unwrap((Predicate<Object>) predicate).test(t));
      //this.message = requireNonNull(message);
      this.target = toEvaluableIfNecessary(predicate);
    }

    @Override
    public Evaluable<? super T> target() {
      return this.target;
    }

    @Override
    public String message() {
      return this.formatter.get();
    }
  }

  private static class Disjunction<T> extends Junction<T> implements Evaluable.Disjunction<T> {
    protected Disjunction(List<Predicate<? super T>> predicates, boolean shortcut) {
      super(
          predicates,
          DISJUNCTION,
          "||",
          Predicate::or,
          shortcut);
    }
  }

  abstract static class Junction<T> extends PrintablePredicate<T> implements Evaluable.Composite<T> {
    final         List<Evaluable<? super T>> children;
    final private boolean                    shortcut;

    protected Junction(
        List<Predicate<? super T>> predicates,
        PrintablePredicateFactory creator,
        String junctionSymbol,
        BinaryOperator<Predicate<T>> junctionOp, boolean shortcut) {
      super(
          creator,
          new ArrayList<>(predicates),
          () -> formatJunction(predicates, junctionSymbol),
          junction(predicates, junctionOp));
      this.children = predicates.stream().map(InternalUtils::toEvaluableIfNecessary).collect(toList());
      this.shortcut = shortcut;
    }

    @Override
    public List<Evaluable<? super T>> children() {
      return this.children;
    }

    @Override
    public boolean shortcut() {
      return this.shortcut;
    }

    static <T> String formatJunction(List<Predicate<? super T>> predicates, String junctionSymbol) {
      return predicates.stream()
          .map(PrintablePredicateFactory::toPrintablePredicate)
          .map(Object::toString)
          .collect(joining(junctionSymbol, "(", ")"));
    }

    @SuppressWarnings("unchecked")
    static <T> Predicate<T> junction(List<Predicate<? super T>> predicates_, BinaryOperator<Predicate<T>> junctionOp) {
      return predicates_
          .stream()
          .map(PrintablePredicate::unwrap)
          .map(p -> (Predicate<T>) p)
          .reduce(junctionOp)
          .orElseThrow(PrintablePredicateFactory::noPredicateGiven);
    }
  }

  /**
   * This is an interface that corresponds to a "matcher" in other assertion
   * libraries.
   */
  public static class TransformingPredicate<P, O> extends PrintablePredicate<O> implements Evaluable.Transformation<O, P> {
    private final Evaluable<? super P> checker;
    private final Evaluable<? super O> mapper;

    public TransformingPredicate(String name, Predicate<? super P> predicate, Function<? super O, ? extends P> function) {
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

    /**
     * This is an interface that corresponds to a "matcher" in other assertion
     * libraries.
     *
     * @param <P> Intermediate parameter type tested by a predicated.
     * @param <O> Input parameter type.
     */
    public interface Factory<P, O> {
      default TransformingPredicate<P, O> check(String condName, Predicate<? super P> cond) {
        return check(leaf(condName, cond));
      }

      @SuppressWarnings("unchecked")
      default <OO> Factory<P, OO> castTo(Class<OO> ooClass) {
        return (Factory<P, OO>) this;
      }

      TransformingPredicate<P, O> check(Predicate<? super P> cond);

      static <P, O> Factory<P, O> create(Function<O, P> function) {
        return cond -> new TransformingPredicate<>(null, toPrintablePredicate(cond), function);
      }
    }

    public static class Builder<IN, IM> {
      private String           name;
      private Function<IN, IM> function;
      private Predicate<IM>    predicate;

      public Builder() {
      }

      public Builder(String name) {
        this.name(name);
      }

      public Builder<IN, IM> name(String name) {
        this.name = name;
        return this;
      }


      @SuppressWarnings("unchecked")
      public <NIN> Builder<NIN, IM> forValueOf(Class<NIN> forClass) {
        return (Builder<NIN, IM>) this;
      }

      @SuppressWarnings("unchecked")
      public Builder<String, IM> forString() {
        return (Builder<String, IM>) this;
      }

      /**
       * @param elementClass A placeholder parameter, not accessed in this method.
       *                     Do not remove.
       * @param <E>          A type of element in a list.
       * @return This object cast accordingly.
       */
      @SuppressWarnings("unchecked")
      public <E> Builder<List<E>, IM> forListOf(Class<E> elementClass) {
        return (Builder<List<E>, IM>) this;
      }

      @SuppressWarnings("unchecked")
      public <NIM> Builder<IN, NIM> transformBy(Function<IN, NIM> func) {
        this.function = (Function<IN, IM>) func;
        //return Factory.create(Printables.function(this.name, func));
        return (Builder<IN, NIM>) this;
      }

      /**
       * @param intoClass A placeholder parameter, not accessed in this method.
       *                  Do not remove.
       * @param <NIM>     An intermediate type evaluated by the predicate part.
       * @return This object cast.
       */
      @SuppressWarnings("unchecked")
      public <NIM> Builder<IN, NIM> into(@SuppressWarnings("unused") Class<NIM> intoClass) {
        return (Builder<IN, NIM>) this;
      }

      public Predicate<IN> thenVerifyWith(Predicate<IM> predicate) {
        this.predicate = predicate;
        return build();
      }

      public Predicate<IN> build() {
        return TransformingPredicate.Factory.create(makePrintableIfFormatterIsAvailable(this.function)).check(this.predicate);
      }

      private <I, O> Function<I, O> makePrintableIfFormatterIsAvailable(Function<I, O> func) {
        return this.name != null ?
            Printables.function(name, func) :
            func;
      }
    }
  }

  static class ContextPredicate extends PrintablePredicate<Context> implements Evaluable.ContextPred {
    private final Evaluable<?> enclosed;
    private final int          argIndex;

    private <T> ContextPredicate(Object creator, List<Object> args, Predicate<T> predicate, int argIndex) {
      super(
          creator,
          args,
          () -> format("contextPredicate[%s,%s]", predicate, argIndex),
          context -> PrintablePredicate.unwrap(predicate).test(context.valueAt(argIndex)));
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

    public static <T> ContextPredicate create(Predicate<T> predicate, int argIndex) {
      return new ContextPredicate(ContextPredicate.class, asList(predicate, argIndex), predicate, argIndex);
    }
  }

  static class AllMatch<E> extends StreamPredicate<E> {
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

    static <E> StreamPredicate<E> create(Predicate<? super E> predicate) {
      return new AllMatch<>(
          predicate
      );
    }
  }

  static class NoneMatch<E> extends StreamPredicate<E> {
    @SuppressWarnings("unchecked")
    private NoneMatch(Predicate<? super E> predicate) {
      super(
          NoneMatch.class,
          singletonList(predicate),
          () -> format("noneMatch[%s]", predicate),
          (Stream<? extends E> stream) -> stream.noneMatch(predicate),
          toEvaluableIfNecessary((Predicate<? super Stream<? extends E>>) predicate),
          true,
          true);
    }

    public static <E> StreamPredicate<E> create(Predicate<? super E> predicate) {
      return new NoneMatch<>(
          predicate
      );
    }
  }

  static class AnyMatch<E> extends StreamPredicate<E> {
    @SuppressWarnings("unchecked")
    private AnyMatch(Predicate<? super E> predicate) {
      super(
          AnyMatch.class,
          singletonList(predicate),
          () -> format("anyMatch[%s]", predicate),
          (Stream<? extends E> stream) -> stream.anyMatch(PrintablePredicate.<E>unwrap(predicate)),
          toEvaluableIfNecessary((Predicate<? super Stream<? extends E>>) predicate),
          false,
          true);
    }

    public static <E> StreamPredicate<E> create(Predicate<? super E> predicate) {
      return new AnyMatch<>(
          predicate
      );
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
