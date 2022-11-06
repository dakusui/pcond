package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Checker;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public interface ListChecker<OIN, E> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, List<E>>,
    Checker<ListChecker<OIN, E>, OIN, List<E>>,
    Matcher.ForList<OIN, E> {
  @Override
  ListChecker<OIN, E> create(String transformerName, Function<? super OIN, ? extends List<E>> function, Predicate<? super List<E>> predicate, OIN originalInputValue);

  default ListChecker<OIN, E> isEmpty() {
    return addPredicate(Predicates.isEmpty());
  }

  default ListChecker<OIN, E> contains(E element) {
    return addPredicate(Predicates.contains(element));
  }

  @SuppressWarnings("unchecked")
  default ListChecker<OIN, E> findElementsInOrderBy(Predicate<E>... predicates) {
    return this.findElementsInOrderBy(asList(predicates));
  }

  @SuppressWarnings("unchecked")
  default ListChecker<OIN, E> findElementsInOrderBy(List<Predicate<E>> predicates) {
    return addPredicate(Predicates.findElements(predicates.toArray(new Predicate[0])));
  }

  @SuppressWarnings("unchecked")
  default ListChecker<OIN, E> findElementsInOrder(E... elements) {
    return this.findElementsInOrderBy(
        Arrays.stream(elements)
            .map(v -> Printables.predicate("[" + v + "]", e -> Objects.equals(v, e)))
            .map(p -> (Predicate<E>) p)
            .collect(Collectors.toList()));
  }

  class Impl<OIN, E>
      extends Checker.Base<ListChecker<OIN, E>, OIN, List<E>>
      implements ListChecker<OIN, E> {
    public Impl(String transformerName, Function<? super OIN, ? extends List<E>> function, Predicate<? super List<E>> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public ListChecker<OIN, E> create(String transformerName, Function<? super OIN, ? extends List<E>> function, Predicate<? super List<E>> predicate, OIN originalInputValue) {
      return Checker.Factory.listChecker(transformerName, function, predicate, originalInputValue);
    }
  }
}
