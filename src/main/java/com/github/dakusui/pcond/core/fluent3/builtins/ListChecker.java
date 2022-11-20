package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectChecker;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.dakusui.pcond.internals.InternalUtils.trivialIdentityFunction;
import static java.util.Arrays.asList;

public interface ListChecker<
    T,
    E
    > extends
    AbstractObjectChecker<
        ListChecker<T, E>,
        T,
        List<E>> {
  default ListChecker<T, E> isEmpty() {
    return checkWithPredicate(Predicates.isEmpty());
  }

  default ListChecker<T, E> isNotEmpty() {
    return checkWithPredicate(Predicates.not(Predicates.isEmpty()));
  }

  default ListChecker<T, E> contains(E element) {
    return checkWithPredicate(Predicates.contains(element));
  }

  @SuppressWarnings("unchecked")
  default ListChecker<T, E> findElementsInOrderBy(Predicate<E>... predicates) {
    return this.findElementsInOrderBy(asList(predicates));
  }

  @SuppressWarnings("unchecked")
  default ListChecker<T, E> findElementsInOrderBy(List<Predicate<E>> predicates) {
    return checkWithPredicate(Predicates.findElements(predicates.toArray(new Predicate[0])));
  }

  @SuppressWarnings("unchecked")
  default ListChecker<T, E> findElementsInOrder(E... elements) {
    return this.findElementsInOrderBy(
        (Predicate<E>) Arrays.stream(elements)
            .map(v -> Printables.predicate("[" + v + "]", e -> Objects.equals(v, e)))
            .map(p -> (Predicate<E>) p)
            .collect(Collectors.toList()));
  }

  class Impl<
      T,
      E
      > extends Base<
      ListChecker<T, E>,
      T,
      List<E>> implements
      ListChecker<T, E> {
    public Impl(Supplier<T> rootValue, Function<T, List<E>> transformFunction) {
      super(rootValue, transformFunction);
    }

    @Override
    protected ListChecker<List<E>, E> rebase() {
      return new ListChecker.Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
