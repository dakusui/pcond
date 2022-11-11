package com.github.dakusui.pcond.core.fluent2;

import com.github.dakusui.pcond.fluent.Statement;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalChecks.requireState;
import static java.util.Objects.requireNonNull;

public interface Matcher<M extends Matcher<M, OIN, T>, OIN, T> extends Statement<OIN> {
  default M appendPredicateAsChild(Predicate<T> predicate) {
    requireNonNull(predicate);
    return this.appendChild(m -> predicate);
  }

  @SuppressWarnings("unchecked")
  default <N extends Matcher.Base<N, OIN, R>, R> N chain(Function<T, R> func, Function<? super M, N> conv) {
    requireNonNull(func);
    N next = requireNonNull(conv).apply((M) this);
    this.appendChild(m -> Predicates.transform(func).check(conv.apply((M) this).toPredicate()));
    return next;
  }

  Predicate<T> toPredicate();

  M allOf();

  M anyOf();


  M appendChild(Function<M, Predicate<T>> child);

  OIN originalInputValue();

  interface ForString<OIN> extends Matcher<ForString<OIN>, OIN, String> {
  }

  interface ForComparableNumber<OIN, N extends Number & Comparable<N>> extends Matcher<ForComparableNumber<OIN, N>, OIN, N> {
  }

  interface ForInteger<OIN> extends ForComparableNumber<OIN, Integer> {
  }

  interface ForDouble<OIN> extends ForComparableNumber<OIN, Double> {
  }

  interface ForLong<OIN> extends ForComparableNumber<OIN, Long> {
  }

  interface ForFloat<OIN> extends ForComparableNumber<OIN, Float> {
  }

  interface ForShort<OIN> extends ForComparableNumber<OIN, Short> {
  }

  interface ForBoolean<OIN> extends Matcher<ForBoolean<OIN>, OIN, Boolean> {
  }

  interface ForObject<OIN, E> extends Matcher<ForObject<OIN, E>, OIN, E> {
  }

  interface ForList<OIN, E> extends Matcher<ForList<OIN, E>, OIN, List<E>> {
  }

  interface ForStream<OIN, E> extends Matcher<ForStream<OIN, E>, OIN, Stream<E>> {
  }


  /**
   * @param <M>
   * @param <OIN>
   * @param <T>
   */
  class Base<M extends Matcher<M, OIN, T>, OIN, T> implements Matcher<M, OIN, T>, Function<M, Predicate<T>> {
    enum JunctionType {
      CONJUNCTION {
        @SuppressWarnings("unchecked")
        @Override
        public <T> Predicate<T> connect(List<Predicate<T>> predicates) {
          return Predicates.allOf(predicates.toArray(new Predicate[0]));
        }
      },
      DISJUNCTION {
        @SuppressWarnings("unchecked")
        @Override
        public <T> Predicate<T> connect(List<Predicate<T>> predicates) {
          return Predicates.anyOf(predicates.toArray(new Predicate[0]));
        }
      };

      public abstract <T> Predicate<T> connect(List<Predicate<T>> collect);
    }

    private final Function<OIN, T> transform;

    private       JunctionType junctionType;
    private final OIN          originalInputValue;

    private final List<Function<M, Predicate<T>>> childPredicates = new LinkedList<>();

    protected Base(OIN originalInputValue, Function<OIN, T> base) {
      this.transform = requireNonNull(base);
      this.originalInputValue = originalInputValue;
      this.junctionType(JunctionType.CONJUNCTION);
    }

    @Override
    public M appendChild(Function<M, Predicate<T>> child) {
      this.childPredicates.add(requireNonNull(child));
      return me();
    }

    @Override
    public Predicate<T> toPredicate() {
      Predicate<T> ret;
      requireState(this, v -> !v.childPredicates.isEmpty(), (v) -> "No child has been added yet.: <" + v + ">");
      if (this.childPredicates.size() == 1)
        ret = childPredicates.get(0).apply(me());
      else {
        ret = this.junctionType.connect(this.childPredicates.stream().map(each -> each.apply(me())).collect(Collectors.toList()));
      }
      return ret;
    }

    @Override
    public M allOf() {
      return junctionType(JunctionType.CONJUNCTION);
    }

    @Override
    public M anyOf() {
      return junctionType(JunctionType.DISJUNCTION);
    }

    @Override
    public OIN originalInputValue() {
      return this.originalInputValue;
    }

    @Override
    public Predicate<T> apply(M m) {
      return toPredicate();
    }

    @Override
    public boolean test(OIN oin) {
      return Predicates.transform(transform).check(toPredicate()).test(oin);
    }

    protected Function<OIN, T> transform() {
      return this.transform;
    }

    private M junctionType(JunctionType junctionType) {
      requireState(this, v -> childPredicates.isEmpty(), v -> "Child predicate(s) are already added.: <" + this + ">");
      this.junctionType = requireNonNull(junctionType);
      return me();
    }

    private M me() {
      //noinspection unchecked
      return (M) this;
    }
  }
}
