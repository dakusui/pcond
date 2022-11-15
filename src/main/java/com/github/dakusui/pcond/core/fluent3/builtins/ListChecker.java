package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.Checker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public interface ListChecker<
    R extends Matcher<R, R, OIN, OIN>, OIN,
    E
    > extends Checker<ListChecker<R, OIN, E>, R, OIN, List<E>> {
  default ListChecker<R, OIN, E> isEmpty() {
    return appendPredicateAsChild(Predicates.isEmpty());
  }

  default ListChecker<R, OIN, E> isNotEmpty() {
    return appendPredicateAsChild(Predicates.not(Predicates.isEmpty()));
  }

  default ListChecker<R, OIN, E> contains(E element) {
    return appendPredicateAsChild(Predicates.contains(element));
  }

  @SuppressWarnings("unchecked")
  default ListChecker<R, OIN, E> findElementsInOrderBy(Predicate<E>... predicates) {
    return this.findElementsInOrderBy(asList(predicates));
  }

  @SuppressWarnings("unchecked")
  default ListChecker<R, OIN, E> findElementsInOrderBy(List<Predicate<E>> predicates) {
    return appendPredicateAsChild(Predicates.findElements(predicates.toArray(new Predicate[0])));
  }

  @SuppressWarnings("unchecked")
  default ListChecker<R, OIN, E> findElementsInOrder(E... elements) {
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
      ListChecker<R, OIN, E>,
      R,
      OIN,
      List<E>>
      implements ListChecker<R, OIN, E> {
    protected Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }
  }
}
