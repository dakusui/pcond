package com.github.dakusui.pcond.core.fluent4;

import com.github.dakusui.pcond.forms.Predicates;

import java.util.List;
import java.util.function.Predicate;

public interface Matcher<
    M extends Matcher<M, T, R>,
    T,
    R> {
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
