package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Checker;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.streamChecker;

public interface StreamChecker<OIN, E> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, Stream<E>>,
    Checker<StreamChecker<OIN, E>, OIN, Stream<E>>,
    Matcher.ForStream<OIN, E> {
  default StreamChecker<OIN, E> noneMatch(Predicate<E> p) {
    return this.addPredicate(Predicates.noneMatch(p));
  }

  default StreamChecker<OIN, E> anyMatch(Predicate<E> p) {
    return this.addPredicate(Predicates.anyMatch(p));
  }

  default StreamChecker<OIN, E> allMatch(Predicate<E> p) {
    return this.addPredicate(Predicates.allMatch(p));
  }

  class Impl<OIN, E>
      extends Checker.Base<StreamChecker<OIN, E>, OIN, Stream<E>>
      implements StreamChecker<OIN, E> {
    public Impl(String transformerName, Function<? super OIN, ? extends Stream<E>> function, Predicate<? super Stream<E>> predicate, OIN originalInputValue) {
      super(originalInputValue, transformerName, function, predicate);
    }

    @Override
    public StreamChecker<OIN, E> create(String transformerName, Function<? super OIN, ? extends Stream<E>> function, Predicate<? super Stream<E>> predicate, OIN originalInputValue) {
      return streamChecker(transformerName, function, predicate, originalInputValue);
    }
  }
}
