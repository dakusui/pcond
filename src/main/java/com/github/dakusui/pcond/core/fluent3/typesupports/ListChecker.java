package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.Checker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public interface ListChecker<
    OIN,
    R extends Matcher<R, R, OIN, OIN>,
    E
    > extends Checker<ListChecker<OIN, R, E>, R, OIN, List<E>> {
  default ListChecker<OIN, R, E> isEmpty() {
    return appendPredicateAsChild(Predicates.isEmpty());
  }

  default ListChecker<OIN, R, E> isNotEmpty() {
    return appendPredicateAsChild(Predicates.not(Predicates.isEmpty()));
  }

  default ListChecker<OIN, R, E> contains(E element) {
    return appendPredicateAsChild(Predicates.contains(element));
  }

  @SuppressWarnings("unchecked")
  default ListChecker<OIN, R, E> findElementsInOrderBy(Predicate<E>... predicates) {
    return this.findElementsInOrderBy(asList(predicates));
  }

  @SuppressWarnings("unchecked")
  default ListChecker<OIN,R, E> findElementsInOrderBy(List<Predicate<E>> predicates) {
    return appendPredicateAsChild(Predicates.findElements(predicates.toArray(new Predicate[0])));
  }

  @SuppressWarnings("unchecked")
  default ListChecker<OIN, R, E> findElementsInOrder(E... elements) {
    return this.findElementsInOrderBy(
        (Predicate<E>) Arrays.stream(elements)
            .map(v -> Printables.predicate("[" + v + "]", e -> Objects.equals(v, e)))
            .map(p -> (Predicate<E>) p)
            .collect(Collectors.toList()));
  }

  class Impl<
      OIN,
      R extends Matcher<R, R, OIN, OIN>,
      E
      > extends Matcher.Base<
      ListChecker<OIN, R, E>,
      R,
      OIN,
      List<E>>
      implements ListChecker<OIN, R, E> {
    protected Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
