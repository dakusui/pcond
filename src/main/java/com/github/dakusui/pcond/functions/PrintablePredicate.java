package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.internals.PrintableLambdaFactory;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.toEvaluableIfNecessary;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public abstract class PrintablePredicate<T> implements Predicate<T>, Evaluable<T> {
  private static final PrintablePredicate.Factory<?, List<Predicate<Object>>> AND_FACTORY = factory(
      (arg) -> format("(%s&&%s)", arg.get(0), arg.get(1)),
      arg -> (Object t) -> unwrapIfPrintablePredicate(arg.get(0)).test(t) && (unwrapIfPrintablePredicate(arg.get(1))).test(t)
  );

  private static final PrintablePredicate.Factory<?, List<Predicate<Object>>> OR_FACTORY = factory(
      (arg) -> format("(%s||%s)", arg.get(0), arg.get(1)),
      arg -> (Object t) -> unwrapIfPrintablePredicate(arg.get(0)).test(t) || (unwrapIfPrintablePredicate(arg.get(1))).test(t)
  );

  private static final PrintablePredicate.Factory<?, Predicate<Object>> NEGATE_FACTORY = factory(
      (arg) -> format("!%s", arg),
      arg -> (Object t) -> unwrapIfPrintablePredicate(arg).negate().test(t)
  );

  final Predicate<? super T> predicate;
  final Supplier<String>     s;

  public static <T, E> Factory<T, E> factory(Function<E, String> nameComposer, Function<E, Predicate<T>> ff) {
    return new Factory<T, E>(nameComposer) {
      @Override
      Predicate<? super T> createPredicate(E arg) {
        return ff.apply(arg);
      }
    };
  }

  protected PrintablePredicate(Supplier<String> s, Predicate<? super T> predicate) {
    this.predicate = requireNonNull(predicate);
    this.s = requireNonNull(s);
  }

  @Override
  public boolean test(T t) {
    return predicate.test(t);
  }

  @SuppressWarnings({ "unchecked" })
  @Override
  public Predicate<T> and(Predicate<? super T> other) {
    requireNonNull(other);
    Predicate<Object> p = (Predicate<Object>) this;
    Predicate<Object> q = (Predicate<Object>) other;
    return (Predicate<T>) AND_FACTORY.createConjunction(asList((Predicate<Object>) this, (Predicate<Object>) other), p, q);
  }

  @SuppressWarnings({ "unchecked" })
  @Override
  public Predicate<T> or(Predicate<? super T> other) {
    requireNonNull(other);
    Predicate<Object> p = (Predicate<Object>) this;
    Predicate<Object> q = (Predicate<Object>) other;
    return (Predicate<T>) OR_FACTORY.createDisjunction(asList((Predicate<Object>) this, (Predicate<Object>) other), p, q);
  }

  @SuppressWarnings({ "unchecked" })
  @Override
  public Predicate<T> negate() {
    Predicate<Object> p = (Predicate<Object>) this;
    return (Predicate<T>) NEGATE_FACTORY.createNegation((Predicate<Object>) this, p);
  }

  @Override
  public int hashCode() {
    return this.predicate.hashCode();
  }

  @Override
  public boolean equals(Object anotherObject) {
    if (this == anotherObject)
      return true;
    if (!(anotherObject instanceof PrintablePredicate))
      return false;
    @SuppressWarnings("unchecked") PrintablePredicate<T> another = (PrintablePredicate<T>) anotherObject;
    return this.predicate.equals(another.predicate);
  }

  @Override
  public String toString() {
    return s.get();
  }

  private static Predicate<Object> unwrapIfPrintablePredicate(Predicate<Object> predicate) {
    Predicate<Object> ret = predicate;
    if (predicate instanceof PrintablePredicate)
      ret = ((PrintablePredicate<Object>) predicate).predicate;
    return ret;
  }

  public static class LeafPred<T> extends PrintablePredicate<T> implements Evaluable.LeafPred<T> {

    public LeafPred(Supplier<String> s, Predicate<? super T> predicate) {
      super(s, predicate);
    }

    @Override
    public Predicate<? super T> predicate() {
      return predicate;
    }
  }

  private abstract static class Junction<T> extends PrintablePredicate<T> implements Evaluable.Composite<T> {
    private final Evaluable<? super T> a;
    private final Evaluable<? super T> b;

    public Junction(Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> a, Evaluable<? super T> b) {
      super(s, predicate);
      this.a = a;
      this.b = b;
    }

    @Override
    public Evaluable<? super T> a() {
      return this.a;
    }

    @Override
    public Evaluable<? super T> b() {
      return this.b;
    }
  }

  public static class Conjunction<T> extends Junction<T> implements Evaluable.Conjunction<T> {
    public Conjunction(Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> a, Evaluable<? super T> b) {
      super(s, predicate, a, b);
    }
  }

  public static class Disjunction<T> extends Junction<T> implements Evaluable.Disjunction<T> {
    public Disjunction(Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> a, Evaluable<? super T> b) {
      super(s, predicate, a, b);
    }
  }

  public static class Negation<T> extends PrintablePredicate<T> implements Evaluable.Negation<T> {
    private final Evaluable<? super T> body;

    public Negation(Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> body) {
      super(s, predicate);
      this.body = body;
    }

    @Override
    public Evaluable<? super T> target() {
      return this.body;
    }
  }

  public static abstract class Factory<T, E> extends PrintableLambdaFactory<E> {
    Factory(Function<E, String> s) {
      super(s);
    }

    abstract Predicate<? super T> createPredicate(E arg);

    public PrintablePredicate<T> create(E arg) {
      return createLeaf(arg);
    }

    public LeafPredPrintablePredicateFromFactory<T, E> createLeaf(E arg) {
      Lambda.Spec<E> spec = new Lambda.Spec<>(Factory.this, arg, LeafPredPrintablePredicateFromFactory.class);
      return new LeafPredPrintablePredicateFromFactory<>(spec, () -> this.nameComposer().apply(arg), createPredicate(arg));
    }

    public <P extends Predicate<? super T>> ConjunctionPrintablePredicateFromFactory<T, E> createConjunction(E arg, P p, P q) {
      Lambda.Spec<E> spec = new Lambda.Spec<>(Factory.this, arg, ConjunctionPrintablePredicateFromFactory.class);
      return new ConjunctionPrintablePredicateFromFactory<>(
          spec,
          () -> this.nameComposer().apply(arg),
          createPredicate(arg),
          toEvaluableIfNecessary(p),
          toEvaluableIfNecessary(q));
    }

    public <P extends Predicate<? super T>> DisjunctionPrintablePredicateFromFactory<T, E> createDisjunction(E arg, P p, P q) {
      Lambda.Spec<E> spec = new Lambda.Spec<>(Factory.this, arg, DisjunctionPrintablePredicateFromFactory.class);
      return new DisjunctionPrintablePredicateFromFactory<>(
          spec,
          () -> this.nameComposer().apply(arg),
          createPredicate(arg),
          toEvaluableIfNecessary(p),
          toEvaluableIfNecessary(q));
    }

    public <P extends Predicate<? super T>> NegationPrintablePredicateFromFactory<T, E> createNegation(E arg, P p) {
      Lambda.Spec<E> spec = new Lambda.Spec<>(Factory.this, arg, NegationPrintablePredicateFromFactory.class);
      return new NegationPrintablePredicateFromFactory<>(
          spec,
          () -> this.nameComposer().apply(arg),
          createPredicate(arg),
          toEvaluableIfNecessary(p));
    }

    static class LeafPredPrintablePredicateFromFactory<T, E> extends LeafPred<T> implements Lambda<Factory<T, E>, E> {
      private final Spec<E> spec;

      LeafPredPrintablePredicateFromFactory(Spec<E> spec, Supplier<String> s, Predicate<? super T> predicate) {
        super(s, predicate);
        this.spec = spec;
      }

      @Override
      public Spec<E> spec() {
        return spec;
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

    static class ConjunctionPrintablePredicateFromFactory<T, E> extends Conjunction<T> implements Lambda<Factory<T, E>, E> {
      private final Spec<E> spec;

      ConjunctionPrintablePredicateFromFactory(Spec<E> spec, Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> a, Evaluable<? super T> b) {
        super(s, predicate, a, b);
        this.spec = spec;
      }

      @Override
      public Spec<E> spec() {
        return spec;
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

    static class DisjunctionPrintablePredicateFromFactory<T, E> extends Disjunction<T> implements Lambda<Factory<T, E>, E> {
      private final Spec<E> spec;

      DisjunctionPrintablePredicateFromFactory(Spec<E> spec, Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> a, Evaluable<? super T> b) {
        super(s, predicate, a, b);
        this.spec = spec;
      }

      @Override
      public Spec<E> spec() {
        return spec;
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

    static class NegationPrintablePredicateFromFactory<T, E> extends Negation<T> implements Lambda<Factory<T, E>, E> {
      private final Spec<E> spec;

      NegationPrintablePredicateFromFactory(Spec<E> spec, Supplier<String> s, Predicate<? super T> predicate, Evaluable<? super T> target) {
        super(s, predicate, target);
        this.spec = spec;
      }

      @Override
      public Spec<E> spec() {
        return spec;
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
  }
}
