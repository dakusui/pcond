package com.github.dakusui.pcond.core.fluent3;

import com.github.dakusui.pcond.core.fluent4.Matcher.JunctionType;
import com.github.dakusui.pcond.fluent.Statement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

  M check(Function<M, Predicate<? super T>> child);

  default M checkWithPredicate(Predicate<? super T> predicate) {
    requireNonNull(predicate);
    return this.check(m -> predicate);
  }

  Predicate<? super T> toPredicate();

  OIN rootValue();

  R root();

  @SuppressWarnings("unchecked")
  default Predicate<OIN> done() {
    return (Predicate<OIN>) root().clone().toPredicate();
  }

  boolean isRoot();

  Statement<OIN> toStatement();

  @Override
  default OIN statementValue() {
    return rootValue();
  }

  @SuppressWarnings("unchecked")
  @Override
  default Predicate<OIN> statementPredicate() {
    return (Predicate<OIN>)root().toPredicate();
  }

  M clone();

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
      Matcher<M, R, OIN, T> {
    private final R root;

    private final Supplier<OIN> rootValue;

    private JunctionType junctionType;

    private final List<Function<M, Predicate<? super T>>> childPredicates = new LinkedList<>();
    private       Predicate<T>                            builtPredicate;

    @SuppressWarnings("unchecked")
    protected Base(Supplier<OIN> rootValue, R root) {
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
    public M check(Function<M, Predicate<? super T>> child) {
      this.childPredicates.add(requireNonNull(child));
      return me();
    }

    @Override
    public Predicate<T> toPredicate() {
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
      return this.rootValue.get();
    }

    @Override
    public R root() {
      return this.root;
    }

    @Override
    public boolean isRoot() {
      return this == this.root;
    }

    @Override
    public Statement<OIN> toStatement() {
      if (isRootMatcher()) {
        return new Statement<OIN>() {
          @Override
          public OIN statementValue() {
            return rootValue();
          }

          @SuppressWarnings("unchecked")
          @Override
          public Predicate<OIN> statementPredicate() {
            return (Predicate<OIN>) root().toPredicate();
          }

        };
      }
      return root.toStatement();
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

    @Override
    public String toString() {
      return this.getClass().getSimpleName() + ":" + this.childPredicates;
    }

    @SuppressWarnings("unchecked")
    public M cloneEmpty() {
      return ((Base<M, R, OIN, T>) clone()).makeEmpty();
    }

    M makeEmpty() {
      this.childPredicates.clear();
      return me();
    }

    private boolean isRootMatcher() {
      return this == this.root;
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
