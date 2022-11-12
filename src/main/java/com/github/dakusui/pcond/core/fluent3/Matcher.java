package com.github.dakusui.pcond.core.fluent3;

import com.github.dakusui.pcond.fluent.Statement;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.dakusui.pcond.internals.InternalChecks.requireState;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public interface Matcher<
    M extends Matcher<M, OIN, T>,
    OIN,
    T>
    extends Statement<OIN> {
  default M appendPredicateAsChild(Predicate<T> predicate) {
    requireNonNull(predicate);
    return this.appendChild(m -> predicate);
  }

  Predicate<T> predicateForCurrentType();

  M allOf();

  M anyOf();


  M appendChild(Function<M, Predicate<? super T>> child);

  OIN originalInputValue();

  @Override
  default OIN statementValue() {
    return originalInputValue();
  }

  abstract Predicate<OIN> statementPredicate();

  Matcher<?, OIN, OIN> root();

  /**
   * @param <M>
   * @param <OIN>
   * @param <T>
   */
  abstract class Base<
      M extends Matcher<M, OIN, T>,
      OIN,
      T> implements
      Matcher<M, OIN, T>,
      Function<M, Predicate<T>> {
    private final Matcher<?, OIN, OIN> root;

    private final OIN rootValue;


    private JunctionType junctionType;

    private final List<Function<M, Predicate<? super T>>> childPredicates = new LinkedList<>();
    private       Predicate<T>                            predicateForCurrentType;

    @SuppressWarnings("unchecked")
    protected Base(OIN rootValue, Matcher<?, OIN, OIN> root) {
      this.rootValue = rootValue;
      this.root = root == null ? (Matcher<?, OIN, OIN>) this : root;
      this.junctionType(JunctionType.CONJUNCTION);
    }

    @Override
    public M appendChild(Function<M, Predicate<? super T>> child) {
      this.childPredicates.add(requireNonNull(child));
      return me();
    }

    @Override
    public Predicate<T> predicateForCurrentType() {
      if (this.predicateForCurrentType == null)
        this.predicateForCurrentType = createPredicateForCurrentType();
      return this.predicateForCurrentType;
    }

    @SuppressWarnings("unchecked")
    private Predicate<T> createPredicateForCurrentType() {
      Predicate<T> ret;
      requireState(this, v -> !v.childPredicates.isEmpty(), (v) -> "No child has been added yet.: <" + v + ">");
      if (this.childPredicates.size() == 1)
        ret = (Predicate<T>) childPredicates.get(0).apply(me());
      else {
        ret = (Predicate<T>) this.junctionType.connect(
            new ArrayList<>(this.childPredicates)
                .stream()
                .map(each -> each.apply(me()))
                .collect(toList()));
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


    @SuppressWarnings("unchecked")
    @Override
    public Predicate<OIN> statementPredicate() {
      if (this == this.root)
        return (Predicate<OIN>) predicateForCurrentType();
      return this.root.statementPredicate();
    }

    @Override
    public Matcher<?, OIN, OIN> root() {
      return this.root;
    }

    @Override
    public Predicate<T> apply(M m) {
      return predicateForCurrentType();
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
}
