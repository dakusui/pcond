package com.github.dakusui.pcond.core.fluent4;

import com.github.dakusui.pcond.forms.Predicates;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalChecks.requireState;
import static java.util.Objects.requireNonNull;

public interface Matcher<
    M extends Matcher<M, T, R>,
    T,
    R> {
  M allOf();

  M anyOf();

  abstract class Base<
      M extends Matcher<M, T, R>,
      T,
      R> implements Matcher<M, T, R> {
    private final List<Function<Transformer<?, ?, R, R>, Predicate<R>>> childPredicates = new LinkedList<>();
    private Matcher.JunctionType                                        junctionType;

    @Override
    public M allOf() {
      return junctionType(Matcher.JunctionType.CONJUNCTION);
    }

    @Override
    public M anyOf() {
      return junctionType(Matcher.JunctionType.DISJUNCTION);
    }

    @SuppressWarnings("unchecked")
    protected M me() {
      return (M) this;
    }

    private M junctionType(Matcher.JunctionType junctionType) {
      requireState(this, v -> childPredicates.isEmpty(), v -> "Child predicate(s) are already added.: <" + this + ">");
      this.junctionType = requireNonNull(junctionType);
      return me();
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

    public abstract <T> Predicate<T> connect(List<Predicate<T>> predicates);
  }
}
