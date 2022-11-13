package com.github.dakusui.pcond.core.fluent3;

import com.github.dakusui.pcond.fluent.Statement;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalChecks.requireState;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public interface Matcher<
    M extends Matcher<M, R, OIN, T>,
    R extends Matcher<R, R, OIN, OIN>,
    OIN,
    T>
    extends
    Statement<OIN>,
    Cloneable {
  M allOf();

  M anyOf();

  M appendChild(Function<M, Predicate<? super T>> child);

  default M appendPredicateAsChild(Predicate<T> predicate) {
    requireNonNull(predicate);
    return this.appendChild(m -> predicate);
  }

  Predicate<T> builtPredicate();


  OIN rootValue();
  @Override
  default OIN statementValue() {
    return rootValue();
  }

  Predicate<OIN> statementPredicate();

  R root();

  M cloneEmpty();

  /**
   * @param <M>
   * @param <OIN>
   * @param <T>
   */
  abstract class Base<
      M extends Matcher<M, R, OIN, T>,
      R extends Matcher<R, R, OIN, OIN>,
      OIN,
      T> implements
      Matcher<M, R, OIN, T>,
      Function<M, Predicate<T>> {
    private final R root;

    private final OIN rootValue;

    private JunctionType junctionType;

    private final List<Function<M, Predicate<? super T>>> childPredicates = new LinkedList<>();
    private       Predicate<T>                            builtPredicate;
    private       Predicate<OIN>                          rootPredicate   = null;

    @SuppressWarnings("unchecked")
    protected Base(OIN rootValue, R root) {
      this.rootValue = rootValue;
      this.root = root == null ? (R) this : root;
      this.allOf();
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
    public M appendChild(Function<M, Predicate<? super T>> child) {
      this.childPredicates.add(requireNonNull(child));
      return me();
    }

    @Override
    public Predicate<T> builtPredicate() {
      if (this.builtPredicate == null)
        this.builtPredicate = buildPredicate();
      return this.builtPredicate;
    }

    @SuppressWarnings("unchecked")
    private Predicate<T> buildPredicate() {
      Predicate<T> ret;
      requireState(this, v -> !v.childPredicates.isEmpty(), (v) -> "No child has been added yet.: <" + v + ">");
      if (this.childPredicates.size() == 1)
        ret = (Predicate<T>) childPredicates.get(0).apply(cloneEmpty());
      else {
        ret = (Predicate<T>) this.junctionType.connect(
            new ArrayList<>(this.childPredicates)
                .stream()
                .map(each -> each.apply(cloneEmpty()))
                .collect(toList()));
      }
      return ret;
    }

    @Override
    public OIN rootValue() {
      return this.rootValue;
    }

    @Override
    public R root() {
      return this.root;
    }


    @Override
    public Predicate<OIN> statementPredicate() {
      return rootPredicate();
    }

    @SuppressWarnings("unchecked")
    private Predicate<OIN> createRootPredicate() {
      assert this == this.root;
      return (Predicate<OIN>) builtPredicate();
    }

    @Override
    public Predicate<T> apply(M m) {
      return builtPredicate();
    }

    @Override
    public boolean test(OIN oin) {
      return testValueWithRootPredicate(oin);
    }

    @SuppressWarnings("unchecked")
    @Override
    public M clone() {
      try {
        return (M) super.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }

    //@SuppressWarnings("unchecked")
    public M cloneEmpty() {
      return clone();
      //      return ((Base<M, OIN, T>) clone()).makeEmpty();
    }

    M makeEmpty() {
      this.childPredicates.clear();
      return me();
    }

    private boolean isRootMatcher() {
      boolean ret = this == this.root;
      assert ret || this.rootPredicate == null;
      return ret;
    }

    private Predicate<OIN> rootPredicate() {
      if (isRootMatcher())
        rootPredicate = createRootPredicate();
      return this.root().builtPredicate();
    }

    private boolean testValueWithRootPredicate(OIN oin) {
      return rootPredicate().test(oin);
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
