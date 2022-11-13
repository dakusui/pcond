package com.github.dakusui.pcond.core.fluent2;

import com.github.dakusui.pcond.fluent.Statement;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.dakusui.pcond.internals.InternalChecks.requireState;
import static java.util.Objects.requireNonNull;

public interface Matcher<M extends Matcher<M, OIN, T>, OIN, T> extends Statement<OIN>, Predicate<OIN> {
  default M appendPredicateAsChild(Predicate<T> predicate) {
    requireNonNull(predicate);
    return this.appendChild(m -> predicate);
  }

  @SuppressWarnings("unchecked")
  default <N extends Matcher.Base<N, OIN, R>, R> N chain(Function<T, R> func, Function<? super M, N> conv) {
    requireNonNull(func);
    N next = requireNonNull(conv).apply((M) this);
    this.appendChild(m -> Predicates.transform(func).check(conv.apply((M) this).connectChildPredicates()));
    return next;
  }

  Predicate<T> connectChildPredicates();

  M allOf();

  M anyOf();


  M appendChild(Function<M, Predicate<? super T>> child);

  OIN originalInputValue();

  @Override
  default OIN statementValue() {
    return originalInputValue();
  }

  Predicate<OIN> statementPredicate();


  /**
   * @param <M>
   * @param <OIN>
   * @param <T>
   */
  class Base<
      M extends Matcher<M, OIN, T>,
      OIN,
      T> implements
      Matcher<M, OIN, T>,
      Function<M, Predicate<T>> {
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

    private final Supplier<Predicate<OIN>> rootPredicateSupplier;
    private final OIN                      rootValue;


    private JunctionType junctionType;

    private final List<Function<M, Predicate<? super T>>> childPredicates = new LinkedList<>();

    protected Base(OIN rootValue, Supplier<Predicate<OIN>> rootPredicateSupplier) {
      this.rootValue = rootValue;
      this.rootPredicateSupplier = rootPredicateSupplier;
      this.junctionType(JunctionType.CONJUNCTION);
    }

    @Override
    public M appendChild(Function<M, Predicate<? super T>> child) {
      this.childPredicates.add(requireNonNull(child));
      return me();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Predicate<T> connectChildPredicates() {
      Predicate<T> ret;
      requireState(this, v -> !v.childPredicates.isEmpty(), (v) -> "No child has been added yet.: <" + v + ">");
      if (this.childPredicates.size() == 1)
        ret = (Predicate<T>) childPredicates.get(0).apply(me());
      else {
        ret = (Predicate<T>) this.junctionType.connect(this.childPredicates.stream().map(each -> each.apply(me())).collect(Collectors.toList()));
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
      return this.rootValue;
    }

    @Override
    public Predicate<OIN> statementPredicate() {
      return rootPredicateSupplier != null ?
          rootPredicateSupplier.get() :
          // This cast should be safe, because if the root predicate is missing, this object must be the root.
          (Predicate<OIN>) connectChildPredicates();
    }

    @Override
    public Predicate<T> apply(M m) {
      return connectChildPredicates();
    }

    @Override
    public boolean test(OIN oin) {
      return statementPredicate().test(oin);
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
